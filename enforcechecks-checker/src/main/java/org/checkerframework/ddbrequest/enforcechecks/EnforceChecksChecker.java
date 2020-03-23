package org.checkerframework.ddbrequest.enforcechecks;

import org.checkerframework.ddbrequest.ddbdefinitions.DDBDefinitionsChecker;
import org.checkerframework.ddbrequest.ddbrequirements.DDBRequirementsChecker;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.qual.StubFiles;

import java.util.LinkedHashSet;

/**
 * This checker uses a fake-ish type
 * system to enforce that DDBRequirements
 * and DDBDefinitions match on anything
 * annotated as @EnforceChecks.
 */
@StubFiles("ddb.astub")
public class EnforceChecksChecker extends BaseTypeChecker {
    @Override
    protected LinkedHashSet<Class<? extends BaseTypeChecker>> getImmediateSubcheckerClasses() {
        LinkedHashSet<Class<? extends BaseTypeChecker>> checkers =
                super.getImmediateSubcheckerClasses();
        checkers.add(DDBDefinitionsChecker.class);
        checkers.add(DDBRequirementsChecker.class);
        return checkers;
    }
}
