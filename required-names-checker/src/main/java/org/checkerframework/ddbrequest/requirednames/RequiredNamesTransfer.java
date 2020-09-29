package org.checkerframework.ddbrequest.requirednames;

import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.common.accumulation.AccumulationTransfer;
import org.checkerframework.common.value.ValueAnnotatedTypeFactory;
import org.checkerframework.common.value.ValueChecker;
import org.checkerframework.common.value.ValueCheckerUtils;
import org.checkerframework.common.value.qual.StringVal;
import org.checkerframework.dataflow.analysis.TransferInput;
import org.checkerframework.dataflow.analysis.TransferResult;
import org.checkerframework.dataflow.cfg.node.MethodInvocationNode;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.ddbrequest.common.DDBUtils;
import org.checkerframework.framework.flow.CFAnalysis;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFValue;
import org.checkerframework.framework.type.AnnotatedTypeMirror;

/**
 * Transfer function for required names. Handles side-effecting the receiver of calls to expression
 * methods that induce a name requirement (the annotated type factory handles the return types of
 * such methods).
 */
public class RequiredNamesTransfer extends AccumulationTransfer {

  /** Business logic about the function of various parts of DynamoDB. */
  private final DDBUtils ddbUtils;

  public RequiredNamesTransfer(CFAnalysis analysis) {
    super(analysis);
    ddbUtils = new DDBUtils(analysis.getEnv());
  }

  @Override
  public TransferResult<CFValue, CFStore> visitMethodInvocation(
      MethodInvocationNode n, TransferInput<CFValue, CFStore> in) {
    TransferResult<CFValue, CFStore> result = super.visitMethodInvocation(n, in);
    if (ddbUtils.isExpressionMethodCall(n, analysis.getEnv())) {
      ValueAnnotatedTypeFactory valueATF =
          analysis.getTypeFactory().getTypeFactoryOfSubchecker(ValueChecker.class);
      Node firstParam = n.getArgument(0);
      AnnotatedTypeMirror valueType = valueATF.getAnnotatedType(firstParam.getTree());
      AnnotationMirror stringVal = valueType.getAnnotation(StringVal.class);
      if (stringVal != null) {
        List<String> values = ValueCheckerUtils.getValueOfAnnotationWithStringArgument(stringVal);
        if (values.size() == 1) {
          String value = values.get(0);
          List<String> newNames = ddbUtils.parseNames(value);
          Node receiver = n.getTarget().getReceiver();
          accumulate(receiver, result, newNames.toArray(new String[0]));
        }
      }
    }
    return super.visitMethodInvocation(n, in);
  }
}
