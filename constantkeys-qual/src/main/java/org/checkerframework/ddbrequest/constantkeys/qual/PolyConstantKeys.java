package org.checkerframework.ddbrequest.constantkeys.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.PolymorphicQualifier;

/**
 * A polymorphic qualifier for the Constant Keys type system.
 *
 * @checker_framework.manual #qualifier-polymorphism Qualifier polymorphism
 */
@PolymorphicQualifier(ConstantKeysTop.class)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface PolyConstantKeys {
}
