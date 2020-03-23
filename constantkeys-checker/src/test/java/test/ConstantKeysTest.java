package test;

import java.io.File;
import java.util.List;

import org.checkerframework.ddbrequest.constantkeys.ConstantKeysChecker;
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test runner that uses the Checker Framework's tooling.
 */
public class ConstantKeysTest extends CheckerFrameworkPerDirectoryTest {
    public ConstantKeysTest(List<File> testFiles) {
        super(
                testFiles,
                ConstantKeysChecker.class,
                "constantkeys",
                "-Anomsgtext",
                "-Astubs=stubs",
                "-nowarn");
    }

    @Parameters
    public static String[] getTestDirs() {
        return new String[] {"constantkeys"};
    }
}