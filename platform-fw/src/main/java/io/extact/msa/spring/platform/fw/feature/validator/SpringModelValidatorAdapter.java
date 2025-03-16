package io.extact.msa.spring.platform.fw.feature.validator;

import java.io.Serializable;
import java.util.function.Supplier;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;

import io.extact.msa.spring.platform.fw.domain.model.DomainModel;
import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.exception.RmsValidationException;
import io.extact.msa.spring.platform.fw.exception.message.ValidationErrorMessage;
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
            ValidationErrorMessage message = translator.from(
                    errors,
                    SpringModelValidatorAdapter.class.getSimpleName());
            throw new RmsValidationException(message);
        }
    }

    @Override
    public void validateField(DomainModel model, SerializableSupplier<Object> getter, Object... groups) {

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                model,
                model.getClass().getSimpleName());

        Object fieldValue = getter.get();
        String fieldName = ValidatorPropertyUtils.extractPropertyName(getter);

        validator.validateValue(
                model.getClass(),
                fieldName,
                fieldValue,
                errors,
                groups);

        // Rootのモデルからみてフィールドにエラーがなく、かつフィールドがDomainModelだった場合は
        // フィールドオブジェクトに対する@Validによる検証も実施してあげる
        if (!errors.hasErrors()) {
            errors = new BeanPropertyBindingResult(
                    model,
                    model.getClass().getSimpleName() + "." + fieldName);
            if (fieldValue instanceof DomainModel nestedModel) {
                validator.validate(nestedModel, errors, groups);
            }
        }

        if (errors.hasErrors()) {
            ValidationErrorMessage message = translator.from(
                    errors,
                    SpringModelValidatorAdapter.class.getSimpleName());
            throw new RmsValidationException(message);
        }

    }

    @FunctionalInterface
    public interface SerializableSupplier<T> extends Supplier<T>, Serializable {
    }
}
