package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.patterns.io.deimos.TextIO;

import java.util.List;

public class BadArgs extends HelpArgs {

    private BadArgs(final Context context, final List<String> args) {
        super(context, args, "TASK [arg [, arg [, ...]]]");
        add(ctx -> ctx.printf(TextIO.read(BadArgs.class, "BadArgs.txt"), Regular.excerpt(), cmdName));
    }

    public static boolean test(final List<String> args) {
        return !args.isEmpty();
    }

    public static Runnable job(final Context context, final List<String> args) {
        assert test(args);
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        return new BadArgs(context, args);
    }
}
