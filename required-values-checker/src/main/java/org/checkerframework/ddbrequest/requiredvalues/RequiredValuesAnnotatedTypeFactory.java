package org.checkerframework.ddbrequest.requiredvalues;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import java.util.Collection;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.common.accumulation.AccumulationAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.value.ValueAnnotatedTypeFactory;
import org.checkerframework.common.value.ValueChecker;
import org.checkerframework.common.value.ValueCheckerUtils;
import org.checkerframework.common.value.qual.StringVal;
import org.checkerframework.ddbrequest.common.DDBUtils;
import org.checkerframework.ddbrequest.requiredvalues.qual.RequiredValues;
import org.checkerframework.ddbrequest.requiredvalues.qual.RequiredValuesBottom;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.javacutil.AnnotationBuilder;

/** The annotated type factory for the Required Values Checker. */
public class RequiredValuesAnnotatedTypeFactory extends AccumulationAnnotatedTypeFactory {

  /** The business logic related to which DynamoDB operations to support. */
  private final DDBUtils ddbUtils;

  public RequiredValuesAnnotatedTypeFactory(BaseTypeChecker checker) {
    super(checker, RequiredValues.class, RequiredValuesBottom.class);
    ddbUtils = new DDBUtils(this.processingEnv);
    this.postInit();
  }

  @Override
  public TreeAnnotator createTreeAnnotator() {
    return new ListTreeAnnotator(
        new RequiredValuesTreeAnnotator(this), super.createTreeAnnotator());
  }

  /** Creates a @RequiredValues annotation from the given values. */
  public AnnotationMirror createRequiredValues(Collection<String> values) {
    AnnotationBuilder builder = new AnnotationBuilder(processingEnv, RequiredValues.class);
    String[] valueArray = values.toArray(new String[0]);
    builder.setValue("value", valueArray);
    return builder.build();
  }

  /**
   * Defaults the return type of methods that require expression attribute values to an appropriate
   * {@code RequiredValues} annotation.
   */
  private class RequiredValuesTreeAnnotator extends TreeAnnotator {
    RequiredValuesTreeAnnotator(RequiredValuesAnnotatedTypeFactory atf) {
      super(atf);
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree tree, AnnotatedTypeMirror type) {
      if (ddbUtils.isExpressionMethodCall(tree, processingEnv)) {
        ValueAnnotatedTypeFactory valueATF = getTypeFactoryOfSubchecker(ValueChecker.class);
        Tree firstParam = tree.getArguments().get(0);
        AnnotatedTypeMirror valueType = valueATF.getAnnotatedType(firstParam);
        AnnotationMirror stringVal = valueType.getAnnotation(StringVal.class);
        if (stringVal != null) {
          List<String> values = ValueCheckerUtils.getValueOfAnnotationWithStringArgument(stringVal);
          if (values.size() == 1) {
            String value = values.get(0);
            List<String> newValues = ddbUtils.parseValues(value);
            AnnotationMirror newAnno = createRequiredValues(newValues);
            type.replaceAnnotation(newAnno);
          }
        }
      }
      return super.visitMethodInvocation(tree, type);
    }
  }
}
