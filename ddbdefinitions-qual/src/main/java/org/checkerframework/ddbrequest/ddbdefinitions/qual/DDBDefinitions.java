package org.checkerframework.ddbrequest.ddbdefinitions.qual;

import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * An expression whose type has this annotation evaluates to a DynamoDB request
 * builder that requires the given values and names to be set.
 */
@SubtypeOf(DDBDefinitionsTop.class)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface DDBDefinitions {
    /**
     * The expression that evaluates to the map used by this request to
     * set its attribute expression values.
     */
    String[] values();

    /**
     * The expression that evaluates to the map used by this request to
     * set its attribute expression names.
     */
    String[] names();
}
