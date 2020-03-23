package org.checkerframework.ddbrequest.enforcechecks;

import org.checkerframework.ddbrequest.enforcechecks.qual.DoNotEnforceChecks;
import org.checkerframework.ddbrequest.enforcechecks.qual.EnforceChecks;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class only exists so that reflection resolution works as expected.
 */
public class EnforceChecksAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {
    public EnforceChecksAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker);
        this.postInit();
    }

    @Override
    protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
        return new LinkedHashSet<>(
                Arrays.asList(EnforceChecks.class, DoNotEnforceChecks.class));
    }
}
