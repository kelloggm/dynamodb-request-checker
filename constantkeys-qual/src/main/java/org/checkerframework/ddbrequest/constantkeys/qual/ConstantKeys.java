package org.checkerframework.ddbrequest.constantkeys.qual;

import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * An expression whose type has this annotation evaluates to a value that is a map
 * whose keys include at least the constant Strings in the value() field.
 *
 * Note that a non-empty value for this annotation
 * implies that the map's key type is String or CharSequence.
 *
 * An empty value is equivalent to @ConstantKeysTop.
 */
@SubtypeOf(ConstantKeysTop.class)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface ConstantKeys {
    /**
     * The constants that are definitely keys for this map.
     */
    String[] value();
}
