package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.patterns.io.deimos.TextIO;
import de.team33.patterns.testing.titan.io.Redirected;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HelpTest implements Context {

    @Test
    final void help() throws IOException {
        final String expected = String.format(TextIO.read(HelpTest.class, "HelpTest-help.txt"));
        final Runnable runnable = Help.by(this, List.of("cmdName")).runnable();

        final String result = Redirected.outputOf(runnable::run);
        //printf(result);

        assertEquals(expected, result);
    }
}
