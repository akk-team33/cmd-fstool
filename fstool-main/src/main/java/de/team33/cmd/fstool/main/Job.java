package de.team33.cmd.fstool.main;

import de.team33.cmd.fstool.main.common.BadRequestException;
import de.team33.cmd.fstool.main.common.Context;
import de.team33.cmd.fstool.main.job.Clean;
import de.team33.cmd.fstool.main.job.DirCopy;
import de.team33.cmd.fstool.main.job.Info;
import de.team33.cmd.fstool.main.job.MKDate;
import de.team33.cmd.fstool.main.job.MKTime;
import de.team33.cmd.fstool.main.job.Move;
import de.team33.cmd.fstool.main.job.Setup;
import de.team33.cmd.fstool.main.job.Sieve;
import de.team33.patterns.io.deimos.TextIO;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Job {

    ABOUT(Args::about, "Get basic info about this application."),
    SETUP(Args::setup, "GET or SET the user specific setup."),
    DCOPY(Args::dirCopy, "Copy the subdirectory structure from one directory to another."),
    MKDATE(Args::mkDate, "Create a directory named according to the current local date."),
    MKTIME(Args::mkTime, "Create a directory named according to the current local time."),
    CLEAN(Args::clean, "Remove all empty directories within a given directory substructure."),
    MOVE(Args::move, "Relocate all regular files located in a given directory."),
    SIEVE(Args::sieve, "Relocate all duplicated regular files located in a given directory."),
    INFO(Args::info, "Get information about a given file or directory.");

    private static final String NEW_LINE = String.format("%n    ");
    private static final Collector<CharSequence, ?, String> JOINING = Collectors.joining(NEW_LINE);
    private static final String PROBLEM = "Problem:%n%n    Unknown COMMAND: %s%n%n";

    private final Function<Args, Runnable> toRunnable;
    private final String excerpt;

    Job(final Function<Args, Runnable> toRunnable, final String excerpt) {
        this.toRunnable = toRunnable;
        this.excerpt = excerpt;
    }

    static Runnable runnable(final Context context, final String shellCmd, final List<String> args) {
        if (args.isEmpty()) {
            throw newBadRequestException("", shellCmd);
        } else {
            return runnable(new Args(context, shellCmd, args.get(0), args.subList(1, args.size())));
        }
    }

    private static Runnable runnable(final Args args) {
        return Stream.of(values())
                     .filter(job -> job.name().equalsIgnoreCase(args.mainCmd))
                     .findAny()
                     .map(value -> value.toRunnable.apply(args))
                     .orElseThrow(() -> newBadRequestException(String.format(PROBLEM, args.mainCmd), args.shellCmd));
    }

    private static String jobInfos() {
        return Stream.of(values())
                     .map(job -> job.name() + " : " + job.excerpt)
                     .collect(JOINING);
    }

    private static BadRequestException newBadRequestException(final String problem, final String shellCmd) {
        return new BadRequestException(String.format(TextIO.read(Job.class, "job.txt"),
                                                     problem, shellCmd, jobInfos()));
    }

    private record Args(Context context, String shellCmd, String mainCmd, List<String> args) {

        Runnable setup() {
            return Setup.runnable(context, shellCmd, args);
        }

        Runnable about() {
            return () -> context.printf(TextIO.read(Job.class, "about.txt"), shellCmd);
        }

        Runnable dirCopy() {
            return DirCopy.runnable(context, shellCmd, args);
        }

        Runnable mkDate() {
            return MKDate.runnable(context, shellCmd, args);
        }

        Runnable mkTime() {
            return MKTime.runnable(context, shellCmd, args);
        }

        Runnable clean() {
            return Clean.runnable(context, shellCmd, args);
        }

        Runnable move() {
            return Move.runnable(context, shellCmd, args);
        }

        Runnable sieve() {
            return Sieve.runnable(context, shellCmd, args);
        }

        Runnable info() {
            return Info.runnable(context, shellCmd, args);
        }
    }
}
