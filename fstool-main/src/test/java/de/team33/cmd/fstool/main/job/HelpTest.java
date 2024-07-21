package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.patterns.io.deimos.TextIO;
import de.team33.patterns.testing.titan.io.Redirected;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelpTest implements Context {

    @Test
    final void help() throws IOException {
        final String expected = String.format(TextIO.read(HelpTest.class, "HelpTest-help.txt"));
        final Help help = new Help(this).add(ctx -> ctx.printf("%s%n%n", HelpTest.class.getCanonicalName()));

        final String result = Redirected.outputOf(help::run);
        //printf(result);

        assertEquals(expected, result);
    }
}
