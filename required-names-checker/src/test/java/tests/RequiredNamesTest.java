package tests;

import java.io.File;
import java.util.List;
import org.checkerframework.ddbrequest.requirednames.RequiredNamesChecker;
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

/** Test runner that uses the Checker Framework's tooling. */
public class RequiredNamesTest extends CheckerFrameworkPerDirectoryTest {
  public RequiredNamesTest(List<File> testFiles) {
    super(
        testFiles,
        RequiredNamesChecker.class,
        "requirednames",
        "-Anomsgtext",
        "-Astubs=stubs",
        "-nowarn");
  }

  @Parameters
  public static String[] getTestDirs() {
    return new String[] {"requirednames"};
  }
}
