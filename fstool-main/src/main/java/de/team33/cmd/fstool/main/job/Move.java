package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.BadRequestException;
import de.team33.cmd.fstool.main.common.Context;
import de.team33.cmd.fstool.main.move.Resolver;
import de.team33.cmd.fstool.main.move.ResolverException;
import de.team33.patterns.io.phobos.FileEntry;
import de.team33.patterns.io.phobos.FileIndex;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Move implements Runnable {

    private static final String HELP_FORMAT = //
            "%1$sExpected request scheme:%n" +
            "%n" +
            "    %2$s move PATH RULE [-r [XCLD_DIR [XCLD_DIR [...]]]]%n" +
            "%n" +
            "    to relocate all regular files located directly* in a given%n" +
            "    working directory.%n" +
            "%n" +
            "    *Optionally, regular files located in subdirectories are also processed.%n" +
            "     Empty subdirectories will finally be removed.%n" +
            "%n" +
            "Required arguments:%n" +
            "%n" +
            "    PATH : a path to the working directory.%n" +
            "    RULE : a processing rule that has a similar structure to a%n" +
            "           relative path, but which can contain TOKENs that are%n" +
            "           interpreted during processing.%n" +
            "%n" +
            "      TOKENs (case sensitive!):%n" +
            "%n" +
            "          $Y -> the year the file in question was last modified.%n" +
            "          $M -> the month the file in question was last modified.%n" +
            "          $D -> the day the file in question was last modified.%n" +
            "          $X -> the filename extension of the file in question.%n" +
            "          $N -> the filename without extension of the file in question.%n" +
            "          $F -> the full filename (with extension) of the file in question.%n" +
            "          $P -> the relative path from the working directory up to the%n" +
            "                parent of the file in question.%n" +
            "          $# -> a hash value over the content of the file in question.%n" +
            "          $$ -> The dollar symbol%n" +
            "%n" +
            "Optional arguments:%n" +
            "%n" +
            "    -r | -R :  recursive processing of subdirectories.%n" +
            // TODO "               Empty directories will finally be removed.%n" +
            "    XCLD_DIR : for recursive processing you may wish to exclude some%n" +
            "               directories from processing.";

    private final Context context;
    private final Supplier<Stream<FileEntry>> toStream;
    private final Path mainPath;
    private final Resolver resolver;
    private final Set<Path> exclusions;

    private Move(final Context context, final String shellCmd, final boolean recursive,
                 final List<String> mainArgs, final List<String> optArgs) {
        this.context = context;
        this.toStream = recursive ? this::deepStream : this::flatStream;
        this.mainPath = Paths.get(mainArgs.get(0)).toAbsolutePath().normalize();
        this.exclusions = optArgs.stream()
                                 .map(mainPath::resolve)
                                 .map(Path::normalize)
                                 .collect(Collectors.toSet());
        try {
            this.resolver = Resolver.parse(mainArgs.get(1));
        } catch (final ResolverException caught) {
            final String problem = String.format("Problem:%n%n    %s%n%n", caught.getMessage());
            throw new BadRequestException(String.format(HELP_FORMAT, problem, shellCmd));
        }

        if (!Files.isDirectory(mainPath, LinkOption.NOFOLLOW_LINKS)) {
            final String problem = String.format("Problem:%n%n    not a directory:%n    %s%n%n", mainPath);
            throw new BadRequestException(String.format(HELP_FORMAT, problem, shellCmd));
        }
    }

    public static Runnable runnable(final Context context, final String shellCmd, final List<String> args) {
        final int size = args.size();
        if (2 < size && "-r".equalsIgnoreCase(args.get(2))) {
            return new Move(context, shellCmd, true, args.subList(0, 2), args.subList(3, size));
        } else if (2 == size) {
            return new Move(context, shellCmd, false, args, List.of());
        }
        throw new BadRequestException(String.format(HELP_FORMAT, "", shellCmd));
    }

    private Stream<FileEntry> flatStream() {
        return FileEntry.of(mainPath, LinkOption.NOFOLLOW_LINKS)
                        .content()
                        .stream()
                        .map(path -> FileEntry.of(path, LinkOption.NOFOLLOW_LINKS));
    }

    private Stream<FileEntry> deepStream() {
        return FileIndex.of(mainPath, LinkOption.NOFOLLOW_LINKS)
                        .skipPath(exclusions::contains)
                        .stream();
    }

    @Override
    public void run() {
        context.printf("%s ...%n", mainPath);
        toStream.get()
                .filter(FileEntry::isRegularFile)
                .map(FileEntry::path)
                .forEach(this::move);
        Clean.runnable(context, Move.class.getSimpleName(), List.of(mainPath.toString()))
             .run();
    }

    private void move(final Path path) {
        context.printf("%s ...%n", path);
        final Path newPath = mainPath.resolve(resolver.resolve(mainPath, path)).normalize();
        context.printf("--> %s ... ", newPath);

        if (path.equals(newPath)) {
            context.printf("nothing to do%n");
            return;
        }

        //noinspection DuplicatedCode
        try {
            Files.createDirectories(newPath.getParent());
            Files.move(path, newPath);
            context.printf("ok%n");
        } catch (final Error e) {
            context.printf("Error!%n");
            throw e;
        } catch (final Throwable e) {
            context.printf("failed:%n    %s%n    %s%n", e.getClass().getCanonicalName(), e.getMessage());
        }
    }
}
