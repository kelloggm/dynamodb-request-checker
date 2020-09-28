package org.checkerframework.ddbrequest.constantkeys;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.common.accumulation.AccumulationAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.value.ValueAnnotatedTypeFactory;
import org.checkerframework.common.value.ValueChecker;
import org.checkerframework.common.value.ValueCheckerUtils;
import org.checkerframework.common.value.qual.StringVal;
import org.checkerframework.ddbrequest.common.MapUtils;
import org.checkerframework.ddbrequest.constantkeys.qual.ConstantKeys;
import org.checkerframework.ddbrequest.constantkeys.qual.ConstantKeysBottom;
import org.checkerframework.ddbrequest.constantkeys.qual.PolyConstantKeys;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.javacutil.AnnotationBuilder;

/** The annotated type factory for the Constant Keys Checker. */
public class ConstantKeysAnnotatedTypeFactory extends AccumulationAnnotatedTypeFactory {

  /** This class holds the hard-coded business logic about interesting maps. */
  private final MapUtils mapUtils;

  public ConstantKeysAnnotatedTypeFactory(BaseTypeChecker checker) {
    super(checker, ConstantKeys.class, ConstantKeysBottom.class);
    mapUtils = new MapUtils(getProcessingEnv());
    this.postInit();
  }

  @Override
  protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
    return new LinkedHashSet<>(
        Arrays.asList(ConstantKeys.class, ConstantKeysBottom.class, PolyConstantKeys.class));
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

  @Override
  public TreeAnnotator createTreeAnnotator() {
    return new ListTreeAnnotator(new ConstantKeysTreeAnnotator(this), super.createTreeAnnotator());
  }

  /**
   * This TreeAnnotator defaults the results of ImmutableMap.Builder and ImmutableMap.of calls to an
   * appropriate constant keys annotation.
   */
  private class ConstantKeysTreeAnnotator extends TreeAnnotator {
    public ConstantKeysTreeAnnotator(
        ConstantKeysAnnotatedTypeFactory constantKeysAnnotatedTypeFactory) {
      super(constantKeysAnnotatedTypeFactory);
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
              AnnotationMirror newAnno = createConstantKeys(Collections.singletonList(value));
              type.replaceAnnotation(newAnno);
            }
          }
        }
      }
      return super.visitMethodInvocation(tree, type);
    }
  }
}
