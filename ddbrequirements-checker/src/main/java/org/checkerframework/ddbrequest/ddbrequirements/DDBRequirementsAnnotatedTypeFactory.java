package org.checkerframework.ddbrequest.ddbrequirements;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.value.ValueAnnotatedTypeFactory;
import org.checkerframework.common.value.ValueChecker;
import org.checkerframework.common.value.ValueCheckerUtils;
import org.checkerframework.common.value.qual.StringVal;
import org.checkerframework.ddbrequest.common.DDBUtils;
import org.checkerframework.ddbrequest.ddbrequirements.qual.DDBRequirements;
import org.checkerframework.ddbrequest.ddbrequirements.qual.DDBRequirementsBottom;
import org.checkerframework.ddbrequest.ddbrequirements.qual.DDBRequirementsTop;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.framework.util.MultiGraphQualifierHierarchy;
import org.checkerframework.framework.util.MultiGraphQualifierHierarchy.MultiGraphFactory;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.javacutil.TreeUtils;

/** The annotated type factory for the Constant Keys Checker. */
public class DDBRequirementsAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {

  /** Canonical bottom annotation. */
  private final AnnotationMirror bottom =
      AnnotationBuilder.fromClass(elements, DDBRequirementsBottom.class);

  /** Canonical top annotation. */
  public final AnnotationMirror top =
      AnnotationBuilder.fromClass(elements, DDBRequirementsTop.class);

  /** The business logic for dynamo. */
  final DDBUtils ddbUtils;

  public DDBRequirementsAnnotatedTypeFactory(BaseTypeChecker checker) {
    super(checker);
    ddbUtils = new DDBUtils(getProcessingEnv());
    this.postInit();
  }

  @Override
  protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
    return new LinkedHashSet<>(
        Arrays.asList(
            DDBRequirementsBottom.class, DDBRequirementsTop.class, DDBRequirements.class));
  }

  @Override
  public TreeAnnotator createTreeAnnotator() {
    return new ListTreeAnnotator(
        new DDBRequirementsTreeAnnotator(this), super.createTreeAnnotator());
  }

  @Override
  public QualifierHierarchy createQualifierHierarchy(MultiGraphFactory factory) {
    return new DBBExpressionsQualifierHierarchy(factory);
  }

  private class DBBExpressionsQualifierHierarchy extends MultiGraphQualifierHierarchy {
    DBBExpressionsQualifierHierarchy(MultiGraphFactory factory) {
      super(factory);
    }

    @Override
    public AnnotationMirror greatestLowerBound(AnnotationMirror a1, AnnotationMirror a2) {
      if (AnnotationUtils.areSameByClass(a1, DDBRequirementsTop.class)) {
        return a2;
      } else if (AnnotationUtils.areSameByClass(a2, DDBRequirementsTop.class)) {
        return a1;
      } else if (AnnotationUtils.areSameByClass(a1, DDBRequirementsBottom.class)
          || AnnotationUtils.areSameByClass(a2, DDBRequirementsBottom.class)) {
        return bottom;
      } else {
        List<String> a1Values =
            AnnotationUtils.getElementValueArray(a1, "values", String.class, true);
        List<String> a1Names =
            AnnotationUtils.getElementValueArray(a1, "names", String.class, true);
        List<String> a2Values =
            AnnotationUtils.getElementValueArray(a2, "values", String.class, true);
        List<String> a2Names =
            AnnotationUtils.getElementValueArray(a2, "names", String.class, true);
        a1Values.addAll(a2Values);
        a1Names.addAll(a2Names);
        return createDDBRequirements(new HashSet<>(a1Names), new HashSet<>(a1Values));
      }
    }

    @Override
    public AnnotationMirror leastUpperBound(AnnotationMirror a1, AnnotationMirror a2) {
      if (AnnotationUtils.areSameByClass(a1, DDBRequirementsBottom.class)) {
        return a2;
      } else if (AnnotationUtils.areSameByClass(a2, DDBRequirementsBottom.class)) {
        return a1;
      } else if (AnnotationUtils.areSameByClass(a1, DDBRequirementsTop.class)
          || AnnotationUtils.areSameByClass(a2, DDBRequirementsTop.class)) {
        return top;
      } else {
        List<String> a1Values =
            AnnotationUtils.getElementValueArray(a1, "values", String.class, true);
        List<String> a1Names =
            AnnotationUtils.getElementValueArray(a1, "names", String.class, true);
        List<String> a2Values =
            AnnotationUtils.getElementValueArray(a2, "values", String.class, true);
        List<String> a2Names =
            AnnotationUtils.getElementValueArray(a2, "names", String.class, true);
        a1Values.retainAll(a2Values);
        a1Names.retainAll(a2Names);
        if (a1Names.isEmpty() && a1Values.isEmpty()) {
          return top;
        }
        return createDDBRequirements(a1Names, a1Values);
      }
    }

    @Override
    public boolean isSubtype(AnnotationMirror subtype, AnnotationMirror supertype) {
      if (AnnotationUtils.areSameByClass(subtype, DDBRequirementsBottom.class)
          || AnnotationUtils.areSameByClass(supertype, DDBRequirementsTop.class)) {
        return true;
      } else if (AnnotationUtils.areSameByClass(supertype, DDBRequirementsBottom.class)
          || AnnotationUtils.areSameByClass(subtype, DDBRequirementsTop.class)) {
        return false;
      } else {
        // both have values, so either return bottom
        List<String> subValues =
            AnnotationUtils.getElementValueArray(subtype, "values", String.class, true);
        List<String> subNames =
            AnnotationUtils.getElementValueArray(subtype, "names", String.class, true);
        List<String> superValues =
            AnnotationUtils.getElementValueArray(supertype, "values", String.class, true);
        List<String> superNames =
            AnnotationUtils.getElementValueArray(supertype, "names", String.class, true);
        return subValues.containsAll(superValues) && subNames.containsAll(superNames);
      }
    }
  }

  /** Creates a @DDBRequirements annotation from the given names and values. */
  public AnnotationMirror createDDBRequirements(
      Collection<String> names, Collection<String> values) {
    AnnotationBuilder builder = new AnnotationBuilder(processingEnv, DDBRequirements.class);
    String[] nameArray = names.toArray(new String[0]);
    builder.setValue("names", nameArray);
    String[] valueArray = values.toArray(new String[0]);
    builder.setValue("values", valueArray);
    return builder.build();
  }

  private class DDBRequirementsTreeAnnotator extends TreeAnnotator {
    DDBRequirementsTreeAnnotator(DDBRequirementsAnnotatedTypeFactory atf) {
      super(atf);
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree tree, AnnotatedTypeMirror type) {

      // In the Object Construction Checker, which this code is based on, this propagation
      // is guarded by a call into the Returns Receiver Checker. Here, however, we know
      // the relevant class in advance, so we can just check that. All that logic is
      // in DDBUtils#isKnownReturnsReceiverMethod; eventually, that should just be
      // replaced with a call to the ReturnsReceiverChecker (once this is OSS).
      if (ddbUtils.isKnownReturnsReceiverMethod(tree)) {
        // Fetch the current type of the receiver, or top if none exists
        ExpressionTree receiverTree = TreeUtils.getReceiverTree(tree.getMethodSelect());
        AnnotatedTypeMirror receiverType;
        AnnotationMirror receiverAnno = null;

        if (receiverTree != null) {
          receiverType = getAnnotatedType(receiverTree);
          if (receiverType != null) {
            receiverAnno = receiverType.getAnnotationInHierarchy(top);
          }
        }
        if (receiverAnno == null) {
          receiverAnno = top;
        }

        if (ddbUtils.isExpressionMethodCall(tree, processingEnv)) {
          ValueAnnotatedTypeFactory valueATF = getTypeFactoryOfSubchecker(ValueChecker.class);
          Tree firstParam = tree.getArguments().get(0);
          AnnotatedTypeMirror valueType = valueATF.getAnnotatedType(firstParam);
          AnnotationMirror stringVal = valueType.getAnnotation(StringVal.class);
          if (stringVal != null) {
            List<String> values =
                ValueCheckerUtils.getValueOfAnnotationWithStringArgument(stringVal);
            if (values.size() == 1) {
              String value = values.get(0);
              List<String> newValues = ddbUtils.parseValues(value);
              List<String> newNames = ddbUtils.parseNames(value);
              AnnotationMirror newReceiverAnno =
                  getQualifierHierarchy()
                      .greatestLowerBound(receiverAnno, createDDBRequirements(newNames, newValues));

              type.replaceAnnotation(newReceiverAnno);
            }
          }
        } else {
          type.replaceAnnotation(receiverAnno);
        }
      }

      return super.visitMethodInvocation(tree, type);
    }
  }
}
