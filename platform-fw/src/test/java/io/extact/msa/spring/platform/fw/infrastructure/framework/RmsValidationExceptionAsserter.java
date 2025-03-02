package io.extact.msa.spring.platform.fw.infrastructure.framework;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;

import io.extact.msa.spring.platform.fw.exception.RmsValidationException;
import io.extact.msa.spring.platform.fw.exception.message.ValidationErrorMessage;
import io.extact.msa.spring.platform.fw.exception.message.ValidationErrorMessage.MessageItem;
import io.extact.msa.spring.platform.fw.feature.validator.SpringModelValidatorAdapter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RmsValidationExceptionAsserter {

    static String VALIDATION_ERROR_MESSAGE = "パラメーターエラーが発生しました";

    private final RmsValidationException e;

    public static RmsValidationExceptionAsserter asserterTo(RmsValidationException e) {
        return new RmsValidationExceptionAsserter(e);
    }

    public RmsValidationExceptionAsserter verifyMessageHeader() {
        assertThat(e).hasMessageContaining(VALIDATION_ERROR_MESSAGE);

        ValidationErrorMessage message = e.getErrorMessage();
        assertThat(message.errorReason()).isEqualTo(SpringModelValidatorAdapter.class.getSimpleName());
        assertThat(message.errorMessage()).isEqualTo(VALIDATION_ERROR_MESSAGE);

        return this;
    }

    public RmsValidationExceptionAsserter verifyItemOf(String fieldName, String errorMessage) {
        ValidationErrorMessage message = e.getErrorMessage();
        List<MessageItem> items = message.messageItems();
        assertThat(items)
                .containsExactlyInAnyOrderElementsOf(
                        List.of(new MessageItem(fieldName, errorMessage)));
        return this;
    }

    public RmsValidationExceptionAsserter verifyItemOf(Map<String, String> expectedMap) {

        List<MessageItem> expectItems = expectedMap.entrySet().stream()
                .map(entry -> new MessageItem(entry.getKey(), entry.getValue()))
                .toList();

        ValidationErrorMessage message = e.getErrorMessage();
        List<MessageItem> items = message.messageItems();

        assertThat(items).containsExactlyInAnyOrderElementsOf(expectItems);

        return this;
    }
}