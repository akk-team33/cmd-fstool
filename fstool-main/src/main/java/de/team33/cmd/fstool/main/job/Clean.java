package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.BadRequestException;
import de.team33.cmd.fstool.main.common.Context;
import de.team33.patterns.io.phobos.FileEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public final class Clean implements Runnable {

    public static final String EXCERPT = "Remove all empty directories within a given directory substructure.";
    private static final String HELP_FORMAT = "%sExpected request scheme:%n" +
                                              "%n" +
                                              "    %s clean [-dry] PATH%n" +
                                              "%n" +
                                              "    to remove a given directory if it is empty%n" +
                                              "    after cleaning all its subdirectories.%n" +
                                              "%n" +
                                              "Option:%n" +
                                              "%n" +
                                              "    -dry: Just show what needs to be done. Remove nothing.";

    private final Context context;
    private final Mode mode;
    private final FileEntry mainEntry;

    private Clean(final Context context, final String shellCmd, final Mode mode, final List<String> args) {
        if (1 != args.size()) {
            throw new BadRequestException(String.format(HELP_FORMAT, "", shellCmd));
        }

        final Path path = Path.of(args.get(0)).toAbsolutePath().normalize();
        this.context = context;
        this.mode = mode;
        this.mainEntry = FileEntry.of(path);

        if (!mainEntry.isDirectory()) {
            final String problem = String.format("Problem:%n%n    not a directory:%n    %s%n%n", path);
            throw new BadRequestException(String.format(HELP_FORMAT, problem, shellCmd));
        }
    }

    public static Runnable job(final Context context, final List<String> args) {
        return job(context, args.get(0), args.subList(1, args.size()));
    }

    private static Runnable job(final Context context, final String shellCmd, final List<String> args) {
        if (0 < args.size() && "-dry".equalsIgnoreCase(args.get(0))) {
            return new Clean(context, shellCmd, Mode.DRY, args.subList(1, args.size()));
        } else {
            return new Clean(context, shellCmd, Mode.REAL, args);
        }
    }

    private void delete(final Path path) {
        try {
            Files.delete(path);
        } catch (final IOException caught) {
            context.printf("--> %s%n    %s%n    %s%n",
                           "could not delete <" + path + ">",
                           caught.getClass().getCanonicalName(),
                           caught.getMessage());
        }
    }

    @Override
    public void run() {
        clean(mainEntry);
    }

    private boolean clean(final FileEntry entry) {
        final List<FileEntry> content = entry.content()
                                             .stream()
                                             .map(FileEntry::of)
                                             .collect(Collectors.toCollection(ArrayList::new));
        final List<FileEntry> deleted = clean(content);
        content.removeAll(deleted);
        if (content.isEmpty()) {
            context.printf("%s ...%n", entry.path());
            mode.job.accept(this, entry.path());
            context.printf("--> %s%n", mode.done);
            return true;
        } else {
            return false;
        }
    }

    private List<FileEntry> clean(final List<FileEntry> entries) {
        return entries.stream()
                      .filter(FileEntry::isDirectory)
                      .filter(this::clean)
                      .toList();
    }

    private enum Mode {

        DRY((clean, path) -> {
        }, "to be deleted"),
        REAL(Clean::delete, "deleted");

        final BiConsumer<Clean, Path> job;
        final String done;

        Mode(final BiConsumer<Clean, Path> job, final String done) {
            this.job = job;
            this.done = done;
        }
    }
}
