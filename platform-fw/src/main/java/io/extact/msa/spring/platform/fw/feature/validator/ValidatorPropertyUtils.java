package io.extact.msa.spring.platform.fw.feature.validator;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Objects;

import io.extact.msa.spring.platform.fw.exception.RmsSystemException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ValidatorPropertyUtils {


    static String extractPropertyName(Serializable lambda) {
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

    static String getterToPropertyName(String getterName) {

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

}
