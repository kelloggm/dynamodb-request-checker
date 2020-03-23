package org.checkerframework.ddbrequest.ddbdefinitions;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.ddbrequest.common.DDBUtils;
import org.checkerframework.ddbrequest.constantkeys.ConstantKeysAnnotatedTypeFactory;
import org.checkerframework.ddbrequest.constantkeys.ConstantKeysChecker;
import org.checkerframework.ddbrequest.constantkeys.qual.ConstantKeys;
import org.checkerframework.ddbrequest.ddbdefinitions.qual.DDBDefinitions;
import org.checkerframework.ddbrequest.ddbdefinitions.qual.DDBDefinitionsBottom;
import org.checkerframework.ddbrequest.ddbdefinitions.qual.DDBDefinitionsTop;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.framework.util.MultiGraphQualifierHierarchy;
import org.checkerframework.framework.util.MultiGraphQualifierHierarchy.MultiGraphFactory;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.javacutil.TreeUtils;

/** The annotated type factory for the DDB Requirements Checker. */
public class DDBDefinitionsAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {

  /** Canonical bottom annotation. */
  private final AnnotationMirror bottom =
      AnnotationBuilder.fromClass(elements, DDBDefinitionsBottom.class);

  /** Canonical top annotation. */
  public final AnnotationMirror top =
      AnnotationBuilder.fromClass(elements, DDBDefinitionsTop.class);

  /** The business logic related to which Dynamo operations to support. */
  private final DDBUtils ddbUtils;

  public DDBDefinitionsAnnotatedTypeFactory(BaseTypeChecker checker) {
    super(checker);
    ddbUtils = new DDBUtils(this.processingEnv);
    this.postInit();
  }

  @Override
  protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
    return new LinkedHashSet<>(
        Arrays.asList(DDBDefinitionsTop.class, DDBDefinitionsBottom.class, DDBDefinitions.class));
  }

  @Override
  public TreeAnnotator createTreeAnnotator() {
    return new ListTreeAnnotator(
        new DDBDefinitionsTreeAnnotator(this), super.createTreeAnnotator());
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
      if (AnnotationUtils.areSameByClass(a1, DDBDefinitionsTop.class)) {
        return a2;
      } else if (AnnotationUtils.areSameByClass(a2, DDBDefinitionsTop.class)) {
        return a1;
      } else if (AnnotationUtils.areSameByClass(a1, DDBDefinitionsBottom.class)
          || AnnotationUtils.areSameByClass(a2, DDBDefinitionsBottom.class)) {
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
        return createDDBDefinitions(new HashSet<>(a1Names), new HashSet<>(a1Values));
      }
    }

    @Override
    public AnnotationMirror leastUpperBound(AnnotationMirror a1, AnnotationMirror a2) {
      if (AnnotationUtils.areSameByClass(a1, DDBDefinitionsBottom.class)) {
        return a2;
      } else if (AnnotationUtils.areSameByClass(a2, DDBDefinitionsBottom.class)) {
        return a1;
      } else if (AnnotationUtils.areSameByClass(a1, DDBDefinitionsTop.class)
          || AnnotationUtils.areSameByClass(a2, DDBDefinitionsTop.class)) {
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
        return createDDBDefinitions(a1Names, a1Values);
      }
    }

    @Override
    public boolean isSubtype(AnnotationMirror subtype, AnnotationMirror supertype) {
      if (AnnotationUtils.areSameByClass(subtype, DDBDefinitionsBottom.class)
          || AnnotationUtils.areSameByClass(supertype, DDBDefinitionsTop.class)) {
        return true;
      } else if (AnnotationUtils.areSameByClass(supertype, DDBDefinitionsBottom.class)
          || AnnotationUtils.areSameByClass(subtype, DDBDefinitionsTop.class)) {
        return false;
      } else {
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

  /** Creates a @DDBDefinitions annotation from the given names and values. */
  public AnnotationMirror createDDBDefinitions(
      Collection<String> names, Collection<String> values) {
    AnnotationBuilder builder = new AnnotationBuilder(processingEnv, DDBDefinitions.class);
    String[] nameArray = names.toArray(new String[0]);
    builder.setValue("names", nameArray);
    String[] valueArray = values.toArray(new String[0]);
    builder.setValue("values", valueArray);
    return builder.build();
  }

  private class DDBDefinitionsTreeAnnotator extends TreeAnnotator {
    DDBDefinitionsTreeAnnotator(DDBDefinitionsAnnotatedTypeFactory atf) {
      super(atf);
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree tree, AnnotatedTypeMirror type) {

      // In the Object Construction Checker, which this code is based on, this propagation
      // is guarded by a call into the Returns Receiver Checker. Here, however, we know
      // the relevant class in advance, so we can just check that. Since AWS SDK builders
      // return "this" for all method calls that return the builder type, it is sufficient
      // to check that: 1. the called method is defined in QueryResult.Builder, and 2. that
      // the return type of the method is also QueryResult.Builder.
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

        if (ddbUtils.isNamesSetter(tree, processingEnv)) {
          List<String> values =
              AnnotationUtils.areSame(receiverAnno, top)
                  ? new ArrayList<>()
                  : AnnotationUtils.getElementValueArray(
                      receiverAnno, "values", String.class, true);
          Tree namesExpr = tree.getArguments().get(0);
          ConstantKeysAnnotatedTypeFactory ckatf =
              getTypeFactoryOfSubchecker(ConstantKeysChecker.class);
          AnnotatedTypeMirror ckType = ckatf.getAnnotatedType(namesExpr);
          AnnotationMirror ckAnno = ckType.getAnnotation(ConstantKeys.class);
          List<String> names =
              ckAnno == null
                  ? new ArrayList<>()
                  : AnnotationUtils.getElementValueArray(ckAnno, "value", String.class, true);
          type.replaceAnnotation(createDDBDefinitions(names, values));
        } else if (ddbUtils.isValuesSetter(tree, processingEnv)) {
          List<String> names =
              AnnotationUtils.areSame(receiverAnno, top)
                  ? new ArrayList<>()
                  : AnnotationUtils.getElementValueArray(receiverAnno, "names", String.class, true);
          Tree valuesExpr = tree.getArguments().get(0);
          ConstantKeysAnnotatedTypeFactory ckatf =
              getTypeFactoryOfSubchecker(ConstantKeysChecker.class);
          AnnotatedTypeMirror ckType = ckatf.getAnnotatedType(valuesExpr);
          AnnotationMirror ckAnno = ckType.getAnnotation(ConstantKeys.class);
          List<String> values =
              ckAnno == null
                  ? new ArrayList<>()
                  : AnnotationUtils.getElementValueArray(ckAnno, "value", String.class, true);
          type.replaceAnnotation(createDDBDefinitions(names, values));
        } else {
          type.replaceAnnotation(receiverAnno);
        }
      }

      return super.visitMethodInvocation(tree, type);
    }
  }
}
