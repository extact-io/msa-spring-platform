package io.extact.msa.spring.platform.fw.infrastructure.framework.model;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import org.springframework.util.ReflectionUtils;

import io.extact.msa.spring.platform.fw.domain.model.DomainModel;
import io.extact.msa.spring.platform.fw.domain.model.ModelPropertySupport;
import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultModelSetterSupport<M extends DomainModel> implements ModelPropertySupport {

    private final Supplier<M> creator;
    private final ModelValidator validator;
    private final M updateModel;

    public void setPropertyWithValidation(String propertyName, Object newValue) {

        // 入力値のチェック
        M testModel = creator.get();
        setFieldValue(testModel, propertyName, newValue);
        validator.validateField(testModel, propertyName);

        // チェックOKの場合はターゲットモデルのプロパティを更新する
        setFieldValue(updateModel, propertyName, newValue);
    }

    private void setFieldValue(Object target, String fieldName, Object value) {

        Field field = ReflectionUtils.findField(target.getClass(), fieldName);
        if (field != null) {
            field.setAccessible(true);
            ReflectionUtils.setField(field, target, value);
        } else {
            throw new IllegalArgumentException("Field '" + fieldName + "' not found in " + target.getClass());
        }
   }
}