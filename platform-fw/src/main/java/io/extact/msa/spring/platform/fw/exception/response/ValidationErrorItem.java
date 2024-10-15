package io.extact.msa.spring.platform.fw.exception.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor // for JSON Deserialize
@AllArgsConstructor
@Data
public class ValidationErrorItem {

    private String fieldName;
    private String message;
}
