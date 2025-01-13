package io.extact.msa.spring.platform.fw.infrastructure.persistence.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import io.extact.msa.spring.platform.fw.domain.model.EntityModel;
import io.extact.msa.spring.platform.fw.domain.model.Identity;
import io.extact.msa.spring.platform.fw.domain.repository.GenericRepository;
import io.extact.msa.spring.platform.fw.domain.service.IdentityGenerator;
import io.extact.msa.spring.platform.fw.exception.RmsPersistenceException;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.io.FileOperator;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.io.IoSystemException;

public abstract class AbstractFileRepository<M extends EntityModel>
        implements GenericRepository<M>, IdentityGenerator, FileRepository, EnvironmentAware, InitializingBean {

    private final ReentrantLock lock = new ReentrantLock();

    private Environment env;

    private FileOperator fileOperator;
    private ModelArrayMapper<M> modelArrayMapper;


    // ----------------------------------------------------- constructor methods

    public AbstractFileRepository(FileOperator fileOperator, ModelArrayMapper<M> mapper) {
        this.fileOperator = fileOperator;
        this.modelArrayMapper = mapper;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    // Beanの初期化プロセス中に行われるコールバックのためInjectされたBeanを利用してはならない
    @Override
    public void afterPropertiesSet() throws Exception {

        String entity = getEntityName();

        lock.lock();
        try {
            PersistentFileInitializer initFile = new PersistentFileInitializer(env);
            initFile.initPermanentDataIfAbsent(entity);
        } finally {
            lock.unlock();
        }
    }

    // ----------------------------------------------------- implement methods

    @Override
    public Optional<M> find(Identity id) {
        return load().stream()
                .filter(items -> Integer.parseInt(items[0]) == id.id()) // numberはpos:0は共通
                .map(modelArrayMapper::toModel)
                .findFirst();
    }

    @Override
    public List<M> findAll() {
        return load().stream()
                .map(modelArrayMapper::toModel)
                .sorted()
                .toList();
    }

    @Override
    public void add(M model) {
        save(model.transform(modelArrayMapper::toArray));
    }

    public void update(M model) {
        AtomicBoolean replaced = new AtomicBoolean(false);
        List<String[]> lines = load().stream()
                .map(items -> {
                    if (items[0].equals(String.valueOf(model.getId().id()))) {
                        replaced.set(true);
                        return modelArrayMapper.toArray(model);
                    }
                    return items;
                })
                .toList();
        if (!replaced.get()) {
            throw new RmsPersistenceException("target does not exist for id:" + model.getId().id());
        }
        this.saveAll(lines);
    }

    public void delete(M model) {
        this.delete(model.getId().id());
    }

    @Override
    public int nextIdentity() {
        return getNextSequence();
    }

    @Override
    public Path getStoragePath() {
        return fileOperator.getFilePath();
    }


    // ----------------------------------------------------- specific methods

    protected ModelArrayMapper<M> getMapper() {
        return modelArrayMapper;
    }

    protected int getNextSequence() {
        return load().stream()
                .map(items -> Integer.parseInt(items[0]))
                .collect(Collectors.maxBy(Integer::compareTo))
                .orElse(0)
                + 1;
    }

    protected void delete(Integer id) {
        AtomicBoolean deleted = new AtomicBoolean(false);
        List<String[]> excludedData = load().stream()
                .filter(items -> {
                    if (Integer.parseInt(items[0]) == id) { // numberはpos:0は共通
                        deleted.set(true);
                        return false;
                    }
                    return true;
                })
                .toList();
        if (!deleted.get()) {
            throw new RmsPersistenceException("target does not exist for id:" + id);
        }
        saveAll(excludedData);
    }


    protected List<String[]> load() {
        try {
            List<String[]> dataList = new ArrayList<>();
            fileOperator.load(dataList);
            return dataList;
        } catch (IOException e) {
            throw new IoSystemException(e);
        }
    }

    protected void save(String[] arrayData) {
        try {
            fileOperator.save(arrayData);
        } catch (IOException e) {
            throw new IoSystemException(e);
        }
    }

    protected void saveAll(List<String[]> allData) {
        try {
            fileOperator.saveAll(allData);
        } catch (IOException e) {
            throw new IoSystemException(e);
        }
    }
}
