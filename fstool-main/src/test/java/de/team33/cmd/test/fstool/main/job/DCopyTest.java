package de.team33.cmd.test.fstool.main.job;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.cmd.fstool.main.job.DCopy;
import de.team33.patterns.io.deimos.TextIO;
import de.team33.patterns.testing.titan.io.Redirected;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DCopyTest implements Context {

//    private static final String PATH_NO_DIR = Path.of("target",
//                                                      "testing",
//                                                      DCopyTest.class.getSimpleName(),
//                                                      UUID.randomUUID().toString())
//                                                  .resolve("regular.file")
//                                                  .toString();
    private static final String ARG0 = DCopyTest.class.getSimpleName();
    private static final String ARG1 = "dcopy";

    @ParameterizedTest
    @EnumSource
    void run_help(final RunHelpCase testCase) throws IOException {
        final String cmdLine = String.join(" ", testCase.args);
        final String format = TextIO.read(DCopyTest.class, "DCopyTest-run_help.txt");
        final String expected = String.format(format, cmdLine);

        final String result = Redirected.outputOf(() -> DCopy.job(this, testCase.args).run());

        assertEquals(expected, result);
    }

    enum RunHelpCase {
        MISSING_ARGS(List.of(ARG0, ARG1)),
        TOO_MUCH_ARGS(List.of(ARG0, ARG1, "too", "much", "args"))/*,
        SRC_NO_DIR(List.of(ARG0, ARG1, PATH_NO_DIR))*/;

        final List<String> args;

        RunHelpCase(final List<String> args) {
            this.args = args;
        }
    }
}
