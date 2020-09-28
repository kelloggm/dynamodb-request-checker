package org.checkerframework.ddbrequest.constantkeys.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.DefaultQualifierInHierarchy;
import org.checkerframework.framework.qual.SubtypeOf;

/**
 * An expression whose type has this annotation evaluates to a value that is a map whose keys
 * include at least the constant Strings in the value() field.
 *
 * <p>Note that a non-empty value for this annotation implies that the map's key type is String or
 * CharSequence.
 *
 * <p>An empty value is the default type.
 */
@SubtypeOf({})
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@DefaultQualifierInHierarchy
public @interface ConstantKeys {
  /** The constants that are definitely keys for this map. */
  String[] value() default {};
}
