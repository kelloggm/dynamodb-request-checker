package org.checkerframework.ddbrequest.enforcechecks;

import java.util.LinkedHashSet;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.ddbrequest.definednames.DefinedNamesChecker;
import org.checkerframework.ddbrequest.definedvalues.DefinedValuesChecker;
import org.checkerframework.ddbrequest.requirednames.RequiredNamesChecker;
import org.checkerframework.ddbrequest.requiredvalues.RequiredValuesChecker;
import org.checkerframework.framework.qual.StubFiles;

/**
 * This checker uses a fake-ish type system to enforce that DDBRequirements and DDBDefinitions match
 * on anything annotated as @EnforceChecks.
 */
@StubFiles("ddb.astub")
public class EnforceChecksChecker extends BaseTypeChecker {
  @Override
  protected LinkedHashSet<Class<? extends BaseTypeChecker>> getImmediateSubcheckerClasses() {
    LinkedHashSet<Class<? extends BaseTypeChecker>> checkers =
        super.getImmediateSubcheckerClasses();
    checkers.add(DefinedValuesChecker.class);
    checkers.add(DefinedNamesChecker.class);
    checkers.add(RequiredValuesChecker.class);
    checkers.add(RequiredNamesChecker.class);
    return checkers;
  }
}
