package io.extact.msa.spring.platform.fw.persistence.file.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    public static void deleteDirectoryUnderFiles(String directoryPath) throws Exception {
        Path targetDir = Paths.get(directoryPath);
        Files.list(targetDir).forEach(FileUtils::deleteQuietly); // ファイル削除
        Files.deleteIfExists(targetDir); // ディレクトリ削除
    }

    /**
     * 指定されたリソースファイルを一時ディレクトリにコピーする。
     * <p>
     * @param resourcePath リソースファイル
     * @param resolver コピー先の一時ディレクトリが指定されたPathResolver
     * @return 一時ディレクトリにコピーされたファイルのパス
     * @throws IOException ファイル入出力エラーが発生した場合
     */
    public static Path copyResourceToRealPath(String resourcePath, FilePathResolver resolver) {
        String[] resourcePathNodes = resourcePath.split("/");
        String outputFileName = resourcePathNodes[resourcePathNodes.length - 1];
        return copyResourceToRealPath(resourcePath, resolver, outputFileName);
    }

    public static Path copyResourceToRealPath(String resourcePath, FilePathResolver resolver, String outputFileName) {
        try (InputStream in = FileOperator.class.getResourceAsStream("/" + resourcePath)) {
            if (!Files.exists(resolver.getBaseDir())) {
                Files.createDirectory(resolver.getBaseDir());
            }
            Path outputFilePath = resolver.resolve(outputFileName);
            Files.copy(in, outputFilePath);
            return outputFilePath;
        } catch (IOException e) {
            throw new IoSystemException(e);
        }
    }

    private static boolean deleteQuietly(Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new IoSystemException(e);
        }
    }
}
