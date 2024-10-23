package io.extact.msa.spring.platform.fw.infrastructure.persistence.file;

import java.nio.file.Path;

/**
 * ファイル固有なリポジトリ操作とデフォルト実装の定義
 *
 * @param <T> エンティティの型
 */
public interface FileRepository {

    /**
     * ファイルに永続化するエンティティ名。
     *
     * @return エンティティ名
     */
    String getEntityName();

    /**
     * 永続化ファイルのパスを取得する
     *
     * @return 永続化ファイルのパス
     */
    Path getStoragePath();
}
