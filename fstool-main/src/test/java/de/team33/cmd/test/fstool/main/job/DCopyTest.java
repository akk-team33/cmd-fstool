package de.team33.cmd.test.fstool.main.job;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.cmd.fstool.main.job.DCopy;
import de.team33.patterns.io.deimos.TextIO;
import de.team33.patterns.testing.titan.io.Redirected;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DCopyTest implements Context {

    private static final Path TEST_PATH = Path.of("target",
                                                  "testing",
                                                  DCopyTest.class.getSimpleName(),
                                                  UUID.randomUUID().toString());
    private static final Path PATH_MISSING = TEST_PATH.resolve("missing");
    private static final Path PATH_NO_DIR = TEST_PATH.resolve("regular.file");
    private static final String ARG0 = DCopyTest.class.getSimpleName();
    private static final String ARG1 = "dcopy";
    private static final String SRC_MISSING_PROBLEM = String.format(
            "Problem:%n%n    source path <%s> is not a directory%n%n", PATH_MISSING);
    private static final String SRC_NO_DIR_PROBLEM = String.format(
            "Problem:%n%n    source path <%s> is not a directory%n%n", PATH_NO_DIR);
    private static final String TGT_NO_DIR_PROBLEM = String.format(
            "Problem:%n%n    target path <%s> exists but is not a directory%n%n", PATH_NO_DIR);

    @BeforeAll
    static void beforeAll() throws IOException {
        Files.createDirectories(TEST_PATH);
        Files.write(PATH_NO_DIR, UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
    }

    @ParameterizedTest
    @EnumSource
    void run_help(final RunHelpCase testCase) throws IOException {
        final String cmdLine = String.join(" ", testCase.args);
        final String format = TextIO.read(DCopyTest.class, "DCopyTest-run_help.txt");
        final String expected = String.format(format, cmdLine, testCase.problem);

        final String result = Redirected.outputOf(() -> DCopy.job(this, testCase.args).run());
        //printf(result);

        assertEquals(expected, result);
    }

    enum RunHelpCase {
        MISSING_ARGS(List.of(ARG0, ARG1), ""),
        TOO_MUCH_ARGS(List.of(ARG0, ARG1, "too", "much", "args"), ""),
        SRC_MISSING(List.of(ARG0, ARG1, PATH_MISSING.toString(), PATH_MISSING.toString()), SRC_MISSING_PROBLEM),
        SRC_NO_DIR(List.of(ARG0, ARG1, PATH_NO_DIR.toString(), PATH_MISSING.toString()), SRC_NO_DIR_PROBLEM),
        TGT_NO_DIR(List.of(ARG0, ARG1, TEST_PATH.toString(), PATH_NO_DIR.toString()), TGT_NO_DIR_PROBLEM);

        final List<String> args;
        final String problem;

        RunHelpCase(final List<String> args, final String problem) {
            this.args = args;
            this.problem = problem;
        }
    }
}
