package de.team33.cmd.fstool.main;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.cmd.fstool.main.job.BadArgs;
import de.team33.cmd.fstool.main.job.NoArgs;
import de.team33.cmd.fstool.main.job.Regular;
import de.team33.patterns.enums.alpha.EnumValues;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public enum Main implements Context  {

    NO_ARGS(NoArgs::test, NoArgs::job),
    REGULAR(Regular::test, Regular::job),
    BAD_ARGS(BadArgs::test, BadArgs::job);

    private static final EnumValues<Main> VALUES = EnumValues.of(Main.class)
                                                             .fallback(BAD_ARGS);

    private final Predicate<List<String>> filter;
    private final BiFunction<Context, List<String>, Runnable> toJob;

    Main(final Predicate<List<String>> filter, final BiFunction<Context, List<String>, Runnable> toJob) {
        this.filter = filter;
        this.toJob = toJob;
    }

    public static void main(final String... args) {
        job(Arrays.asList(args)).run();
    }

    private static Runnable job(final List<String> args) {
        return VALUES.mapFirst(value -> value.filter.test(args),
                               value -> value.toJob.apply(value, args));
    }
}
