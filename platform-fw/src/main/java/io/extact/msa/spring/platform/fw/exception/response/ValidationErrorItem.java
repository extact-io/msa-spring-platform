package io.extact.msa.spring.platform.fw.exception.response;

public record ValidationErrorItem(
        String fieldName,
        String message) {
}
