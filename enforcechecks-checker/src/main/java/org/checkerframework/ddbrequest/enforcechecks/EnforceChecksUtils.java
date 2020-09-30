package org.checkerframework.ddbrequest.enforcechecks;

import com.sun.source.tree.Tree;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.ddbrequest.common.DDBUtils;
import org.checkerframework.ddbrequest.definednames.DefinedNamesAnnotatedTypeFactory;
import org.checkerframework.ddbrequest.definednames.qual.DefinedNames;
import org.checkerframework.ddbrequest.definedvalues.DefinedValuesAnnotatedTypeFactory;
import org.checkerframework.ddbrequest.definedvalues.qual.DefinedValues;
import org.checkerframework.ddbrequest.requirednames.RequiredNamesAnnotatedTypeFactory;
import org.checkerframework.ddbrequest.requirednames.qual.RequiredNames;
import org.checkerframework.ddbrequest.requiredvalues.RequiredValuesAnnotatedTypeFactory;
import org.checkerframework.ddbrequest.requiredvalues.qual.RequiredValues;
import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.AnnotationUtils;

/**
 * Helper class that implements the logic of actually enforcing that the names and values that are
 * required were actually defined.
 */
public class EnforceChecksUtils {

  private EnforceChecksUtils() {
    // this class is not instantiable
  }

  /**
   * Checks that the types given by the type factories provided for the definitions and requirements
   * are correct, and issues an error through the checker's reporting interface otherwise. This
   * version uses the same tree to report that it checks.
   */
  public static void checkRequirementsAgainstDefinitions(
      Tree treeToCheck,
      DefinedValuesAnnotatedTypeFactory definedValuesFactory,
      DefinedNamesAnnotatedTypeFactory definedNamesFactory,
      RequiredValuesAnnotatedTypeFactory requiredValuesFactory,
      RequiredNamesAnnotatedTypeFactory requiredNamesFactory,
      BaseTypeChecker checker) {
    checkRequirementsAgainstDefinitions(
        treeToCheck,
        treeToCheck,
        definedValuesFactory,
        definedNamesFactory,
        requiredValuesFactory,
        requiredNamesFactory,
        checker);
  }

  /**
   * Utility method for fetching the contents of an accumulator annotation from one of the
   * subcheckers.
   *
   * @param treeToCheck the tree whose annotation should be fetched
   * @param factory the subchecker's type factory
   * @param acc the type of accumulator annotation
   * @return the contents of the accumulator in that hierarchy, or the empty list if there are none
   */
  private static List<String> fetchStringsFromFactory(
      Tree treeToCheck, AnnotatedTypeFactory factory, Class<? extends Annotation> acc) {
    if (treeToCheck != null) {
      AnnotatedTypeMirror type;
      type = factory.getAnnotatedType(treeToCheck);
      if (type != null) {
        AnnotationMirror anno = type.getAnnotation(acc);
        return AnnotationUtils.getElementValueArray(anno, "value", String.class, true);
      }
    }
    return Collections.emptyList();
  }

  /**
   * Checks that the types given by the type factories provided for the definitions and requirements
   * are correct, and issues an error through the checker's reporting interface otherwise. This
   * version uses different trees to check and on which to report.
   */
  public static void checkRequirementsAgainstDefinitions(
      Tree treeToCheck,
      Tree reportLocation,
      DefinedValuesAnnotatedTypeFactory definedValuesFactory,
      DefinedNamesAnnotatedTypeFactory definedNamesFactory,
      RequiredValuesAnnotatedTypeFactory requiredValuesFactory,
      RequiredNamesAnnotatedTypeFactory requiredNamesFactory,
      BaseTypeChecker checker) {

    // first, fetch info from subcheckers about requirements and definitions
    List<String> requiredValues =
        fetchStringsFromFactory(treeToCheck, requiredValuesFactory, RequiredValues.class);
    List<String> requiredNames =
        fetchStringsFromFactory(treeToCheck, requiredNamesFactory, RequiredNames.class);

    List<String> definitionValues =
        fetchStringsFromFactory(treeToCheck, definedValuesFactory, DefinedValues.class);
    List<String> definitionNames =
        fetchStringsFromFactory(treeToCheck, definedNamesFactory, DefinedNames.class);

    // Then turn these two lists into hashsets with the actual names, stripping the # and : in the
    // process.
    // If we encounter incorrect #/:_s, then issue a report.
    HashSet<String> definedNames = new HashSet<>();
    HashSet<String> definedValues = new HashSet<>();

    for (String name : definitionNames) {
      if (name.startsWith("#")) {
        definedNames.add(name.substring(1));
      } else {
        checker.reportError(
            reportLocation, DDBUtils.MIXED_NAMES_VALUES, name, "expressionAttributeNames");
      }
    }

    for (String value : definitionValues) {
      if (value.startsWith(":")) {
        definedValues.add(value.substring(1));
      } else {
        checker.reportError(
            reportLocation, DDBUtils.MIXED_NAMES_VALUES, value, "expressionAttributeValues");
      }
    }

    // finally, check that the required things are actually defined

    for (String requiredName : requiredNames) {
      if (!definedNames.contains(requiredName)) {
        final String definedNamesString = String.join(", ", definitionNames);
        checker.reportError(
            reportLocation, DDBUtils.UNDEFINED, "name", requiredName, definedNamesString);
      }
    }

    for (String requiredValue : requiredValues) {
      if (!definedValues.contains(requiredValue)) {
        final String definedValuesString = String.join(", ", definitionValues);
        checker.reportError(
            reportLocation, DDBUtils.UNDEFINED, "value", requiredValue, definedValuesString);
      }
    }
  }
}
