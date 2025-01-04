package io.extact.msa.spring.platform.fw.infrastructure.framework.validator;


import org.springframework.validation.Errors;
import org.springframework.validation.SimpleErrors;
import org.springframework.validation.SmartValidator;

import io.extact.msa.spring.platform.fw.domain.model.DomainModel;
import io.extact.msa.spring.platform.fw.domain.model.DomainValidator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpringDomainValidator implements DomainValidator {

    private final SmartValidator validator;
    private final Class<?> modelClass;

    @Override
    public void validateModel(DomainModel model, Object... groups) {

        Errors errors = new SimpleErrors(model);
        validator.validate(model, errors, groups);

        if (!errors.hasErrors()) {
            return;
        }

        System.out.println(errors.toString());
    }

    @Override
    public void validateProperty(String propName, Object value, Object... groups) {

        Errors errors = new SimpleErrors(value);
        validator.validateValue(modelClass, propName, value, errors, groups);;

        if (!errors.hasErrors()) {
            return;
        }

        System.out.println(errors.toString());
    }
}
