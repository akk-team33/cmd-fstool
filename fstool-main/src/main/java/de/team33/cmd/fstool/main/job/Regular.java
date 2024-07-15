package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.Job;
import de.team33.cmd.fstool.main.common.Context;
import de.team33.patterns.enums.alpha.EnumTool;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum Regular {

    ABOUT(About::job, About.EXCERPT),
    SETUP(Setup::runnable, "GET or SET the user specific setup."),
    DCOPY(DirCopy::runnable, "Copy the subdirectory structure from one directory to another."),
    MKDATE(MKDate::runnable, "Create a directory named according to the current local date."),
    MKTIME(MKTime::runnable, "Create a directory named according to the current local time."),
    CLEAN(Clean::runnable, "Remove all empty directories within a given directory substructure."),
    MOVE(Move::runnable, "Relocate all regular files located in a given directory."),
    SIEVE(Sieve::runnable, "Relocate all duplicated regular files located in a given directory."),
    INFO(Info::runnable, "Get information about a given file or directory."),
    COPY(Copy::runnable, "Copy files from a source directory to a target directory");

    private static final EnumTool<Regular> TOOL = EnumTool.of(Regular.class);
    private static final String NEWLINE = String.format("%n    ");

    private final BiFunction<Context, List<String>, Runnable> toJob;
    private final String excerpt;

    Regular(final BiFunction<Context, List<String>, Runnable> toJob, final String excerpt) {
        this.toJob = toJob;
        this.excerpt = excerpt;
    }

    public static boolean test(final List<String> args) {
        return (1 < args.size()) && TOOL.stream().anyMatch(toFilter(args));
    }

    public static Runnable job(final Context context, final List<String> args) {
        return TOOL.mapFirst(toFilter(args), toJob(context, args));
    }

    private static Predicate<Regular> toFilter(final List<String> args) {
        return item -> item.name().equalsIgnoreCase(args.get(1));
    }

    private static Function<Regular, Runnable> toJob(final Context context, final List<String> args) {
        return item -> item.toJob.apply(context, args);
    }

    public static String excerpt() {
        return TOOL.stream()
                   .map(regular -> String.format("%s : %s", regular.name(), regular.excerpt))
                   .collect(Collectors.joining(NEWLINE));
    }
}
