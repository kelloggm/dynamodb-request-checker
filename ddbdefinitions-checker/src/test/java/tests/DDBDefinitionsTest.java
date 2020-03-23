package tests;

import java.io.File;
import java.util.List;
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

/** Test runner that uses the Checker Framework's tooling. */
public class DDBDefinitionsTest extends CheckerFrameworkPerDirectoryTest {
  public DDBDefinitionsTest(List<File> testFiles) {
    super(
        testFiles,
        org.checkerframework.ddbrequest.ddbdefinitions.DDBDefinitionsChecker.class,
        "ddbdefinitions",
        "-Anomsgtext",
        "-Astubs=stubs",
        "-nowarn");
  }

  @Parameters
  public static String[] getTestDirs() {
    return new String[] {"ddbdefinitions"};
  }
}
