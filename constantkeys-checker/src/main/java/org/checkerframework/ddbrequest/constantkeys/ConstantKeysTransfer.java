package org.checkerframework.ddbrequest.constantkeys;

import java.util.Collections;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.common.value.ValueAnnotatedTypeFactory;
import org.checkerframework.common.value.ValueChecker;
import org.checkerframework.common.value.ValueCheckerUtils;
import org.checkerframework.common.value.qual.StringVal;
import org.checkerframework.dataflow.analysis.FlowExpressions;
import org.checkerframework.dataflow.analysis.TransferInput;
import org.checkerframework.dataflow.analysis.TransferResult;
import org.checkerframework.dataflow.cfg.node.MethodInvocationNode;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.framework.flow.CFAnalysis;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFTransfer;
import org.checkerframework.framework.flow.CFValue;
import org.checkerframework.framework.type.AnnotatedTypeMirror;

/** The transfer function, which handles inference of receiver types in calls to Map#put. */
public class ConstantKeysTransfer extends CFTransfer {

  private final ConstantKeysAnnotatedTypeFactory annotatedTypeFactory;

  public ConstantKeysTransfer(CFAnalysis analysis) {
    super(analysis);
    annotatedTypeFactory = (ConstantKeysAnnotatedTypeFactory) analysis.getTypeFactory();
  }

  /**
   * Based heavily on ObjectConstructionTransfer#visitMethodInvocation from
   * github.com/kelloggm/object-construction-checker. TODO: move this code into an accumulation
   * analysis abstract checker in the framework itself.
   */
  @Override
  public TransferResult<CFValue, CFStore> visitMethodInvocation(
      final MethodInvocationNode node, final TransferInput<CFValue, CFStore> input) {

    TransferResult<CFValue, CFStore> result = super.visitMethodInvocation(node, input);
    Node receiver = node.getTarget().getReceiver();

    // in the event that the method we're visiting is static
    if (receiver == null) {
      return result;
    }

    if (annotatedTypeFactory.mapUtils.isMapPuts(
        node.getTree(), annotatedTypeFactory.getProcessingEnv())) {
      ValueAnnotatedTypeFactory valueATF =
          annotatedTypeFactory.getTypeFactoryOfSubchecker(ValueChecker.class);
      Node firstParam = node.getArgument(0);
      AnnotatedTypeMirror valueType = valueATF.getAnnotatedType(firstParam.getTree());
      AnnotationMirror stringVal = valueType.getAnnotation(StringVal.class);
      if (stringVal != null) {
        List<String> values = ValueCheckerUtils.getValueOfAnnotationWithStringArgument(stringVal);
        if (values.size() == 1) {
          String value = values.get(0);
          AnnotatedTypeMirror receiverType = annotatedTypeFactory.getReceiverType(node.getTree());
          AnnotationMirror receiverAnno =
              receiverType.getAnnotationInHierarchy(annotatedTypeFactory.top);
          AnnotationMirror newReceiverAnno =
              annotatedTypeFactory
                  .getQualifierHierarchy()
                  .greatestLowerBound(
                      receiverAnno,
                      annotatedTypeFactory.createConstantKeys(Collections.singletonList(value)));
          // For some reason, visitMethodInvocation returns a conditional store. I think this is to
          // support conditional post-condition annotations, based on the comments in
          // CFAbstractTransfer.
          CFStore thenStore = result.getThenStore();
          CFStore elseStore = result.getElseStore();
          FlowExpressions.Receiver receiverReceiver =
              FlowExpressions.internalReprOf(annotatedTypeFactory, receiver);
          thenStore.insertValue(receiverReceiver, newReceiverAnno);
          elseStore.insertValue(receiverReceiver, newReceiverAnno);
        }
      }
    }
    return result;
  }
}
