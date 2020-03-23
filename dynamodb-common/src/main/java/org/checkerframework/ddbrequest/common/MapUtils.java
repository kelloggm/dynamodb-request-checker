package org.checkerframework.ddbrequest.common;

import com.sun.source.tree.MethodInvocationTree;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import org.checkerframework.javacutil.ElementUtils;
import org.checkerframework.javacutil.TreeUtils;

/** This class contains all the hard-coded business logic related to various kinds of maps. */
public class MapUtils {
  public MapUtils(ProcessingEnvironment processingEnv) {
    immutableMapOfs = new ArrayList<>();
    for (int ofParamCount = 2; ofParamCount <= 10; ofParamCount += 2) {
      immutableMapOfs.add(
          TreeUtils.getMethod(
              "com.google.common.collect.ImmutableMap", "of", ofParamCount, processingEnv));
    }
    for (int ofParamCount = 2; ofParamCount <= 10; ofParamCount += 2) {
      immutableMapOfs.add(
          TreeUtils.getMethod(
              "software.amazon.awssdk.utils.ImmutableMap", "of", ofParamCount, processingEnv));
    }
    immutableMapOfs.add(
        TreeUtils.getMethod("java.util.Collections", "singletonMap", 2, processingEnv));

    mapPuts = new ArrayList<>();
    mapPuts.add(TreeUtils.getMethod("java.util.Map", "put", 2, processingEnv));
    mapPuts.add(TreeUtils.getMethod("java.util.HashMap", "put", 2, processingEnv));
    mapPuts.add(TreeUtils.getMethod("java.util.EnumMap", "put", 2, processingEnv));
    mapPuts.add(TreeUtils.getMethod("java.util.Hashtable", "put", 2, processingEnv));
    mapPuts.add(TreeUtils.getMethod("java.util.TreeMap", "put", 2, processingEnv));
    mapPuts.add(TreeUtils.getMethod("java.util.WeakHashMap", "put", 2, processingEnv));

    // putIfAbsent also guarantees the same thing
    mapPuts.add(TreeUtils.getMethod("java.util.Map", "putIfAbsent", 2, processingEnv));
    mapPuts.add(TreeUtils.getMethod("java.util.HashMap", "putIfAbsent", 2, processingEnv));
    mapPuts.add(TreeUtils.getMethod("java.util.Hashtable", "putIfAbsent", 2, processingEnv));

    immutableMapBuilderPuts = new ArrayList<>();
    immutableMapBuilderPuts.add(
        TreeUtils.getMethod(
            "com.google.common.collect.ImmutableMap.Builder", "put", 2, processingEnv));
    immutableMapBuilderPuts.add(
        TreeUtils.getMethod(
            "software.amazon.awssdk.utils.ImmutableMap.Builder", "put", 2, processingEnv));
  }

  private static final String[] IMMUTABLE_MAP_BUILDERS = {
    "com.google.common.collect.ImmutableMap.Builder",
    "software.amazon.awssdk.utils.ImmutableMap.Builder"
  };

  /** The representations of Map#put and similar methods. */
  private final List<ExecutableElement> mapPuts;

  /**
   * The representations of methods like com.google.common.collect.ImmutableMap.Builder#put, which
   * both act like Map#put *and* return their receiver.
   */
  private final List<ExecutableElement> immutableMapBuilderPuts;

  /**
   * This list contains representations of all methods that should be treated like the
   * com.google.common.collect.ImmutableMap#of methods.
   */
  private final List<ExecutableElement> immutableMapOfs;

  /**
   * Returns true if the given MethodInvocationTree is invoking a method that should be used for
   * inference in the style of Map#put, in the given processing environment. The processing
   * environment should come from an AnnotatedTypeFactory.
   */
  public boolean isMapPuts(MethodInvocationTree tree, ProcessingEnvironment processingEnv) {
    return TreeUtils.isMethodInvocation(tree, mapPuts, processingEnv)
        || TreeUtils.isMethodInvocation(tree, immutableMapBuilderPuts, processingEnv);
  }

  /**
   * Returns true iff this method is defined in a builder for a map similar to
   * com.google.common.collect.ImmutableMap and returns a Builder object.
   */
  public boolean isFromImmutableMapBuilder(MethodInvocationTree tree) {
    ExecutableElement methodDeclaration = TreeUtils.elementFromUse(tree);
    TypeElement enclosingClass = ElementUtils.enclosingClass(methodDeclaration);
    String qualifiedName = enclosingClass.getQualifiedName().toString();
    String returnTypeName = methodDeclaration.getReturnType().toString();
    int typeParamIndex = returnTypeName.indexOf('<');
    if (typeParamIndex != -1) {
      returnTypeName = returnTypeName.substring(0, typeParamIndex);
    }
    if (enclosingClass != null) {
      for (String builderName : IMMUTABLE_MAP_BUILDERS) {
        if (builderName.equals(qualifiedName) && builderName.equals(returnTypeName)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Checks whether the given tree is an invocation of a method that should be treated like
   * ImmutableMap#of.
   */
  public boolean isImmutableMapOf(MethodInvocationTree tree, ProcessingEnvironment processingEnv) {
    return TreeUtils.isMethodInvocation(tree, immutableMapOfs, processingEnv);
  }

  /**
   * Checks whether the given tree is an invocation of a method that should be treated like
   * ImmutableMap.Builder#put.
   */
  public boolean isImmutableMapBuilderPut(
      MethodInvocationTree tree, ProcessingEnvironment processingEnv) {
    return TreeUtils.isMethodInvocation(tree, immutableMapBuilderPuts, processingEnv);
  }
}
