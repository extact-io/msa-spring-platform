package io.extact.msa.spring.platform.fw.persistence.file.io;

import static java.nio.file.StandardOpenOption.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import lombok.Cleanup;

/**
 * ファイル入出力クラス。
 */
public class FileOperator {

    /** ファイルパス */
    private Path filePath;

    // ----------------------------------------------------- constructor methods

    /**
     * コンストラクタ
     * <p>
     * @param csvFilePath ファイルパス
     */
    public FileOperator(Path csvFilePath) {
        this.filePath = csvFilePath;
    }

    // ----------------------------------------------------- public methods

    /**
     * ファイルパスを取得する。
     * <p>
     * @return ファイルパス
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * ファイルを読み込む。
     * <p>
     * @param dataList 読み込んだデータを埋めて返す(in/out)
     * @return 読み込み件数
     * @throws IOException 読み込みエラーが発生した場合
     */
    public int load(List<String[]> dataList) throws IOException {
        @Cleanup CSVParser parser = CSVParser.parse(filePath, StandardCharsets.UTF_8, CSVFormat.RFC4180);
        parser.getRecords().stream()
                .map(record -> StreamSupport.stream(record.spliterator(), false).toList())
                .map(values -> {
                    var array = new String[values.size()];
                    values.toArray(array);
                    return array;
                })
                .forEach(dataList::add);
        return dataList.size();
    }

    /**
     * ファイルに書き込む。
     * <p>
     * @param targetData 書き込みデータ
     * @throws IOException 読み込みエラーが発生した場合
     */
    public void save(String[] targetData) throws IOException {
        List<String> singleLine = new ArrayList<>();
        singleLine.add(CSVFormat.RFC4180.format((Object[])targetData));
        Files.write(filePath, singleLine, StandardCharsets.UTF_8, WRITE, APPEND);
    }

    /**
     * 全件をファイルに書き込む。
     * ファイルに既にあるデータは削除される。
     *
     * @param allData 書き込みデータ
     * @throws IOException 読み込みエラーが発生した場合
     */
    public void saveAll(List<String[]> allData) throws IOException {
        Stream<CharSequence> allLines = allData.stream()
                .map(items -> CSVFormat.RFC4180.format((Object[]) items)); // Memory-friendly and lazy stringification
        Files.write(filePath, allLines::iterator, StandardCharsets.UTF_8, CREATE, TRUNCATE_EXISTING);
    }
}
