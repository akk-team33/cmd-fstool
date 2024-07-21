package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.patterns.io.deimos.TextIO;

import java.util.List;

public class NoArgs extends Help {

    private NoArgs(final Context context) {
        super(context);
        add(ctx -> ctx.printf("%s%n%n", TextIO.read(NoArgs.class, "NoArgs.txt")));
    }

    public static boolean test(final List<String> args) {
        return args.isEmpty();
    }

    public static Runnable job(final Context context, final List<String> args) {
        assert test(args);
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        return new NoArgs(context);
    }
}
