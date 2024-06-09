package io.extact.msa.spring.platform.fw.exception.webapi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class GenericErrorInfo {
    private String errorReason;
    private String errorMessage;
}
