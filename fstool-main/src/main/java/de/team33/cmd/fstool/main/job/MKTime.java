package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.BadRequestException;
import de.team33.cmd.fstool.main.common.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.List;

public class MKTime implements Runnable {

    private static final String HELP_FORMAT = //
            "Expected request scheme:%n" +
                    "%n" +
                    "    %s mkdate TGT_PATH%n" +
                    "%n" +
                    "    to create a subdirectory named according to the current local time%n" +
                    "    in a given target directory. The target directory will be created%n" +
                    "    if it does not currently exist.%n" +
                    "%n" +
                    "Required argument:%n" +
                    "%n" +
                    "    TGT_PATH: a path to the target directory (may not exist yet).";

    private final Context context;
    private final Path tgtPath;

    public MKTime(final Context context, final String shellCmd, final List<String> args) {
        if (1 != args.size()) {
            throw new BadRequestException(String.format(HELP_FORMAT, shellCmd));
        }
        this.context = context;
        this.tgtPath = Paths.get(args.get(0)).toAbsolutePath().normalize();
    }

    public static Runnable runnable(final Context context, final List<String> args) {
        return new MKTime(context, args.get(0), args.subList(1, args.size()));
    }

    public static Runnable runnable(final Context context, final String shellCmd, final List<String> args) {
        return new MKTime(context, shellCmd, args);
    }

    @Override
    public void run() {
        final LocalTime now = LocalTime.now();
        final String time = String.format("%02d-%02d-%02d", now.getHour(), now.getMinute(), now.getSecond());
        try {
            context.printf("Creating directory %s%n    in %s ... ", time, tgtPath);
            Files.createDirectories(tgtPath.resolve(time));
            context.printf("ok%n");
        } catch (final IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
