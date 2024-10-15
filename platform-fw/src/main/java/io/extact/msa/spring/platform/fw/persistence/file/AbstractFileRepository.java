package io.extact.msa.spring.platform.fw.persistence.file;

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

import io.extact.msa.spring.platform.fw.domain.Identifiable;
import io.extact.msa.spring.platform.fw.domain.Transformable;
import io.extact.msa.spring.platform.fw.persistence.GenericRepository;
import io.extact.msa.spring.platform.fw.persistence.file.io.FileOperator;
import io.extact.msa.spring.platform.fw.persistence.file.io.IoSystemException;

public abstract class AbstractFileRepository<T extends Transformable & Identifiable>
        implements EnvironmentAware, InitializingBean, GenericRepository<T>, FileRepository {

    private final ReentrantLock lock = new ReentrantLock();

    private Environment env;

    private FileOperator fileOperator;
    private EntityArrayMapper<T> entityMapper;


    // ----------------------------------------------------- constructor methods

    public AbstractFileRepository(FileOperator fileOperator, EntityArrayMapper<T> entityMapper) {
        this.fileOperator = fileOperator;
        this.entityMapper = entityMapper;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    // Beanの初期化プロセス中に行われるコールバックのためInjectされたBeanを利用してはならない
    @Override
    public void afterPropertiesSet() throws Exception {

        String entity = getEntityName();
        if (!env.getProperty(ApiType.PROP_NAME.formatted(entity)).equals(ApiType.FILE)) {
            return;
        }

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
    public Optional<T> get(int id) {
        return load().stream()
                .filter(items -> Integer.parseInt(items[0]) == id) // numberはpos:0は共通
                .map(entityMapper::toEntity)
                .findFirst();
    }

    @Override
    public List<T> findAll() {
        return load().stream()
                .map(entityMapper::toEntity)
                .toList();
    }

    @Override
    public void add(T entity) {
        int nextSeq = this.getNextSequence();
        entity.setId(nextSeq);
        save(entity.transform(entityMapper::toArray));
    }

    public Optional<T> update(T entity) {
        AtomicBoolean replaced = new AtomicBoolean(false);
        List<String[]> lines = load().stream()
                .map(items -> {
                    if (items[0].equals(String.valueOf(entity.getId()))) {
                        replaced.set(true);
                        return getConverter().toArray(entity);
                    }
                    return items;
                })
                .toList();
        if (!replaced.get()) {
            return Optional.empty();
        }
        this.saveAll(lines);
        return Optional.of(entity);
    }

    public void delete(T entity) {
        this.delete(entity.getId());
    }

    @Override
    public Path getStoragePath() {
        return fileOperator.getFilePath();
    }


    // ----------------------------------------------------- specific methods

    public int getNextSequence() {
        return load().stream()
                .map(items -> Integer.parseInt(items[0]))
                .collect(Collectors.maxBy(Integer::compareTo))
                .orElse(0)
                + 1;
    }

    public void delete(Integer id) {
        List<String[]> excludedData = load().stream()
                .filter(items -> Integer.parseInt(items[0]) != id) // numberはpos:0は共通
                .toList();
        saveAll(excludedData);
    }

    protected EntityArrayMapper<T> getConverter() {
        return entityMapper;
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

    // ----------------------------------------------------- package private methods

    void save(String[] arrayData) {
        try {
            fileOperator.save(arrayData);
        } catch (IOException e) {
            throw new IoSystemException(e);
        }
    }

    void saveAll(List<String[]> allData) {
        try {
            fileOperator.saveAll(allData);
        } catch (IOException e) {
            throw new IoSystemException(e);
        }
    }
}
