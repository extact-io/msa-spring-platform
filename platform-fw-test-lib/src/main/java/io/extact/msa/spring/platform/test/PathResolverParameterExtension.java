package io.extact.msa.spring.platform.test;

import java.lang.annotation.Inherited;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import io.extact.msa.spring.platform.fw.persistence.file.io.FilePathResolver;

/**
 * テストクラスのメソッド引数で{@link FilePathResolver}を指定可能するJUnit5拡張クラス実装。
 * {@link FilePathResolver}の実装には{@link FilePathResolver.TempDirPathResolver}インスタンスを返す。
 */
public class PathResolverParameterExtension implements ParameterResolver {
    /**
     * {@link Inherited}e
     */
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == FilePathResolver.class;
    }
    /**
     * {@link Inherited}e
     */
    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return new FilePathResolver.TempDirPathResolver();
    }
}
