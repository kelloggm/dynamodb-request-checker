package tests;

import java.io.File;
import java.util.List;
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

/** Test runner that uses the Checker Framework's tooling. */
public class EnforceChecksTest extends CheckerFrameworkPerDirectoryTest {
  public EnforceChecksTest(List<File> testFiles) {
    super(
        testFiles,
        org.checkerframework.ddbrequest.enforcechecks.EnforceChecksChecker.class,
        "enforcechecks",
        "-Anomsgtext",
        "-Astubs=stubs",
        "-nowarn");
  }

  @Parameters
  public static String[] getTestDirs() {
    return new String[] {"enforcechecks"};
  }
}
