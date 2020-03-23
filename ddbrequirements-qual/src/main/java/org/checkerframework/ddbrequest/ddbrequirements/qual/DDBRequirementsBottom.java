package org.checkerframework.ddbrequest.ddbrequirements.qual;

import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A bottom annotation for the DDB requirements type system.
 * This annotation should never be written by programmers, but
 * is needed by the type hierarchy for completeness.
 *
 * If you see an error including this annotation, please
 * contact checkerframework-dev@ and provide the error message
 * and the code package you were running on.
 */
@SubtypeOf(DDBRequirements.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
public @interface DDBRequirementsBottom {
}
