package org.checkerframework.ddbrequest.requirednames.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.DefaultQualifierInHierarchy;
import org.checkerframework.framework.qual.SubtypeOf;

/**
 * An expression whose type has this annotation evaluates to a DynamoDB request builder one of whose
 * expression strings include at least the contained expression attribute names.
 */
@SubtypeOf({})
@DefaultQualifierInHierarchy
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface RequiredNames {
  /** These names are definitely required (that is, must be defined) by this request. */
  String[] value() default {};
}
