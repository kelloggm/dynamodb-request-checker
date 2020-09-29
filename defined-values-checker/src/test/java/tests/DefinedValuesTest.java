package tests;

import java.io.File;
import java.util.List;
import org.checkerframework.ddbrequest.definedvalues.DefinedValuesChecker;
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

/** Test runner that uses the Checker Framework's tooling. */
public class DefinedValuesTest extends CheckerFrameworkPerDirectoryTest {
  public DefinedValuesTest(List<File> testFiles) {
    super(
        testFiles,
        DefinedValuesChecker.class,
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
