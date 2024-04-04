package de.team33.cmd.fstool.main.task;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.patterns.io.deimos.TextIO;
import de.team33.patterns.lazy.narvi.Lazy;

import java.util.List;

public class EmptyTask implements Runnable {

    private static final Lazy<String> HEAD = Lazy.init(() -> TextIO.read(NoTask.class, "../head.txt"));

    private final Context context;
    private final String command;

    public EmptyTask(final Context context, final String command) {
        this.context = context;
        this.command = command;
    }

    public static Runnable of(final Context context, final List<String> args) {
        assert 1 == args.size();
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        return new EmptyTask(context, args.get(0));
    }

    @Override
    public final void run() {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
