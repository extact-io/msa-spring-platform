package io.extact.msa.spring.platform.fw.exception.message;

import java.util.List;
import java.util.stream.Collectors;

public record ValidationErrorMessage(
        String errorReason,
        String errorMessage,
        List<MessageItem> messageItems) implements ErrorMessage {

    public String errorDetail() {
        return messageItems.stream()
                .map(item -> "[%s:%s]".formatted(item.fieldName(), item.message()))
                .collect(Collectors.joining(","));
    }

    public static record MessageItem(
            String fieldName,
            String message) {
    }
}
