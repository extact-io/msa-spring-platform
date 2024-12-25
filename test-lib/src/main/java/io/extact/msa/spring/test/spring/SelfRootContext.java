package io.extact.msa.spring.test.spring;

import org.springframework.context.annotation.Configuration;

/**
 * 指定されたテストクラス自身を起動コンテキストのルートにするコンフィグクラス。
 *
 * <code>@SpringBootApplication</code>を持つプロジェクトではテストの起動コンテキストを明示的に指定しない場合、
 * クラスパス上の<code>@SpringBootApplication</code>が検索され、コンポーネントスキャンが走ってしまう。<p>
 *
 * これは通常の結合テストを行う場合は便利であるが、あるBeanを単体で登録してテストしたい場合に困る。
 * これを回避するため、テストクラスにインナークラスで<code>@Configuration</code>を指定したコンフィグクラスを
 * 作成した場合、<code>@Configuration</code>がクラスパス上にあるためインナークラスであっても他のテストクラスから
 * 意図せずコンポーネントスキャンされてしまう。<p>
 *
 * コンポーネントスキャンの対象にならないようにと<code>@TestConfiguration</code>を使って定義した場合、今度は
 * コンテキストが未指定の扱いとなり<code>@SpringBootApplication</code>がスキャンされてしまう。<p>
 *
 * この問題を回避するため、このクラスではコンテキストとして指定しつつもないも読み込まない副作用のないコンフィグ
 * となっている。<code>@SpringBootApplication</code>が検索されないようにテストクラス自身をコンテキストの起点として、テストで
 * 必要なBeanを登録する場合はコード例のように@TestConfigurationを使ってBean登録する。@TestConfigurationは他のテスト
 * クラスからはスキャンされないため、他のテストクラスから完全に隔離してテスト用のBeanを登録することができる。
 *
 * <pre>
 * {@code
 *   @SpringBootTest(classes = { SelfRootContext.class, TestConfig.class })
 *   class FooTest {
 *
 *       private Repository repository;
 *
 *       @TestConfiguration(proxyBeanMethods = false)
 *       static class TestConfig {
 *           @Bean
 *           TestRepository testRepository() {
 *           ...
 * }
 * </pre>
 * classesにSelfRootContextを指定することでstaticインナークラスの自動スキャンは無効化されるため明示的にBean登録する
 * コンフィグクラスも明示的にclassesに指定する必要がある。
 */
@Configuration(proxyBeanMethods = false)
public class SelfRootContext {
}
