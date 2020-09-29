package tests;

import java.io.File;
import java.util.List;
import org.checkerframework.ddbrequest.ddbvaluesdefinitions.DDBValuesDefinitionsChecker;
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

/** Test runner that uses the Checker Framework's tooling. */
public class DDBValuesDefinitionsTest extends CheckerFrameworkPerDirectoryTest {
  public DDBValuesDefinitionsTest(List<File> testFiles) {
    super(
        testFiles,
        DDBValuesDefinitionsChecker.class,
        "ddbvaluesdefinitions",
        "-Anomsgtext",
        "-Astubs=stubs",
        "-nowarn");
  }

  @Parameters
  public static String[] getTestDirs() {
    return new String[] {"ddbvaluesdefinitions"};
  }
}
