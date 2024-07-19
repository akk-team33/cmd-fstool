package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.cmd.fstool.main.common.JobException;
import de.team33.cmd.fstool.main.common.Resolving;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class DCopy implements Runnable {

    public static final String EXCERPT = "Copy the subdirectory structure from one directory to another.";

    private final Context context;
    private final Path srcPath;
    private final Path tgtPath;

    private DCopy(final Context context, final List<String> args) throws JobException {
        if (args.size() != 4) {
            throw newJobException(null);
        }
        final Path srcPath = Paths.get(args.get(2));
        if (!Files.isDirectory(srcPath)) {
            throw newJobException("source path <" + srcPath + "> is not a directory");
        }
        final Path tgtPath = Paths.get(args.get(3));
        if (Files.exists(tgtPath) && !Files.isDirectory(tgtPath)) {
            throw newJobException("target path <" + tgtPath + "> exists but is not a directory");
        }
        this.context = context;
        this.srcPath = srcPath.toAbsolutePath().normalize();
        this.tgtPath = tgtPath.toAbsolutePath().normalize();
    }

    private static JobException newJobException(final String message) {
        return new JobException(DCopy.class, message);
    }

    public static Runnable job(final Context context, final List<String> args) {
        return Resolving.job(context, args, DCopy::new);
    }

    @Override
    public void run() {
        context.printf("SOURCE: %s%n", srcPath);
        context.printf("TARGET: %s%n", tgtPath);
        context.printf("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -%n");
        if (!Files.exists(tgtPath, LinkOption.NOFOLLOW_LINKS)) {
            context.printf("creating: %s ... ", tgtPath);
            final String result = create(tgtPath);
            context.printf("%s%n", result);
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
            final String result = create(target);
            context.printf("%s%n", result);
            copyAll(source);
        }
    }

    private static String create(final Path target) {
        try {
            Files.createDirectories(target);
            return "ok";
        } catch (final IOException e) {
            return String.format("failed:%n    %s%n    (%s)", e.getMessage(), e.getClass().getCanonicalName());
        }
    }
}
