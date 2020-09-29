package org.checkerframework.ddbrequest.enforcechecks.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.SubtypeOf;

/**
 * A special annotation indicating the type to which it is attached should have its ddb requirements
 * checked against its ddb definitions.
 *
 * <p>This type is a subtype of @DoNotEnforceChecks in the standard way.
 * However, @DoNotEnforceChecks is a subtype of this type iff every name and value in the {@code
 * RequiredValues} and {@code RequiredNames} types are defined by the {@code RequiredValues}
 * and {@code RequiredValues} types, respectively.
 *
 * <p>This annotation exists to permit writing specifications for DDB APIs in stub files. For
 * example, to enforce that at a call to build() in the V2 API, the QueryRequest has been correctly
 * constructed:
 *
 * <p>{@code QueryRequest build(QueryRequest.@EnforceChecks Builder this) { ... } }
 */
@SubtypeOf(DoNotEnforceChecks.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
public @interface EnforceChecks {}
