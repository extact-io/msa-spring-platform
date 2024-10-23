package io.extact.msa.spring.platform.fw.infrastructure.persistence.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import org.springframework.core.env.Environment;

import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.io.FilePathResolver;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.io.FileUtils;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.io.IoSystemException;
import lombok.extern.slf4j.Slf4j;

/**
 * ApiTypeがFILEのBeanが存在する場合に初期データの投入処理を行う。
 */
@Slf4j
class PersistentFileInitializer {

    private static final String PROP_PREFIX = "rms.persistence.";

    private Environment env;

    PersistentFileInitializer(Environment environment) {
        this.env = environment;
    }

    /**
     * fileTypeがpermanentだがマスタデータ格納フォルダが存在しない場合に初期データを投入する。
     * <code>rms.persistence.{entity}.csv.type.permanent.init.data</code>で初期データフォルダが
     * 指定されている場合はそのデータを投入、指定されていない場合は
     * <code>rms.persistence.{entity}.csv.temporary.fileName</code>に指定されているリソース
     * ファイルを投入する。
     */
    void initPermanentDataIfAbsent(String entity) {

        if (env.getProperty(PROP_PREFIX + entity + ".csv.type", String.class).equals("temporary")) {
            return;
        }


        Path baseDir = Paths.get(env.getProperty(PROP_PREFIX + entity + ".csv.permanent.directory", String.class));
        FilePathResolver pathResolver = new FilePathResolver.FixedDirPathResolver(baseDir);
        if (Files.exists(pathResolver.getBaseDir())) {
            return;
        }

        // 初期データをデータ格納フォルダにコピー
        String initDataPath = env.getProperty(PROP_PREFIX + entity + ".csv.permanent.init-data", String.class);
        if (initDataPath != null) {
            Path from = Paths.get(initDataPath);
            RecursiveCopyCommand.from(from).to(pathResolver.getBaseDir()).copy();
            log.info("初期データを作成しました。" + from + "=>" + pathResolver.getBaseDir());
            return;
        }

        // csv.temporary.fileName.$1 のvalueに指定されているリソースファイルを読み込み
        // csv.permanent.fileName.$1 のvalueのファイル名でデータ格納フォルダに出力する
        String temporaryResourcePath = env.getProperty(PROP_PREFIX + entity + ".csv.temporary.resource");
        String permanetFileNamePath = env.getProperty(PROP_PREFIX + entity + ".csv.permanent.filename");

        FileUtils.copyResourceToRealPath(temporaryResourcePath, pathResolver, permanetFileNamePath);
        log.info("初期データを作成しました。${" + PROP_PREFIX + entity + ".csv.type.temporary.fileName}" + "=>"
                + pathResolver.getBaseDir());
    }

    /**
     * ディレクトリをfromからtoにコピーする。
     * サブディレクトリの中身も全てコピーする<br>
     * コピー先が存在する場合は例外を送出する<br>
     */
    static class RecursiveCopyCommand {

        private Path from;
        private Path to;

        private RecursiveCopyCommand() {
            // nop
        }

        public static RecursiveCopyCommand from(Path from) {
            RecursiveCopyCommand command = new RecursiveCopyCommand();
            command.from = from;
            return command;
        }

        public RecursiveCopyCommandFinsher to(Path to) {
            this.to = to;
            return new RecursiveCopyCommandFinsher();
        }

        public class RecursiveCopyCommandFinsher {

            public void copy() {

                //コピー元
                final Path fromPath = from;
                //コピー先
                final Path toPath = to;

                //FileVisitorの定義
                FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        //ディレクトリをコピーする
                        Files.copy(dir, toPath.resolve(fromPath.relativize(dir)), StandardCopyOption.COPY_ATTRIBUTES);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        //ファイルをコピーする
                        Files.copy(file, toPath.resolve(fromPath.relativize(file)), StandardCopyOption.COPY_ATTRIBUTES);
                        return FileVisitResult.CONTINUE;
                    }
                };

                //ファイルツリーを辿ってFileVisitorの動作をさせる
                try {
                    Files.walkFileTree(fromPath, visitor);
                } catch (IOException e) {
                    throw new IoSystemException(e);
                }
            }
        }
    }
}
