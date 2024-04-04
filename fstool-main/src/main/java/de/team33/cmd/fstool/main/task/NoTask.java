package de.team33.cmd.fstool.main.task;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.patterns.io.deimos.TextIO;
import de.team33.patterns.lazy.narvi.Lazy;

import java.util.List;

public class NoTask implements Runnable {

    private static final Lazy<String> HEAD = Lazy.init(() -> TextIO.read(NoTask.class, "../head.txt"));
    private static final Lazy<String> TAIL = Lazy.init(() -> TextIO.read(NoTask.class, "NoTask.txt"));

    private final Context context;

    private NoTask(final Context context) {
        this.context = context;
    }

    public static Runnable of(final Context context, final List<String> args) {
        assert args.isEmpty();
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        return new NoTask(context);
    }

    @Override
    public void run() {
        context.printf("%s%n%s%n%n", HEAD.get(), TAIL.get());
    }
}
