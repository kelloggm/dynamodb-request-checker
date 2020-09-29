package org.checkerframework.ddbrequest.ddbvaluesdefinitions.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.DefaultQualifierInHierarchy;
import org.checkerframework.framework.qual.SubtypeOf;

/**
 * An expression whose type has this annotation evaluates to a DynamoDB request builder that has
 * defined at least the values stored in the maps referenced by this annotation.
 */
@SubtypeOf({})
@DefaultQualifierInHierarchy
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface DDBValuesDefinitions {
  /**
   * The expression that evaluates to the map used by this request to set its attribute expression
   * values.
   */
  String[] value() default {};
}
