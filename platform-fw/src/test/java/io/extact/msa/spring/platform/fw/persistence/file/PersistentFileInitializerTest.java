package io.extact.msa.spring.platform.fw.persistence.file;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import io.extact.msa.spring.platform.fw.persistence.file.io.IoSystemException;
import io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.file.PersonFileRepositoryConfig;

/**
 * Spring起動時にPersistentFileInitializerにより行われる以下を確認するテスト。
 * ・PersistentFileInitializerによりApiTypeがFILEのBeanが存在する場合で、かつ<br>
 * ・fileTypeがpermanentだがマスタデータ格納フォルダが存在しない場合に初期データが投入される
 */
@TestPropertySource(properties = """
        rms.persistence.person.api-type=file
        rms.persistence.person.csv.type=permanent
        rms.persistence.person.csv.permanent.init-data=./target/test-classes/preparetor-test
        rms.persistence.person.csv.permanent.directory=./target/temp-integrationtest
        rms.persistence.person.csv.permanent.filename=person.csv
        """)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class PersistentFileInitializerTest {

    private static final String TEST_PERMANENT_DIR = "./target/temp-integrationtest";

    @Configuration(proxyBeanMethods = false)
    @Import(PersonFileRepositoryConfig.class)
    static class TestConfig {
        // NOP
    }

    @AfterAll
    static void teardownAfterAll() throws Exception {
        Path targetDir = Paths.get(TEST_PERMANENT_DIR);
        Files.list(targetDir).forEach(PersistentFileInitializerTest::deleteQuietly); // ファイル削除
        Files.deleteIfExists(targetDir); // ディレクトリ削除
    }

    @Test
    void testAssertCopyFiles() throws Exception {

        List<String> expected = List.of(
                "InitFilePreparatorExtensionTest1.txt",
                "InitFilePreparatorExtensionTest2.txt");

        List<String> fileNames = Files.list(Paths.get(TEST_PERMANENT_DIR))
                .map(path -> path.getFileName().toString())
                .toList();

        assertThat(fileNames).containsExactlyInAnyOrderElementsOf(expected);
    }

    static boolean deleteQuietly(Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new IoSystemException(e);
        }
    }
}
