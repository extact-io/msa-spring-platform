package io.extact.msa.spring.platform.fw.test.utils;

import java.util.List;

import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.internal.ComparisonStrategy;
import org.assertj.core.internal.Objects;
import org.assertj.core.internal.StandardComparisonStrategy;

import io.extact.msa.spring.platform.fw.domain.model.IsEqualable;

public class IsEqualableAssert {


    // ----------------------------------------------------- using equals strategry

    private static final ComparisonStrategy IS_EQUAL_STRATEGY = new StandardComparisonStrategy() {
        
        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public boolean areEqual(Object actual, Object other) {
            if (actual == other) {
                return true;
            }
            if (actual == null || other == null) {
                return false;
            }
            if (!(actual instanceof IsEqualable) || !(other instanceof IsEqualable)) {
                return false;
            }
            
            IsEqualable left = (IsEqualable<?>) actual;
            IsEqualable right = (IsEqualable<?>) other;

            return left.isEqual(right);
        }
    };


    // ----------------------------------------------------- public methods

    public static ObjectAssert<IsEqualable<?>> assertThatByEqualable(IsEqualable<?> actual) {
        return new ObjectIsEqualableComparisonAssert(actual);
    }

    public static ListAssert<IsEqualable<?>> assertThatByEqualable(List<? extends IsEqualable<?>> actual) {
        return new ListIsEqualableComparisonAssert(actual);
    }


    // ----------------------------------------------------- inner classes

    public static class ObjectIsEqualableComparisonAssert extends ObjectAssert<IsEqualable<?>> {

        public ObjectIsEqualableComparisonAssert(IsEqualable<?> actual) {
            super(actual);
            this.objects = new Objects(IS_EQUAL_STRATEGY);
        }
    }

    public static class ListIsEqualableComparisonAssert extends ListAssert<IsEqualable<?>> {
        public ListIsEqualableComparisonAssert(List<? extends IsEqualable<?>> actual) {
            super(actual);
            this.usingComparisonStrategy(IS_EQUAL_STRATEGY);
        }
    }
}
