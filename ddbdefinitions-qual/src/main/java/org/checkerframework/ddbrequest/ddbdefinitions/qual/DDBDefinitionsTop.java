package org.checkerframework.ddbrequest.ddbdefinitions.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.DefaultQualifierInHierarchy;
import org.checkerframework.framework.qual.SubtypeOf;

/**
 * A top annotation for the DDB definitions type hierarchy.
 *
 * <p>This annotation means that no information is known about what names and values have been
 * defined.
 */
@SubtypeOf({})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
@DefaultQualifierInHierarchy
public @interface DDBDefinitionsTop {}
