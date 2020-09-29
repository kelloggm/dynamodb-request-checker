package org.checkerframework.ddbrequest.ddbnamesdefinitions;

import java.util.LinkedHashSet;
import org.checkerframework.common.accumulation.AccumulationChecker;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.ddbrequest.constantkeys.ConstantKeysChecker;

/**
 * A typechecker that determines the attribute names that have been defined in a Dynamo DB request.
 */
public class DDBNamesDefinitionsChecker extends AccumulationChecker {

  @Override
  protected LinkedHashSet<Class<? extends BaseTypeChecker>> getImmediateSubcheckerClasses() {
    LinkedHashSet<Class<? extends BaseTypeChecker>> checkers =
        super.getImmediateSubcheckerClasses();
    checkers.add(ConstantKeysChecker.class);
    return checkers;
  }
}
