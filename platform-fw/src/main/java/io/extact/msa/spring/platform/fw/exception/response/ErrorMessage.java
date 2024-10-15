package io.extact.msa.spring.platform.fw.exception.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ErrorMessage {

    private String errorReason;
    private String errorMessage;
}
