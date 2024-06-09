package io.extact.msa.spring.platform.fw.exception.webapi;

import java.util.ArrayList;
import java.util.List;

import io.extact.msa.spring.platform.fw.exception.RmsValidationException.ValidationErrorInfo;
import io.extact.msa.spring.platform.fw.exception.RmsValidationException.ValidationErrorItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor // for JSON Deserialize
public class ValidationErrorInfoImpl extends GenericErrorInfo implements ValidationErrorInfo {

    private List<ValidationErrorItemImpl> errorItems;

    public ValidationErrorInfoImpl(String errorReason, String errorMessage, List<ValidationErrorItemImpl> errorItems) {
        super(errorReason, errorMessage);
        this.errorItems = errorItems;
    }

    @Override
    public List<ValidationErrorItem> getErrorItems() {
        return new ArrayList<>(errorItems);
    }

    public void setErrorItems(List<ValidationErrorItemImpl> errorItems) {
        this.errorItems = errorItems;
    }


    // ----------------------------------------------------- inner classes

    @NoArgsConstructor // for JSON Deserialize
    @AllArgsConstructor
    @Getter @Setter
    public static class ValidationErrorItemImpl implements ValidationErrorItem {
        private String fieldName;
        private String message;
    }
}

