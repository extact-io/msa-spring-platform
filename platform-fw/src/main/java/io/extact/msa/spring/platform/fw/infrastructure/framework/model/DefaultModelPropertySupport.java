package io.extact.msa.spring.platform.fw.infrastructure.framework.model;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import org.springframework.util.ReflectionUtils;

import io.extact.msa.spring.platform.fw.domain.model.EntityModel;
import io.extact.msa.spring.platform.fw.domain.model.ModelPropertySupport;
import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultModelPropertySupport implements ModelPropertySupport {

    private final Supplier<EntityModel> testModelCreator;
    private final ModelValidator validator;
    private final EntityModel updateModel;

    public void setPropertyWithValidation(String propertyName, Object newValue) {

        // 入力値のチェック
        EntityModel testModel = testModelCreator.get();
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