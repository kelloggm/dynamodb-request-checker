package tests;

import java.io.File;
import java.util.List;
import org.checkerframework.ddbrequest.requiredvalues.RequiredValuesChecker;
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

/** Test runner that uses the Checker Framework's tooling. */
public class RequiredValuesTest extends CheckerFrameworkPerDirectoryTest {
  public RequiredValuesTest(List<File> testFiles) {
    super(
        testFiles,
        RequiredValuesChecker.class,
        "requiredvalues",
        "-Anomsgtext",
        "-Astubs=stubs",
        "-nowarn");
  }

  @Parameters
  public static String[] getTestDirs() {
    return new String[] {"requiredvalues"};
  }
}
