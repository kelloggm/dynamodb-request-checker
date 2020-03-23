package org.checkerframework.ddbrequest.enforcechecks;

import com.sun.source.tree.Tree;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.ddbrequest.common.DDBUtils;
import org.checkerframework.ddbrequest.ddbdefinitions.DDBDefinitionsAnnotatedTypeFactory;
import org.checkerframework.ddbrequest.ddbrequirements.DDBRequirementsAnnotatedTypeFactory;
import org.checkerframework.framework.source.Result;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.AnnotationUtils;

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
      DDBDefinitionsAnnotatedTypeFactory definitionFactory,
      DDBRequirementsAnnotatedTypeFactory requirementsFactory,
      BaseTypeChecker checker) {
    checkRequirementsAgainstDefinitions(
        treeToCheck, treeToCheck, definitionFactory, requirementsFactory, checker);
  }

  /**
   * Checks that the types given by the type factories provided for the definitions and requirements
   * are correct, and issues an error through the checker's reporting interface otherwise. This
   * version uses different trees to check and on which to report.
   */
  public static void checkRequirementsAgainstDefinitions(
      Tree treeToCheck,
      Tree reportLocation,
      DDBDefinitionsAnnotatedTypeFactory definitionFactory,
      DDBRequirementsAnnotatedTypeFactory requirementsFactory,
      BaseTypeChecker checker) {
    // first, pull out the requirements

    AnnotatedTypeMirror requirementsType;
    AnnotationMirror requirementsAnno = null;

    if (treeToCheck != null) {
      requirementsType = requirementsFactory.getAnnotatedType(treeToCheck);
      if (requirementsType != null) {
        requirementsAnno = requirementsType.getAnnotationInHierarchy(requirementsFactory.top);
      }
    }
    if (requirementsAnno == null) {
      requirementsAnno = requirementsFactory.top;
    }

    List<String> requiredNames =
        AnnotationUtils.areSame(requirementsAnno, requirementsFactory.top)
            ? new ArrayList<>()
            : AnnotationUtils.getElementValueArray(requirementsAnno, "names", String.class, true);

    List<String> requiredValues =
        AnnotationUtils.areSame(requirementsAnno, requirementsFactory.top)
            ? new ArrayList<>()
            : AnnotationUtils.getElementValueArray(requirementsAnno, "values", String.class, true);

    // then, use a similar process to get the actual values

    AnnotatedTypeMirror definitionsType;
    AnnotationMirror definitionsAnno = null;

    if (treeToCheck != null) {
      definitionsType = definitionFactory.getAnnotatedType(treeToCheck);
      if (definitionsType != null) {
        definitionsAnno = definitionsType.getAnnotationInHierarchy(definitionFactory.top);
      }
    }

    if (definitionsAnno == null) {
      definitionsAnno = definitionFactory.top;
    }

    List<String> definitionNames =
        AnnotationUtils.areSame(definitionsAnno, definitionFactory.top)
            ? new ArrayList<>()
            : AnnotationUtils.getElementValueArray(definitionsAnno, "names", String.class, true);

    List<String> definitionValues =
        AnnotationUtils.areSame(definitionsAnno, definitionFactory.top)
            ? new ArrayList<>()
            : AnnotationUtils.getElementValueArray(definitionsAnno, "values", String.class, true);

    // Then turn these two lists into hashsets with the actual names, stripping the # and : in the
    // process.
    // If we encounter incorrect #/:_s, then issue a report.
    HashSet<String> definedNames = new HashSet<>();
    HashSet<String> definedValues = new HashSet<>();

    for (String name : definitionNames) {
      if (name.startsWith("#")) {
        definedNames.add(name.substring(1));
      } else {
        checker.report(
            Result.failure(DDBUtils.MIXED_NAMES_VALUES, name, "expressionAttributeNames"),
            reportLocation);
      }
    }

    for (String value : definitionValues) {
      if (value.startsWith(":")) {
        definedValues.add(value.substring(1));
      } else {
        checker.report(
            Result.failure(DDBUtils.MIXED_NAMES_VALUES, value, "expressionAttributeValues"),
            reportLocation);
      }
    }

    // finally, check that the required things are actually defined

    for (String requiredName : requiredNames) {
      if (!definedNames.contains(requiredName)) {
        final String definedNamesString = String.join(", ", definitionNames);
        checker.report(
            Result.failure(DDBUtils.UNDEFINED, "name", requiredName, definedNamesString),
            reportLocation);
      }
    }

    for (String requiredValue : requiredValues) {
      if (!definedValues.contains(requiredValue)) {
        final String definedValuesString = String.join(", ", definitionValues);
        checker.report(
            Result.failure(DDBUtils.UNDEFINED, "value", requiredValue, definedValuesString),
            reportLocation);
      }
    }
  }
}
