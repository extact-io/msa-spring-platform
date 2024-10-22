package io.extact.msa.spring.platform.fw.persistence.jpa;

import java.sql.Connection;

import jakarta.persistence.EntityManager;

/**
 * EntityManagerが内包しているConnectionを抽出する。
 * コネクションの抽出方法はJPAの実装ごとに異なる。JPA実装に直接依存しないうように
 * このインターフェースを導出している。
 * また、DataSourceをインジェクションしてそこから取得する方法もあるが、接続先が
 * 複数存在する場合に問題があるため、リポジトリに割り当て済みのEntityManagerから
 * コネクションを取得するようにしている。
 */
public interface ConnectionExtractor {

    Connection extractFrom(EntityManager entityManager);
}
