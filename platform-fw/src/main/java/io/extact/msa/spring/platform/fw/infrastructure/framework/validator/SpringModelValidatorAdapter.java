package io.extact.msa.spring.platform.fw.infrastructure.framework.validator;


import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;

import io.extact.msa.spring.platform.fw.domain.model.DomainModel;
import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpringModelValidatorAdapter implements ModelValidator {

    private final SmartValidator validator;
    private final ValidationErrorTranslator translator;

    @Override
    public void validateModel(DomainModel model, Object... groups) {

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                model,
                model.getClass().getSimpleName());

        validator.validate(model, errors, groups);

        if (errors.hasErrors()) {
            System.out.println("★：" + errors.toString()); // TODO ここをTranslatorに置き換える
        }
    }

    @Override
    public void validateField(DomainModel model, String targetField, Object... groups) {

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                model,
                model.getClass().getSimpleName());

        validator.validateValue(
                model.getClass(),
                targetField,
                getFieldValue(model, targetField),
                errors,
                groups);

        if (errors.hasErrors()) {
            System.out.println("★：" + errors.toString());
        }
    }

    public Object getFieldValue(Object target, String field) {
        PropertyAccessor accessor = PropertyAccessorFactory.forBeanPropertyAccess(target);
        return accessor.getPropertyValue(field);
    }
}
