package io.extact.msa.spring.platform.fw.infrastructure.framework.validator;


import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;

import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpringModelValidatorAdapter<M> implements ModelValidator<M> {

    private final SmartValidator validator;

    @Override
    public void validateModel(M model, Object... groups) {

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                model,
                model.getClass().getSimpleName());

        validator.validate(model, errors, groups);

        if (errors.hasErrors()) {
            System.out.println("★：" + errors.toString());
        }
    }

    @Override
    public void validateField(M model, String targetField, Object... groups) {

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
//        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(target.getClass(), field);
//        if (pd != null && pd.getReadMethod() != null) {
//            ReflectionUtils.makeAccessible(pd.getReadMethod());
//            return ReflectionUtils.invokeMethod(pd.getReadMethod(), target);
//        }
//
//        Field rawField = ReflectionUtils.findField(target.getClass(), field);
//        if (rawField != null) {
//            ReflectionUtils.makeAccessible(rawField);
//            return ReflectionUtils.getField(rawField, target);
//        }
//
//        throw new IllegalArgumentException("Cannot retrieve value for field '" + field +
//                "' - neither a getter method nor a raw field found");
    }
}
