package org.checkerframework.ddbrequest.constantkeys;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import org.checkerframework.ddbrequest.common.MapUtils;
import org.checkerframework.ddbrequest.constantkeys.qual.ConstantKeys;
import org.checkerframework.ddbrequest.constantkeys.qual.ConstantKeysBottom;
import org.checkerframework.ddbrequest.constantkeys.qual.ConstantKeysTop;
import org.checkerframework.ddbrequest.constantkeys.qual.PolyConstantKeys;
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
public class ConstantKeysAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {

  /** Canonical bottom annotation. */
  private final AnnotationMirror bottom =
      AnnotationBuilder.fromClass(elements, ConstantKeysBottom.class);

  /** Canonical top annotation. */
  final AnnotationMirror top = AnnotationBuilder.fromClass(elements, ConstantKeysTop.class);

  /** Canonical poly annotation. */
  private final AnnotationMirror poly =
      AnnotationBuilder.fromClass(elements, PolyConstantKeys.class);

  /** This class holds the hard-coded business logic about interesting maps. */
  public final MapUtils mapUtils;

  public ConstantKeysAnnotatedTypeFactory(BaseTypeChecker checker) {
    super(checker);
    mapUtils = new MapUtils(this.processingEnv);
    this.postInit();
  }

  @Override
  protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
    return new LinkedHashSet<>(
        Arrays.asList(
            ConstantKeys.class,
            ConstantKeysTop.class,
            ConstantKeysBottom.class,
            PolyConstantKeys.class));
  }

  @Override
  public TreeAnnotator createTreeAnnotator() {
    return new ListTreeAnnotator(new ConstantKeysTreeAnnotator(this), super.createTreeAnnotator());
  }

  @Override
  public QualifierHierarchy createQualifierHierarchy(MultiGraphFactory factory) {
    return new ConstantKeysQualifierHierarchy(factory);
  }

  private class ConstantKeysQualifierHierarchy extends MultiGraphQualifierHierarchy {
    ConstantKeysQualifierHierarchy(MultiGraphFactory factory) {
      super(factory);
    }

    @Override
    public AnnotationMirror greatestLowerBound(AnnotationMirror a1, AnnotationMirror a2) {
      if (AnnotationUtils.areSameByClass(a1, ConstantKeysTop.class)) {
        return a2;
      } else if (AnnotationUtils.areSameByClass(a2, ConstantKeysTop.class)) {
        return a1;
      } else if (AnnotationUtils.areSameByClass(a1, ConstantKeysBottom.class)
          || AnnotationUtils.areSameByClass(a2, ConstantKeysBottom.class)) {
        return bottom;
      } else if (AnnotationUtils.areSameByClass(a1, PolyConstantKeys.class)
          || AnnotationUtils.areSameByClass(a2, PolyConstantKeys.class)) {
        return poly;
      } else {
        List<String> a1Val = ValueCheckerUtils.getValueOfAnnotationWithStringArgument(a1);
        List<String> a2Val = ValueCheckerUtils.getValueOfAnnotationWithStringArgument(a2);
        a1Val.addAll(a2Val);
        return createConstantKeys(new HashSet<>(a1Val));
      }
    }

    @Override
    public AnnotationMirror leastUpperBound(AnnotationMirror a1, AnnotationMirror a2) {
      if (AnnotationUtils.areSameByClass(a1, ConstantKeysBottom.class)) {
        return a2;
      } else if (AnnotationUtils.areSameByClass(a2, ConstantKeysBottom.class)) {
        return a1;
      } else if (AnnotationUtils.areSameByClass(a1, ConstantKeysTop.class)
          || AnnotationUtils.areSameByClass(a2, ConstantKeysTop.class)) {
        return top;
      } else if (AnnotationUtils.areSameByClass(a1, PolyConstantKeys.class)
          || AnnotationUtils.areSameByClass(a2, PolyConstantKeys.class)) {
        return poly;
      } else {
        List<String> a1Val = ValueCheckerUtils.getValueOfAnnotationWithStringArgument(a1);
        List<String> a2Val = ValueCheckerUtils.getValueOfAnnotationWithStringArgument(a2);
        a1Val.retainAll(a2Val);
        if (a1Val.isEmpty()) {
          return top;
        }
        return createConstantKeys(a1Val);
      }
    }

    @Override
    public boolean isSubtype(AnnotationMirror subtype, AnnotationMirror supertype) {
      if (AnnotationUtils.areSameByClass(subtype, ConstantKeysBottom.class)
          || AnnotationUtils.areSameByClass(supertype, ConstantKeysTop.class)) {
        return true;
      } else if (AnnotationUtils.areSameByClass(supertype, ConstantKeysBottom.class)
          || AnnotationUtils.areSameByClass(subtype, ConstantKeysTop.class)) {
        return false;
      } else if (AnnotationUtils.areSameByClass(subtype, PolyConstantKeys.class)) {
        return AnnotationUtils.areSameByClass(supertype, PolyConstantKeys.class);
      } else if (AnnotationUtils.areSameByClass(supertype, PolyConstantKeys.class)) {
        return AnnotationUtils.areSameByClass(subtype, PolyConstantKeys.class);
      } else {
        // both have values, so either return bottom
        List<String> subVals = ValueCheckerUtils.getValueOfAnnotationWithStringArgument(subtype);
        List<String> superVals =
            ValueCheckerUtils.getValueOfAnnotationWithStringArgument(supertype);
        return subVals.containsAll(superVals);
      }
    }
  }

  /**
   * Creates a @ConstantKeys annotation whose values are the given strings, from a collection such
   * as a list.
   */
  public AnnotationMirror createConstantKeys(Collection<String> exprs) {
    AnnotationBuilder builder = new AnnotationBuilder(processingEnv, ConstantKeys.class);
    String[] exprArray = exprs.toArray(new String[0]);
    builder.setValue("value", exprArray);
    return builder.build();
  }

  /**
   * This TreeAnnotator defaults the results of ImmutableMap.Builder and ImmutableMap.of calls to an
   * appropriate constant keys annotation.
   */
  private class ConstantKeysTreeAnnotator extends TreeAnnotator {
    ConstantKeysTreeAnnotator(ConstantKeysAnnotatedTypeFactory atf) {
      super(atf);
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree tree, AnnotatedTypeMirror type) {

      if (mapUtils.isImmutableMapOf(tree, processingEnv)) {
        List<String> oddParamValues = new ArrayList<>();
        ValueAnnotatedTypeFactory vatf = getTypeFactoryOfSubchecker(ValueChecker.class);
        for (int i = 0; i < tree.getArguments().size(); i += 2) {
          AnnotatedTypeMirror argType = vatf.getAnnotatedType(tree.getArguments().get(i));
          AnnotationMirror stringVal = argType.getAnnotation(StringVal.class);
          if (stringVal != null) {
            List<String> values =
                ValueCheckerUtils.getValueOfAnnotationWithStringArgument(stringVal);
            if (values.size() == 1) {
              String value = values.get(0);
              oddParamValues.add(value);
            }
          }
        }
        if (!oddParamValues.isEmpty()) {
          AnnotationMirror newType = createConstantKeys(oddParamValues);
          type.replaceAnnotation(newType);
        }
      }

      if (mapUtils.isFromImmutableMapBuilder(tree)) {
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

        if (mapUtils.isImmutableMapBuilderPut(tree, processingEnv)) {
          ValueAnnotatedTypeFactory valueATF = getTypeFactoryOfSubchecker(ValueChecker.class);
          Tree firstParam = tree.getArguments().get(0);
          AnnotatedTypeMirror valueType = valueATF.getAnnotatedType(firstParam);
          AnnotationMirror stringVal = valueType.getAnnotation(StringVal.class);
          if (stringVal != null) {
            List<String> values =
                ValueCheckerUtils.getValueOfAnnotationWithStringArgument(stringVal);
            if (values.size() == 1) {
              String value = values.get(0);
              AnnotationMirror newReceiverAnno =
                  getQualifierHierarchy()
                      .greatestLowerBound(
                          receiverAnno, createConstantKeys(Collections.singletonList(value)));

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
