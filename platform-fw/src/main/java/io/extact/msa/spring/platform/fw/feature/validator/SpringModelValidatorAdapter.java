package io.extact.msa.spring.platform.fw.feature.validator;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Supplier;

import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;

import io.extact.msa.spring.platform.fw.domain.model.DomainModel;
import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.exception.RmsSystemException;
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
        String fieldName = extractPropertyName(getter);

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

    private String extractPropertyName(Serializable lambda) {
        try {
            Method writeReplace = lambda.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(lambda);
            String getterName = serializedLambda.getImplMethodName();

            return getterToPropertyName(getterName);

        } catch (ReflectiveOperationException e) {
            throw new RmsSystemException("failed to extract method name.", e);
        }
    }

    private String getterToPropertyName(String getterName) {

        Objects.requireNonNull(getterName);

        String rawName;
        if (getterName.startsWith("get") && getterName.length() > 3) {
            rawName = getterName.substring(3);
        } else if (getterName.startsWith("is") && getterName.length() > 2) {
            rawName = getterName.substring(2);
        } else {
            throw new RmsSystemException("not a valid getter method name: " + getterName);
        }

        return Introspector.decapitalize(rawName);
    }

    public Object getFieldValue(Object target, String field) {
        // 可能なアクセスパスはJava Bean、つまりpublicなgetterアクセスのみ
        PropertyAccessor accessor = PropertyAccessorFactory.forBeanPropertyAccess(target);
        return accessor.getPropertyValue(field);
    }

    @FunctionalInterface
    public interface SerializableSupplier<T> extends Supplier<T>, Serializable {
    }

}
