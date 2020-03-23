package org.checkerframework.ddbrequest.ddbrequirements;

import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.value.ValueChecker;

import java.util.LinkedHashSet;

/**
 * A typechecker that determines the expressions
 * that need to be defined in a Dynamo DB query.
 */
public class DDBRequirementsChecker extends BaseTypeChecker {

    @Override
    protected LinkedHashSet<Class<? extends BaseTypeChecker>> getImmediateSubcheckerClasses() {
        LinkedHashSet<Class<? extends BaseTypeChecker>> checkers =
                super.getImmediateSubcheckerClasses();
        checkers.add(ValueChecker.class);
        return checkers;
    }
}
