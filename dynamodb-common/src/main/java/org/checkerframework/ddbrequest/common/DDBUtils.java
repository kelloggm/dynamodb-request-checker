package org.checkerframework.ddbrequest.common;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import org.checkerframework.checker.compilermsgs.qual.CompilerMessageKey;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.dataflow.cfg.node.MethodInvocationNode;
import org.checkerframework.framework.source.Result;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.javacutil.ElementUtils;
import org.checkerframework.javacutil.TreeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This class contains all the hard-coded business logic related
 * to DynamoDB.
 */
public final class DDBUtils {

    public static final @CompilerMessageKey String MIXED_NAMES_VALUES = "ddb.names.and.values.confused";
    public static final @CompilerMessageKey String UNDEFINED = "ddb.expression.not.defined";

    /**
     *  The list of methods whose arguments define names
     */
    private final List<ExecutableElement> namesSetters;

    /**
     *  The list of methods whose arguments define values
     */
    private final List<ExecutableElement> valuesSetters;

    /**
     *  The list of methods whose arguments should be used to infer ddb expressions
     */
    private final List<ExecutableElement> expressionSetters;

    /**
     * The v1 Dynamo DB QueryRequest's fully-qualified name.
     */
    private static final String DDBV1RQ = "com.amazonaws.services.dynamodbv2.model.QueryRequest";

    /**
     * The v2 Dynamo DB QueryRequest builder's fully-qualified name.
     */
    private static final String DDBV2QRB = "software.amazon.awssdk.services.dynamodb.model.QueryRequest.Builder";

    public DDBUtils(ProcessingEnvironment processingEnv) {
        namesSetters = new ArrayList<>();
        namesSetters.add(TreeUtils.getMethod(DDBV2QRB, "expressionAttributeNames", 1, processingEnv));
        namesSetters.add(TreeUtils.getMethod(DDBV1RQ, "withExpressionAttributeNames", 1, processingEnv));
        valuesSetters = new ArrayList<>();
        valuesSetters.add(TreeUtils.getMethod(DDBV2QRB, "expressionAttributeValues", 1, processingEnv));
        valuesSetters.add(TreeUtils.getMethod(DDBV1RQ, "withExpressionAttributeValues", 1, processingEnv));
        expressionSetters = new ArrayList<>();
        expressionSetters.add(TreeUtils.getMethod(DDBV2QRB, "keyConditionExpression", 1, processingEnv));
        expressionSetters.add(TreeUtils.getMethod(DDBV2QRB, "filterExpression", 1, processingEnv));
        expressionSetters.add(TreeUtils.getMethod(DDBV1RQ, "withKeyConditionExpression", 1, processingEnv));
        expressionSetters.add(TreeUtils.getMethod(DDBV1RQ, "withFilterExpression", 1, processingEnv));
        // TODO: there are more of these methods
    }

    /**
     * Returns true if this method is white-listed as returning its receiver.
     * This is a bit of an ugly hack, but it definitely works.
     */
    public boolean isKnownReturnsReceiverMethod(MethodInvocationTree tree) {
        ExecutableElement methodDeclaration = TreeUtils.elementFromUse(tree);
        TypeElement enclosingClass = ElementUtils.enclosingClass(methodDeclaration);
        if (enclosingClass != null) {
            if (DDBV2QRB.equals(enclosingClass.getQualifiedName().toString())) {
                return DDBV2QRB.equals(methodDeclaration.getReturnType().toString());
            } else if (DDBV1RQ.equals(enclosingClass.getQualifiedName().toString())) {
                return DDBV1RQ.equals(methodDeclaration.getReturnType().toString());
            }
        }
        return false;
    }

    /**
     * checks if a given method call sets the names
     */
    public boolean isNamesSetter(MethodInvocationTree tree, ProcessingEnvironment processingEnv) {
        return TreeUtils.isMethodInvocation(tree, namesSetters, processingEnv);
    }

    /**
     * checks if a given method call sets the values
     */
    public boolean isValuesSetter(MethodInvocationTree tree, ProcessingEnvironment processingEnv) {
        return TreeUtils.isMethodInvocation(tree, valuesSetters, processingEnv);
    }

    /**
     * Returns whether this method invocation node is invoking one of the expression setters.
     */
    public boolean isExpressionMethodCall(MethodInvocationNode node, ProcessingEnvironment processingEnv) {
        return isExpressionMethodCall(node.getTree(), processingEnv);
    }

    /**
     * Returns whether this method invocation tree is invoking one of the expression setters.
     */
    public boolean isExpressionMethodCall(MethodInvocationTree tree, ProcessingEnvironment processingEnv) {
        return TreeUtils.isMethodInvocation(tree, expressionSetters, processingEnv);
    }

    /**
     * Parses the value expressions (those that start with ":").
     */
    public List<String> parseValues(String value) {
        return parseImpl(value, ":");
    }

    /**
     * Parses the name expressions (those that start with "#").
     */
    public List<String> parseNames(String value) {
        return parseImpl(value, "#");
    }

    private List<String> parseImpl(String value, String start) {
        String[] parts = value.split("\\s");
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            if (part.startsWith(start)) {
                result.add(part.substring(start.length()));
            }
        }
        return result;
    }
}
