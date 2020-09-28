package org.checkerframework.ddbrequest.constantkeys;

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
import org.checkerframework.ddbrequest.common.MapUtils;
import org.checkerframework.framework.flow.CFAnalysis;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFValue;
import org.checkerframework.framework.type.AnnotatedTypeMirror;

/** The transfer function, which handles inference of receiver types in calls to Map#put. */
public class ConstantKeysTransfer extends AccumulationTransfer {

  /** This class holds the hard-coded business logic about interesting maps. */
  private final MapUtils mapUtils;

  public ConstantKeysTransfer(CFAnalysis analysis) {
    super(analysis);
    mapUtils = new MapUtils(analysis.getEnv());
  }

  @Override
  public TransferResult<CFValue, CFStore> visitMethodInvocation(
      final MethodInvocationNode node, final TransferInput<CFValue, CFStore> input) {

    TransferResult<CFValue, CFStore> result = super.visitMethodInvocation(node, input);
    Node receiver = node.getTarget().getReceiver();

    // in the event that the method we're visiting is static
    if (receiver == null) {
      return result;
    }

    // Handle methods that behave like Map#put.
    if (mapUtils.isMapPuts(node.getTree(), analysis.getEnv())) {
      ValueAnnotatedTypeFactory valueATF =
          analysis.getTypeFactory().getTypeFactoryOfSubchecker(ValueChecker.class);
      Node firstParam = node.getArgument(0);
      AnnotatedTypeMirror valueType = valueATF.getAnnotatedType(firstParam.getTree());
      AnnotationMirror stringVal = valueType.getAnnotation(StringVal.class);
      if (stringVal != null) {
        List<String> values = ValueCheckerUtils.getValueOfAnnotationWithStringArgument(stringVal);
        if (values.size() == 1) {
          String value = values.get(0);
          accumulate(receiver, result, value);
        }
      }
    }
    return result;
  }
}
