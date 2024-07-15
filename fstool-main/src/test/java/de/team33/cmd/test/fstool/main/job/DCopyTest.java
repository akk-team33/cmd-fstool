package de.team33.cmd.test.fstool.main.job;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.cmd.fstool.main.job.DCopy;
import de.team33.patterns.io.deimos.TextIO;
import de.team33.patterns.testing.titan.io.Redirected;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DCopyTest implements Context {

    private static final String ARG0 = DCopyTest.class.getSimpleName();
    private static final String ARG1 = "dcopy";

    @Test
    void run_help() throws IOException {
        final String expected = String.format("%s%n%n",
                                              TextIO.read(DCopyTest.class, "DCopyTest-run_help.txt"));
        final String result = Redirected.outputOf(() -> DCopy.job(this, List.of(ARG0, ARG1)).run());
        assertEquals(expected, result);
    }
}
