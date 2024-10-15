package io.extact.msa.spring.platform.fw.exception.response;

import java.util.ArrayList;
import java.util.List;

import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor // for JSON Deserialize
@ToString
public class ValidationErrorMessage extends ErrorMessage {

    private List<ValidationErrorItem> errorItems;

    public ValidationErrorMessage(String errorReason, String errorMessage, List<ValidationErrorItem> errorItems) {
        super(errorReason, errorMessage);
        this.errorItems = errorItems;
    }

    public List<ValidationErrorItem> getErrorItems() {
        return new ArrayList<>(errorItems);
    }

    public void setErrorItems(List<ValidationErrorItem> errorItems) {
        this.errorItems = errorItems;
    }
}

