package org.checkerframework.ddbrequest.constantkeys;

import java.util.LinkedHashSet;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.value.ValueChecker;

/** A typechecker that determines the constant strings that are keys into Maps. */
public class ConstantKeysChecker extends BaseTypeChecker {

  @Override
  protected LinkedHashSet<Class<? extends BaseTypeChecker>> getImmediateSubcheckerClasses() {
    LinkedHashSet<Class<? extends BaseTypeChecker>> checkers =
        super.getImmediateSubcheckerClasses();
    checkers.add(ValueChecker.class);
    return checkers;
  }
}
