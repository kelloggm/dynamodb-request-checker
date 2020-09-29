package org.checkerframework.ddbrequest.ddbvaluesdefinitions;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.common.accumulation.AccumulationAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.ddbrequest.common.DDBUtils;
import org.checkerframework.ddbrequest.constantkeys.ConstantKeysAnnotatedTypeFactory;
import org.checkerframework.ddbrequest.constantkeys.ConstantKeysChecker;
import org.checkerframework.ddbrequest.constantkeys.qual.ConstantKeys;
import org.checkerframework.ddbrequest.ddbvaluesdefinitions.qual.DDBValuesDefinitions;
import org.checkerframework.ddbrequest.ddbvaluesdefinitions.qual.DDBValuesDefinitionsBottom;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;

/** The annotated type factory for the DDB Values Definitions Checker. */
public class DDBValuesDefinitionsAnnotatedTypeFactory extends AccumulationAnnotatedTypeFactory {

  /** The business logic related to which DynamoDB operations to support. */
  private final DDBUtils ddbUtils;

  public DDBValuesDefinitionsAnnotatedTypeFactory(BaseTypeChecker checker) {
    super(checker, DDBValuesDefinitions.class, DDBValuesDefinitionsBottom.class);
    ddbUtils = new DDBUtils(this.processingEnv);
    this.postInit();
  }

  @Override
  public TreeAnnotator createTreeAnnotator() {
    return new ListTreeAnnotator(
        new DDBValuesDefinitionsTreeAnnotator(this), super.createTreeAnnotator());
  }

  /** Creates a @DDBValuesDefinitions annotation from the given names and values. */
  public AnnotationMirror createDDBValuesDefinitions(Collection<String> values) {
    AnnotationBuilder builder = new AnnotationBuilder(processingEnv, DDBValuesDefinitions.class);
    String[] valueArray = values.toArray(new String[0]);
    builder.setValue("value", valueArray);
    return builder.build();
  }

  /**
   * Defaults the return type of methods that set the expression attribute values to take the known
   * constant keys of the argument into account.
   */
  private class DDBValuesDefinitionsTreeAnnotator extends TreeAnnotator {
    DDBValuesDefinitionsTreeAnnotator(DDBValuesDefinitionsAnnotatedTypeFactory atf) {
      super(atf);
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree tree, AnnotatedTypeMirror type) {
      if (ddbUtils.isValuesSetter(tree, processingEnv)) {
        Tree valuesExpr = tree.getArguments().get(0);
        ConstantKeysAnnotatedTypeFactory ckatf =
            getTypeFactoryOfSubchecker(ConstantKeysChecker.class);
        AnnotatedTypeMirror ckType = ckatf.getAnnotatedType(valuesExpr);
        AnnotationMirror ckAnno = ckType.getAnnotation(ConstantKeys.class);
        List<String> values =
            ckAnno == null
                ? new ArrayList<>()
                : AnnotationUtils.getElementValueArray(ckAnno, "value", String.class, true);
        type.replaceAnnotation(createDDBValuesDefinitions(values));
      }
      return super.visitMethodInvocation(tree, type);
    }
  }
}
