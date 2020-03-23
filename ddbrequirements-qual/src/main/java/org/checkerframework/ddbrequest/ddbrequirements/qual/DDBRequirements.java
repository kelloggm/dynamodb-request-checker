package org.checkerframework.ddbrequest.ddbrequirements.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.SubtypeOf;

/**
 * An expression whose type has this annotation evaluates to some kind of DynamoDB request (for
 * example, a QueryRequest) that needs definitions of the names and values specified in this
 * annotation's fields. Calls to filterExpression, keyConditionExpression, and TODO any others add
 * to the annotation of this type on the given request. When the request is used, the names must all
 * be defined in the map passed to attributeExpressionNames (and similarly for the values).
 */
@SubtypeOf(DDBRequirementsTop.class)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface DDBRequirements {
  /**
   * The values that must be defined in this request. Values start with ":", but this map elides
   * that colon.
   */
  String[] values();

  /**
   * The names that must be defined in this request. Names start with "#", but this map elides that
   * hashtag.
   */
  String[] names();
}
