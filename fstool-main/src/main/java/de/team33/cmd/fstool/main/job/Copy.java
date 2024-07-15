package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.BadRequestException;
import de.team33.cmd.fstool.main.common.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

public class Copy implements Runnable {

    private static final String PROBLEM_FMT = //
            "Problem:%n" +
            "%n" +
            "    %s%n" +
            "%n";
    private static final String HELP_FORMAT = //
            "%sExpected request scheme:%n" +
            "%n" +
            "    %s copy SRC_PATH TGT_PATH%n" +
            "%n" +
            "    to copy the files from a source directory%n" +
            "    to a target directory. The target directory will be created%n" +
            "    if it does not currently exist. Existing files or subdirectories%n" +
            "    in the target directory not present in the source remain unaffected.%n" +
            "%n" +
            "Required arguments:%n" +
            "%n" +
            "    SRC_PATH: a path to the source directory.%n" +
            "    TGT_PATH: a path to the target directory (may not exist yet).";

    private final Context context;
    private final Path srcPath;
    private final Path tgtPath;

    public Copy(final Context context, final String shellCmd, final List<String> args) {
        if (2 != args.size()) {
            throw new BadRequestException(String.format(HELP_FORMAT, "", shellCmd));
        }

        this.context = context;
        this.srcPath = Paths.get(args.get(0)).toAbsolutePath().normalize();
        this.tgtPath = Paths.get(args.get(1)).toAbsolutePath().normalize();

        if (!Files.isDirectory(srcPath)) {
            final String problem = String.format(PROBLEM_FMT, "not a directory: " + args.get(0));
            throw new BadRequestException(String.format(HELP_FORMAT, problem, shellCmd));
        }

        if (Files.exists(tgtPath, LinkOption.NOFOLLOW_LINKS)
            && !Files.isDirectory(tgtPath, LinkOption.NOFOLLOW_LINKS)) {
            final String problem = String.format(PROBLEM_FMT, "not a directory: " + args.get(1));
            throw new BadRequestException(String.format(HELP_FORMAT, problem, shellCmd));
        }
    }

    public static Runnable runnable(final Context context, final List<String> args) {
        return new Copy(context, args.get(0), args.subList(1, args.size()));
    }

    public static Runnable runnable(final Context context, final String shellCmd, final List<String> args) {
        return new Copy(context, shellCmd, args);
    }

    @Override
    public void run() {
        context.printf("SOURCE: %s%n", srcPath);
        context.printf("TARGET: %s%n", tgtPath);
        context.printf("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -%n");
        copyAll(srcPath, tgtPath);
    }

    private void copyAll(final Path source, final Path target) {
        try {
            Files.createDirectories(target);
            try (final Stream<Path> stream = Files.list(source)) {
                stream.forEach(this::copy);
            } catch (final IOException e) {
                context.printf("%s - failed to read source directory:%n    %s%n    (%s)",
                               source, e.getMessage(), e.getClass().getCanonicalName());
            }
        } catch (final IOException e) {
            context.printf("%s - failed to create target directory:%n    %s%n    (%s)",
                           target, e.getMessage(), e.getClass().getCanonicalName());
        }
    }

    private final BasicFileAttributes readAttributes(final Path path) {
        try {
            return Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
        } catch (final IOException e) {
            return null;
        }
    }

    private String copyRegular(final Path source, final BasicFileAttributes sAttribs, final Path target) {
        final BasicFileAttributes tAttribs = readAttributes(target);
        if (null != tAttribs) {
            if (!tAttribs.isRegularFile()) {
                return "target exists but is not a regular file";
            }
            final Instant sTime = sAttribs.lastModifiedTime().toInstant();
            final Instant tTime = tAttribs.lastModifiedTime().toInstant();
            final long delta = Math.abs(sTime.until(tTime, ChronoUnit.MILLIS));
            //context.printf("sTime: %s - tTime: %s - delta: %d%n", sTime, tTime, delta);
            if (1000L >= delta && sAttribs.size() == tAttribs.size()) {
                return "target already exists - not updated";
            }
        }
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            Files.setLastModifiedTime(target, sAttribs.lastModifiedTime());
            return "ok";
        } catch (IOException e) {
            return "failed: " + e.getMessage() + " (" + e.getClass().getCanonicalName() + ")";
        }
    }

    private void copy(final Path source) {
        final Path relative = srcPath.relativize(source);
        final Path target = tgtPath.resolve(relative);
        final BasicFileAttributes sAttribs = readAttributes(source);
        if (null == sAttribs) {
            context.printf("%s - error: source is missing!?%n", relative);
        } else if (sAttribs.isRegularFile()) {
            final String result = copyRegular(source, sAttribs, target);
            context.printf("%s - %s%n", relative, result);
        } else if (sAttribs.isDirectory()) {
            copyAll(source, target);
        } else {
            context.printf("%s - skipped: not a regular file%n", relative);
        }
    }
}
