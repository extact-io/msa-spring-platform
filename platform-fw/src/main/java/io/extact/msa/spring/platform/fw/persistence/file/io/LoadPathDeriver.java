package io.extact.msa.spring.platform.fw.persistence.file.io;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;

/**
 * FileRepositoryでロードするファイルのパスを導出するクラス。
 */
@Slf4j
public class LoadPathDeriver {

    private final Environment env;

    public LoadPathDeriver(Environment env) {
        this.env = env;
    }

    public Path derive(String entity) {

        // fileType value is "permanent" or "temporary"
        String fileType = env.getProperty("rms.persistence.%s.csv.type".formatted(entity));

        // フィルパスの取得
        Path filePath = switch (fileType) {
            case "permanent" -> {
                String baseDir = env.getProperty("rms.persistence.%s.csv.permanent.directory".formatted(entity));
                String fileName = env.getProperty("rms.persistence.%s.csv.permanent.filename".formatted(entity));
                yield new FilePathResolver.FixedDirPathResolver(Paths.get(baseDir)).resolve(fileName);
            }
            case "temporary" -> {
                String resource = env.getProperty("rms.persistence.%s.csv.temporary.resource".formatted(entity));
                yield FileUtils.copyResourceToRealPath(resource, new FilePathResolver.TempDirPathResolver());
            }
            default -> throw new IllegalArgumentException("unknown fileType -> " + fileType);
        };

        log.info("[{}]モードのファイルオープンするパスを導出。PATH={}", fileType, filePath);
        return filePath;
    }
}
