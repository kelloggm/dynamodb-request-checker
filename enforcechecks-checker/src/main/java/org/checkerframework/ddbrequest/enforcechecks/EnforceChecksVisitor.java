package org.checkerframework.ddbrequest.enforcechecks;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import javax.lang.model.element.ElementKind;
import org.checkerframework.checker.compilermsgs.qual.CompilerMessageKey;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.ddbrequest.definednames.DefinedNamesAnnotatedTypeFactory;
import org.checkerframework.ddbrequest.definednames.DefinedNamesChecker;
import org.checkerframework.ddbrequest.definedvalues.DefinedValuesAnnotatedTypeFactory;
import org.checkerframework.ddbrequest.definedvalues.DefinedValuesChecker;
import org.checkerframework.ddbrequest.enforcechecks.qual.DoNotEnforceChecks;
import org.checkerframework.ddbrequest.enforcechecks.qual.EnforceChecks;
import org.checkerframework.ddbrequest.requirednames.RequiredNamesAnnotatedTypeFactory;
import org.checkerframework.ddbrequest.requirednames.RequiredNamesChecker;
import org.checkerframework.ddbrequest.requiredvalues.RequiredValuesAnnotatedTypeFactory;
import org.checkerframework.ddbrequest.requiredvalues.RequiredValuesChecker;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.TreeUtils;

/**
 * This visitor is overridden to avoid issuing argument.type.incompatible errors
 * when @DoNotEnforceChecks is compared to @EnforceChecks, and issuing the appropriate ddb error
 * instead if one is called for.
 *
 * <p>This is accomplished by overriding the common assignment check.
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
      @CompilerMessageKey String errorKey,
      Object... extraArgs) {
    if (varType.hasAnnotation(EnforceChecks.class)
        && valueType.hasAnnotation(DoNotEnforceChecks.class)) {
      DefinedValuesAnnotatedTypeFactory definedValuesFactory =
          atypeFactory.getTypeFactoryOfSubchecker(DefinedValuesChecker.class);
      DefinedNamesAnnotatedTypeFactory definedNamesFactory =
          atypeFactory.getTypeFactoryOfSubchecker(DefinedNamesChecker.class);
      RequiredValuesAnnotatedTypeFactory requiredValuesFactory =
          atypeFactory.getTypeFactoryOfSubchecker(RequiredValuesChecker.class);
      RequiredNamesAnnotatedTypeFactory requiredNamesFactory =
          atypeFactory.getTypeFactoryOfSubchecker(RequiredNamesChecker.class);
      EnforceChecksUtils.checkRequirementsAgainstDefinitions(
          valueTree,
          definedValuesFactory,
          definedNamesFactory,
          requiredValuesFactory,
          requiredNamesFactory,
          checker);
    } else {
      super.commonAssignmentCheck(varType, valueType, valueTree, errorKey, extraArgs);
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
        DefinedValuesAnnotatedTypeFactory definedValuesFactory =
            atypeFactory.getTypeFactoryOfSubchecker(DefinedValuesChecker.class);
        DefinedNamesAnnotatedTypeFactory definedNamesFactory =
            atypeFactory.getTypeFactoryOfSubchecker(DefinedNamesChecker.class);
        RequiredValuesAnnotatedTypeFactory requiredValuesFactory =
            atypeFactory.getTypeFactoryOfSubchecker(RequiredValuesChecker.class);
        RequiredNamesAnnotatedTypeFactory requiredNamesFactory =
            atypeFactory.getTypeFactoryOfSubchecker(RequiredNamesChecker.class);
        EnforceChecksUtils.checkRequirementsAgainstDefinitions(
            TreeUtils.getReceiverTree(node),
            node,
            definedValuesFactory,
            definedNamesFactory,
            requiredValuesFactory,
            requiredNamesFactory,
            checker);
      } else {
        super.checkMethodInvocability(method, node);
      }
    }
  }
}
