package tests;

import java.io.File;
import java.util.List;
import org.checkerframework.ddbrequest.definednames.DefinedNamesChecker;
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

/** Test runner that uses the Checker Framework's tooling. */
public class DefinedNamesTest extends CheckerFrameworkPerDirectoryTest {
  public DefinedNamesTest(List<File> testFiles) {
    super(
        testFiles,
        DefinedNamesChecker.class,
        "ddbnamesdefinitions",
        "-Anomsgtext",
        "-Astubs=stubs",
        "-nowarn");
  }

  @Parameters
  public static String[] getTestDirs() {
    return new String[] {"ddbnamesdefinitions"};
  }
}
