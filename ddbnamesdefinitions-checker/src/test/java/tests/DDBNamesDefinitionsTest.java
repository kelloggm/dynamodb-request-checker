package tests;

import java.io.File;
import java.util.List;
import org.checkerframework.ddbrequest.ddbnamesdefinitions.DDBNamesDefinitionsChecker;
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

/** Test runner that uses the Checker Framework's tooling. */
public class DDBNamesDefinitionsTest extends CheckerFrameworkPerDirectoryTest {
  public DDBNamesDefinitionsTest(List<File> testFiles) {
    super(
        testFiles,
        DDBNamesDefinitionsChecker.class,
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
