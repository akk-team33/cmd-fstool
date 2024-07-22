package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.patterns.enums.alpha.EnumValues;
import de.team33.patterns.io.delta.Resource;

import java.util.List;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum Regular {

    ABOUT(About::job),
    SETUP(Setup::job),
    DCOPY(DCopy::job),
    MKDATE(MKDate::job),
    MKTIME(MKTime::job),
    CLEAN(Clean::job),
    MOVE(Move::job),
    SIEVE(Sieve::job),
    INFO(Info::job),
    COPY(Copy::job);

    private static final EnumValues<Regular> VALUES = EnumValues.of(Regular.class);
    private static final String NEWLINE = String.format("%n    ");

    private final BiFunction<Context, List<String>, Runnable> toJob;
    private final String excerpt;
    final String cmdArgs;

    Regular(final BiFunction<Context, List<String>, Runnable> toJob) {
        final String resourceName = Regular.class.getSimpleName() + "." + name() + ".properties";
        final Properties properties = Resource.by(Regular.class, resourceName)
                                              .readProperties();
        this.toJob = toJob;
        this.excerpt = properties.getProperty("excerpt");
        this.cmdArgs = properties.getProperty("cmdArgs");
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
