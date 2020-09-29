package org.checkerframework.ddbrequest.requirednames;

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
import org.checkerframework.ddbrequest.requirednames.qual.RequiredNamesBottom;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.javacutil.AnnotationBuilder;

/** The annotated type factory for the DDB Names Definitions Checker. */
public class RequiredNamesAnnotatedTypeFactory extends AccumulationAnnotatedTypeFactory {

  /** The business logic related to which DynamoDB operations to support. */
  private final DDBUtils ddbUtils;

  public RequiredNamesAnnotatedTypeFactory(BaseTypeChecker checker) {
    super(
        checker,
        org.checkerframework.ddbrequest.requirednames.qual.RequiredNames.class,
        RequiredNamesBottom.class);
    ddbUtils = new DDBUtils(this.processingEnv);
    this.postInit();
  }

  @Override
  public TreeAnnotator createTreeAnnotator() {
    return new ListTreeAnnotator(new RequiredNamesTreeAnnotator(this), super.createTreeAnnotator());
  }

  /** Creates a @RequiredNames annotation from the given names. */
  public AnnotationMirror createRequiredNames(Collection<String> names) {
    AnnotationBuilder builder =
        new AnnotationBuilder(
            processingEnv, org.checkerframework.ddbrequest.requirednames.qual.RequiredNames.class);
    String[] valueArray = names.toArray(new String[0]);
    builder.setValue("value", valueArray);
    return builder.build();
  }

  /**
   * Defaults the return type of methods that require expression attribute names to an appropriate
   * {@code RequiredNames} annotation.
   */
  private class RequiredNamesTreeAnnotator extends TreeAnnotator {
    RequiredNamesTreeAnnotator(RequiredNamesAnnotatedTypeFactory atf) {
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
            List<String> newNames = ddbUtils.parseNames(value);
            AnnotationMirror newAnno = createRequiredNames(newNames);
            type.replaceAnnotation(newAnno);
          }
        }
      }
      return super.visitMethodInvocation(tree, type);
    }
  }
}
