package org.checkerframework.ddbrequest.constantkeys.qual;

import org.checkerframework.framework.qual.DefaultQualifierInHierarchy;
import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A top annotation for the constant keys type hierarchy.
 *
 * This annotation means that no information is known about
 * which strings are keys to the map, or that the object
 * to whose type this is attached is not a map.
 */
@SubtypeOf({})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
@DefaultQualifierInHierarchy
public @interface ConstantKeysTop {
}
