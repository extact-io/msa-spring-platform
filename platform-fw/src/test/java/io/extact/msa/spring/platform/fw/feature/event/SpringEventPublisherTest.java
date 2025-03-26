package io.extact.msa.spring.platform.fw.feature.event;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;

import io.extact.msa.spring.platform.fw.application.event.ApplicationServiceEvent;
import io.extact.msa.spring.platform.fw.application.event.ApplicationServiceEventPublisher;
import io.extact.msa.spring.platform.fw.domain.event.DomainEvent;
import io.extact.msa.spring.platform.fw.domain.event.DomainEventPublisher;
import lombok.Getter;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class SpringEventPublisherTest {

    @Autowired
    private ApplicationServiceEventPublisher applicationEventPublisher;
    @Autowired
    private DomainEventPublisher domainEventPublisher;
    @Autowired
    private TestApplicationEventHander1 appHandler1;
    @Autowired
    private TestApplicationEventHander2 appHandler2;
    @Autowired
    private TestDomainEventHander domainHandler;

    @Configuration(proxyBeanMethods = false)
    @Import(EventPublisherConfig.class)
    static class TestConfig {

        @Bean
        TestApplicationEventHander1 testApplicationEventHander1() {
            return new TestApplicationEventHander1();
        }

        @Bean
        TestApplicationEventHander2 testApplicationEventHander2() {
            return new TestApplicationEventHander2();
        }

        @Bean
        TestDomainEventHander testDomainEventHander() {
            return new TestDomainEventHander();
        }
    }

    @BeforeEach
    void beforeEach() {
        this.appHandler1.clear();
        this.appHandler2.clear();
        this.domainHandler.clear();
    }

    @Test
    void testValidateModelForRecordField() {

        // given
        TestApplicationEvent1 event1 = new TestApplicationEvent1();
        TestApplicationEvent2 event2 = new TestApplicationEvent2();
        TestApplicationEvent4 event4 = new TestApplicationEvent4();
        TestDomainEvent domainEvent = new TestDomainEvent();

        // when
        applicationEventPublisher.publish(event1);
        applicationEventPublisher.publish(event2);
        applicationEventPublisher.publish(event4);
        domainEventPublisher.publish(List.of(domainEvent));

        // then
        assertThat(appHandler1.getEvent1()).isEqualTo(event1);
        assertThat(appHandler1.getEvent2()).isEqualTo(event2);
        assertThat(appHandler2.getEvent3()).isNull();
        assertThat(appHandler2.getEvent4()).isEqualTo(event4);
    }

    @Getter
    static class TestApplicationEventHander1 {

        private TestApplicationEvent1 event1;
        private TestApplicationEvent2 event2;

        @EventListener
        void handle(TestApplicationEvent1 event1) {
            this.event1 = event1;
        }
        @EventListener
        void handle(TestApplicationEvent2 event2) {
            this.event2 = event2;
        }
        void clear() {
            this.event1 = null;
            this.event2 = null;
        }
    }

    @Getter
    static class TestApplicationEventHander2 {

        private TestApplicationEvent3 event3;
        private TestApplicationEvent4 event4;

        @EventListener
        void handle(TestApplicationEvent3 event3) {
            this.event3 = event3;
        }
        @EventListener
        void handle(TestApplicationEvent4 event4) {
            this.event4 = event4;
        }
        void clear() {
            this.event3 = null;
            this.event4 = null;
        }
    }

    @Getter
    static class TestDomainEventHander {
        private TestDomainEvent event;
        @EventListener
        void handle(TestDomainEvent event) {
            this.event = event;
        }
        void clear() {
            this.event = null;
        }
    }

    static record TestApplicationEvent1(UUID uuid) implements ApplicationServiceEvent {
        TestApplicationEvent1() {
            this(UUID.randomUUID());
        }
    }
    static record TestApplicationEvent2(UUID uuid) implements ApplicationServiceEvent {
        TestApplicationEvent2() {
            this(UUID.randomUUID());
        }
    }
    static record TestApplicationEvent3(UUID uuid) implements ApplicationServiceEvent {
        TestApplicationEvent3() {
            this(UUID.randomUUID());
        }
    }
    static record TestApplicationEvent4(UUID uuid) implements ApplicationServiceEvent {
        TestApplicationEvent4() {
            this(UUID.randomUUID());
        }
    }
    static record TestDomainEvent(UUID uuid) implements DomainEvent {
        TestDomainEvent() {
            this(UUID.randomUUID());
        }
    }
}
