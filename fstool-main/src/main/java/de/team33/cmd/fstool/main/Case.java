package de.team33.cmd.fstool.main;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.cmd.fstool.main.task.EmptyTask;
import de.team33.cmd.fstool.main.task.NoTask;
import de.team33.cmd.fstool.main.task.Unknown;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

enum Case {

    NO_ARGS(List::isEmpty, NoTask::of),
    SINGLE_ARG(args -> 1 == args.size(), EmptyTask::of),
    UNKNOWN(args -> false, Unknown::of);

    private final Predicate<List<String>> filter;
    private final BiFunction<Context, List<String>, Runnable> toRunnable;

    Case(Predicate<List<String>> filter, final BiFunction<Context, List<String>, Runnable> toRunnable) {
        this.filter = filter;
        this.toRunnable = toRunnable;
    }

    static Case select(final List<String> args) {
        return Stream.of(values())
                     .filter(task -> task.filter.test(args))
                     .findFirst()
                     .orElse(UNKNOWN);
    }

    final Runnable task(final Context context, final List<String> args) {
        return toRunnable.apply(context, args);
    }
}
