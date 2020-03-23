package org.checkerframework.ddbrequest.enforcechecks.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.DefaultQualifierInHierarchy;
import org.checkerframework.framework.qual.SubtypeOf;

/**
 * A top annotation for the check-enforcement hierarchy, which is a fake-ish type system used to
 * actually report errors. The subtyping relationship between this annotation and {@link
 * EnforceChecks} is detailed in the Javadoc for that annotation.
 */
@SubtypeOf({})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
@DefaultQualifierInHierarchy
public @interface DoNotEnforceChecks {}
