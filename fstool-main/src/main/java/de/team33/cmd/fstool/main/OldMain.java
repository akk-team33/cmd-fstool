package de.team33.cmd.fstool.main;

import de.team33.cmd.fstool.main.common.BadRequestException;
import de.team33.cmd.fstool.main.common.Context;
import de.team33.patterns.io.deimos.TextIO;

import java.util.List;

public class OldMain implements Context {

    private final Runnable job;

    private OldMain(final List<String> args) {
        this.job = newJob(args);
    }

    public static void main(final String... args) {
        new OldMain(List.of(args)).run();
    }

    private static String argsInLine(final List<String> args) {
        return args.isEmpty() ? "*called without arguments*" : String.join(" ", args);
    }

    private Runnable newJob(final List<String> args) {
        try {
            if (args.size() < 1) {
                throw new BadRequestException(TextIO.read(OldMain.class, "main.txt"));
            } else {
                return Job.runnable(this, args.get(0), args.subList(1, args.size()));
            }
        } catch (final BadRequestException e) {
            return () -> printf("%s%nYour request:%n%n    %s%n%n%s%n%n",
                                TextIO.read(OldMain.class, "head.txt"),
                                argsInLine(args),
                                e.getMessage());
        }
    }

    private void run() {
        job.run();
    }
}
