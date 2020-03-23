package org.checkerframework.ddbrequest.enforcechecks;

import org.checkerframework.ddbrequest.ddbdefinitions.DDBDefinitionsAnnotatedTypeFactory;
import org.checkerframework.ddbrequest.ddbdefinitions.DDBDefinitionsChecker;
import org.checkerframework.ddbrequest.ddbrequirements.DDBRequirementsAnnotatedTypeFactory;
import org.checkerframework.ddbrequest.ddbrequirements.DDBRequirementsChecker;
import org.checkerframework.ddbrequest.enforcechecks.qual.DoNotEnforceChecks;
import org.checkerframework.ddbrequest.enforcechecks.qual.EnforceChecks;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import org.checkerframework.checker.compilermsgs.qual.CompilerMessageKey;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.source.Result;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.TreeUtils;

import javax.lang.model.element.ElementKind;

/**
 * This visitor is overridden to avoid issuing argument.type.incompatible
 * errors when @DoNotEnforceChecks is compared to @EnforceChecks, and issuing
 * the appropriate ddb error instead if one is called for.
 *
 * This is accomplished by overriding the common assignment check.
 */
public class EnforceChecksVisitor extends BaseTypeVisitor<EnforceChecksAnnotatedTypeFactory> {
    public EnforceChecksVisitor(BaseTypeChecker checker) {
        super(checker);
    }

    @Override
    protected void commonAssignmentCheck(
            AnnotatedTypeMirror varType,
            AnnotatedTypeMirror valueType,
            Tree valueTree,
            @CompilerMessageKey String errorKey) {
        if (varType.hasAnnotation(EnforceChecks.class) && valueType.hasAnnotation(DoNotEnforceChecks.class)) {
            DDBDefinitionsAnnotatedTypeFactory definitionFactory =
                    atypeFactory.getTypeFactoryOfSubchecker(DDBDefinitionsChecker.class);
            DDBRequirementsAnnotatedTypeFactory requirementsFactory =
                    atypeFactory.getTypeFactoryOfSubchecker(DDBRequirementsChecker.class);
            EnforceChecksUtils.checkRequirementsAgainstDefinitions(valueTree, definitionFactory, requirementsFactory, checker);
        } else {
            super.commonAssignmentCheck(varType, valueType, valueTree, errorKey);
        }
    }

    @Override
    protected void checkMethodInvocability(
            AnnotatedTypeMirror.AnnotatedExecutableType method, MethodInvocationTree node) {
        // most of this code was copied from super, because for some reason this doesn't use the
        // common assignment check
        if (method.getReceiverType() == null) {
            // Static methods don't have a receiver.
            return;
        }
        if (method.getElement().getKind() == ElementKind.CONSTRUCTOR) {
            // TODO: Explicit "this()" calls of constructors have an implicit passed
            // from the enclosing constructor. We must not use the self type, but
            // instead should find a way to determine the receiver of the enclosing constructor.
            // rcv =
            // ((AnnotatedExecutableType)atypeFactory.getAnnotatedType(atypeFactory.getEnclosingMethod(node))).getReceiverType();
            return;
        }

        AnnotatedTypeMirror methodReceiver = method.getReceiverType().getErased();
        AnnotatedTypeMirror treeReceiver = methodReceiver.shallowCopy(false);
        AnnotatedTypeMirror rcv = atypeFactory.getReceiverType(node);

        treeReceiver.addAnnotations(rcv.getEffectiveAnnotations());

        if (!skipReceiverSubtypeCheck(node, methodReceiver, rcv)) {
            if (treeReceiver.hasAnnotation(DoNotEnforceChecks.class)
                    && methodReceiver.hasAnnotation(EnforceChecks.class)) {
                DDBDefinitionsAnnotatedTypeFactory definitionFactory =
                        atypeFactory.getTypeFactoryOfSubchecker(DDBDefinitionsChecker.class);
                DDBRequirementsAnnotatedTypeFactory requirementsFactory =
                        atypeFactory.getTypeFactoryOfSubchecker(DDBRequirementsChecker.class);
                EnforceChecksUtils.checkRequirementsAgainstDefinitions(
                        TreeUtils.getReceiverTree(node), node, definitionFactory, requirementsFactory, checker);
            } else {
                super.checkMethodInvocability(method, node);
            }
        }
    }
}
