package org.checkerframework.ddbrequest.ddbrequirements;

import org.checkerframework.common.value.ValueCheckerUtils;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import org.checkerframework.common.value.ValueAnnotatedTypeFactory;
import org.checkerframework.common.value.ValueChecker;
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

import javax.lang.model.element.AnnotationMirror;
import java.util.List;

/**
 * The transfer function, which handles inference of receiver types
 * in calls to Map#put.
 */
public class DDBRequirementsTransfer extends CFTransfer {

    private final DDBRequirementsAnnotatedTypeFactory annotatedTypeFactory;

    public DDBRequirementsTransfer(CFAnalysis analysis) {
        super(analysis);
        annotatedTypeFactory = (DDBRequirementsAnnotatedTypeFactory) analysis.getTypeFactory();
    }

    /**
     * Based heavily on ObjectConstructionTransfer#visitMethodInvocation
     * from github.com/kelloggm/object-construction-checker.
     */
    @Override
    public TransferResult<CFValue, CFStore> visitMethodInvocation(final MethodInvocationNode node,
                                                                  final TransferInput<CFValue, CFStore> input) {

        TransferResult<CFValue, CFStore> result = super.visitMethodInvocation(node, input);
        Node receiver = node.getTarget().getReceiver();

        // in the event that the method we're visiting is static
        if (receiver == null) {
            return result;
        }

        if (annotatedTypeFactory.ddbUtils.isExpressionMethodCall(node, annotatedTypeFactory.getProcessingEnv())) {
            ValueAnnotatedTypeFactory valueATF = annotatedTypeFactory.getTypeFactoryOfSubchecker(ValueChecker.class);
            Node firstParam = node.getArgument(0);
            AnnotatedTypeMirror valueType = valueATF.getAnnotatedType(firstParam.getTree());
            AnnotationMirror stringVal = valueType.getAnnotation(StringVal.class);
            if (stringVal != null) {
                List<String> values = ValueCheckerUtils.getValueOfAnnotationWithStringArgument(stringVal);
                if (values.size() == 1) {
                    String value = values.get(0);
                    List<String> newValues = annotatedTypeFactory.ddbUtils.parseValues(value);
                    List<String> newNames = annotatedTypeFactory.ddbUtils.parseNames(value);
                    AnnotatedTypeMirror receiverType = annotatedTypeFactory.getReceiverType(node.getTree());
                    AnnotationMirror receiverAnno = receiverType.getAnnotationInHierarchy(annotatedTypeFactory.top);
                    AnnotationMirror newReceiverAnno = annotatedTypeFactory.getQualifierHierarchy()
                                                                           .greatestLowerBound(receiverAnno,
                                                                                               annotatedTypeFactory.createDDBRequirements(newNames, newValues));
                    // For some reason, visitMethodInvocation returns a conditional store. I think this is to
                    // support conditional post-condition annotations, based on the comments in CFAbstractTransfer.
                    CFStore thenStore = result.getThenStore();
                    CFStore elseStore = result.getElseStore();

                    while (receiver != null) {
                        // Insert the new type computed previously as the type of the receiver.
                        FlowExpressions.Receiver receiverReceiver =
                            FlowExpressions.internalReprOf(annotatedTypeFactory, receiver);

                        thenStore.insertValue(receiverReceiver, newReceiverAnno);
                        elseStore.insertValue(receiverReceiver, newReceiverAnno);

                        Tree receiverTree = receiver.getTree();

                        // Possibly recurse: if the receiver is itself a method call,
                        // then we need to also propagate this new information to its receiver
                        // if the method being called has an @This return type.
                        //
                        // Note that we must check for null, because the tree could be
                        // implicit (when calling an instance method on the class itself).
                        // In that case, do not attempt to refine either - the receiver is
                        // not a method invocation, anyway.
                        if (receiverTree == null || receiverTree.getKind() != Tree.Kind.METHOD_INVOCATION) {
                            // Do not continue, because the receiver isn't a method invocation itself. The
                            // end of the chain of calls has been reached.
                            break;
                        }

                        MethodInvocationTree receiverAsMethodInvocation = (MethodInvocationTree) receiver.getTree();

                        if (annotatedTypeFactory.ddbUtils.isExpressionMethodCall(receiverAsMethodInvocation, annotatedTypeFactory.getProcessingEnv())) {
                            receiver = ((MethodInvocationNode) receiver).getTarget().getReceiver();
                        } else {
                            // Do not continue, because the method does not return @This.
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }
}
