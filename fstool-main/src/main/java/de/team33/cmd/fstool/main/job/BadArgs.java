package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.patterns.io.deimos.TextIO;

import java.util.List;

public class BadArgs {

    public static boolean test(final List<String> args) {
        return !args.isEmpty();
    }

    public static Runnable job(final Context context, final List<String> args) {
        assert test(args);
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        final String cmdLine = String.join(" ", args);
        final String cmdName = args.get(0);
        final String format = TextIO.read(BadArgs.class, "BadArgs.txt");
        return () -> context.printf(format, cmdLine, cmdName, Regular.excerpt());
    }
}
