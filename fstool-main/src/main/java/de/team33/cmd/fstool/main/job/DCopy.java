package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.BadRequestException;
import de.team33.cmd.fstool.main.common.Context;
import de.team33.patterns.io.deimos.TextIO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class DCopy implements Runnable {

    public static final String EXCERPT = "Copy the subdirectory structure from one directory to another.";
    private static final String PROBLEM_FMT = //
            "Problem:%n" +
            "%n" +
            "    %s%n" +
            "%n";
    private static final String HELP_FORMAT = //
            "%sExpected request scheme:%n" +
            "%n" +
            "    %s dcopy SRC_PATH TGT_PATH%n" +
            "%n" +
            "    to copy the subdirectory structure from a source directory%n" +
            "    to a target directory. The target directory will be created%n" +
            "    if it does not currently exist. Existing files or subdirectories%n" +
            "    in the target directory remain unaffected.%n" +
            "%n" +
            "Required arguments:%n" +
            "%n" +
            "    SRC_PATH: a path to the source directory.%n" +
            "    TGT_PATH: a path to the target directory (may not exist yet).";

    private final Context context;
    private final Path srcPath;
    private final Path tgtPath;

    public DCopy(final Context context, final String shellCmd, final List<String> args) {
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

    public static Runnable job(final Context context, final List<String> args) {
        if (args.size() == 4) {
            return new DCopy(context, args.get(0), args.subList(2, args.size()));
        } else {
            final String cmdLine = String.join(" ", args);
            final String cmdName = args.get(0);
            final String format = TextIO.read(DCopy.class, "DCopy.txt");
            return () -> context.printf(format, cmdLine, cmdName);
        }
    }

    @Override
    public void run() {
        context.printf("SOURCE: %s%n", srcPath);
        context.printf("TARGET: %s%n", tgtPath);
        context.printf("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -%n");
        if (!Files.exists(tgtPath, LinkOption.NOFOLLOW_LINKS)) {
            context.printf("creating: %s ... ", tgtPath);
            create(tgtPath);
            context.printf("ok%n");
        }
        copyAll(srcPath);
    }

    private void copyAll(final Path source) {
        try (final Stream<Path> stream = Files.list(source)) {
            stream.filter(Files::isDirectory)
                  .forEach(this::copy);
        } catch (final IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private void copy(final Path source) {
        final Path relative = srcPath.relativize(source);
        final Path target = tgtPath.resolve(relative);
        context.printf("creating: .../%s ... ", relative);
        // We don't want existing directories' content
        // to be changed via symbolic links -> NOFOLLOW_LINKS ...
        if (Files.isDirectory(target, LinkOption.NOFOLLOW_LINKS)) {
            context.printf("already exists%n");
            copyAll(source);
        }
        // We want symbolic links to be treated like normal files,
        // even if they don't reference anything -> NOFOLLOW_LINKS ...
        else if (Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
            context.printf("skipped (not a directory)%n");
        }
        // normal case ...
        else {
            create(target);
            context.printf("ok%n");
            copyAll(source);
        }
    }

    private static void create(final Path target) {
        try {
            Files.createDirectories(target);
        } catch (final IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
