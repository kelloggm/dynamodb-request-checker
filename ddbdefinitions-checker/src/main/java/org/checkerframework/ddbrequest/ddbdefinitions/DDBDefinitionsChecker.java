package org.checkerframework.ddbrequest.ddbdefinitions;

import org.checkerframework.ddbrequest.constantkeys.ConstantKeysChecker;
import org.checkerframework.common.basetype.BaseTypeChecker;

import java.util.LinkedHashSet;

/**
 * A typechecker that determines expressions that have been defined in a Dynamo DB request.
 */
public class DDBDefinitionsChecker extends BaseTypeChecker {

    @Override
    protected LinkedHashSet<Class<? extends BaseTypeChecker>> getImmediateSubcheckerClasses() {
        LinkedHashSet<Class<? extends BaseTypeChecker>> checkers =
                super.getImmediateSubcheckerClasses();
        checkers.add(ConstantKeysChecker.class);
        return checkers;
    }
}
