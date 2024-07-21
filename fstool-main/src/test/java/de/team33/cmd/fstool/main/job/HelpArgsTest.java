package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.patterns.io.deimos.TextIO;
import de.team33.patterns.testing.titan.io.Redirected;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelpArgsTest implements Context {

    @Test
    final void help() throws IOException {
        final String expected = String.format(TextIO.read(HelpArgsTest.class, "HelpArgsTest-help.txt"));
        final Help help = new HelpArgs(this, List.of("cmdName"), "TASK [arg [, arg [, ...]]]")
                .add(ctx -> ctx.printf("    %s%n%n", HelpArgsTest.class.getCanonicalName()));

        final String result = Redirected.outputOf(help::run);
        //printf(result);

        assertEquals(expected, result);
    }
}
