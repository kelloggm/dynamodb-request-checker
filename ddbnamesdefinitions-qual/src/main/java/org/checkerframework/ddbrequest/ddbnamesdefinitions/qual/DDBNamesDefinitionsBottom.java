package org.checkerframework.ddbrequest.ddbnamesdefinitions.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.SubtypeOf;

/**
 * A bottom annotation for the DDB value definitions type system. This annotation should never be
 * written by programmers, but is needed by the type hierarchy for completeness.
 */
@SubtypeOf(DDBNamesDefinitions.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
public @interface DDBNamesDefinitionsBottom {}
