package org.checkerframework.ddbrequest.definednames;

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
import org.checkerframework.ddbrequest.definednames.qual.DefinedNames;
import org.checkerframework.ddbrequest.definednames.qual.DefinedNamesBottom;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;

/** The annotated type factory for the DDB Names Definitions Checker. */
public class DefinedNamesAnnotatedTypeFactory extends AccumulationAnnotatedTypeFactory {

  /** The business logic related to which DynamoDB operations to support. */
  private final DDBUtils ddbUtils;

  public DefinedNamesAnnotatedTypeFactory(BaseTypeChecker checker) {
    super(checker, DefinedNames.class, DefinedNamesBottom.class);
    ddbUtils = new DDBUtils(this.processingEnv);
    this.postInit();
  }

  @Override
  public TreeAnnotator createTreeAnnotator() {
    return new ListTreeAnnotator(
        new DDBNamesDefinitionsTreeAnnotator(this), super.createTreeAnnotator());
  }

  /** Creates a @DDBNamesDefinitions annotation from the given names and names. */
  public AnnotationMirror createDDBNamesDefinitions(Collection<String> names) {
    AnnotationBuilder builder = new AnnotationBuilder(processingEnv, DefinedNames.class);
    String[] valueArray = names.toArray(new String[0]);
    builder.setValue("value", valueArray);
    return builder.build();
  }

  /**
   * Defaults the return type of methods that set the expression attribute names to take the known
   * constant keys of the argument into account.
   */
  private class DDBNamesDefinitionsTreeAnnotator extends TreeAnnotator {
    DDBNamesDefinitionsTreeAnnotator(DefinedNamesAnnotatedTypeFactory atf) {
      super(atf);
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree tree, AnnotatedTypeMirror type) {
      if (ddbUtils.isNamesSetter(tree, processingEnv)) {
        Tree namesExpr = tree.getArguments().get(0);
        ConstantKeysAnnotatedTypeFactory ckatf =
            getTypeFactoryOfSubchecker(ConstantKeysChecker.class);
        AnnotatedTypeMirror ckType = ckatf.getAnnotatedType(namesExpr);
        AnnotationMirror ckAnno = ckType.getAnnotation(ConstantKeys.class);
        List<String> names =
            ckAnno == null
                ? new ArrayList<>()
                : AnnotationUtils.getElementValueArray(ckAnno, "value", String.class, true);
        type.replaceAnnotation(createDDBNamesDefinitions(names));
      }
      return super.visitMethodInvocation(tree, type);
    }
  }
}
