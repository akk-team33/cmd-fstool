package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.patterns.enums.alpha.EnumValues;

import java.util.List;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum Regular {

    ABOUT(About::job, About.EXCERPT, About.CMD_ARGS),
    SETUP(Setup::job, Setup.EXCERPT, null),
    DCOPY(DCopy::job, DCopy.EXCERPT, DCopy.CMD_ARGS),
    MKDATE(MKDate::job, MKDate.EXCERPT, null),
    MKTIME(MKTime::job, MKTime.EXCERPT, null),
    CLEAN(Clean::job, Clean.EXCERPT, null),
    MOVE(Move::job, Move.EXCERPT, null),
    SIEVE(Sieve::job, Sieve.EXCERPT, null),
    INFO(Info::job, Info.EXCERPT, null),
    COPY(Copy::job, Copy.EXCERPT, null);

    private static final EnumValues<Regular> VALUES = EnumValues.of(Regular.class);
    private static final String NEWLINE = String.format("%n    ");

    private final BiFunction<Context, List<String>, Runnable> toJob;
    private final String excerpt;
    final String cmdArgs;

    Regular(final BiFunction<Context, List<String>, Runnable> toJob, final String excerpt, final String cmdArgs) {
        this.toJob = toJob;
        this.excerpt = excerpt;
        this.cmdArgs = cmdArgs;
    }

    public static boolean test(final List<String> args) {
        return (1 < args.size()) && VALUES.stream().anyMatch(toFilter(args));
    }

    public static Runnable job(final Context context, final List<String> args) {
        return VALUES.mapFirst(toFilter(args), toJob(context, args));
    }

    private static Predicate<Regular> toFilter(final List<String> args) {
        return item -> item.name().equalsIgnoreCase(args.get(1));
    }

    private static Function<Regular, Runnable> toJob(final Context context, final List<String> args) {
        return item -> item.toJob.apply(context, args);
    }

    public static String excerpt() {
        return VALUES.stream()
                     .map(regular -> String.format("%s : %s", regular.name(), regular.excerpt))
                     .collect(Collectors.joining(NEWLINE));
    }
}
