package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.BadRequestException;
import de.team33.cmd.fstool.main.common.Context;
import de.team33.patterns.io.phobos.FileEntry;
import de.team33.patterns.io.phobos.FileIndex;
import de.team33.tools.io.FileHashing;
import de.team33.tools.io.LazyHashing;
import de.team33.tools.io.StrictHashing;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.function.Predicate.not;

public class Sieve implements Runnable {

    private static final String HELP_FORMAT = //
            "%1$sExpected request scheme:%n" +
            "%n" +
            "    %2$s sieve PATH%n" +
            "%n" +
            "    to relocate all duplicated regular files located in a given%n" +
            "    working directory.%n" +
            "%n" +
            "Required arguments:%n" +
            "%n" +
            "    PATH : a path to the working directory.";

    private final Context context;
    private final Path mainPath;
    private final Path doubletPath;
    private final Path indexPath;
    private final Map<String, Entry> index;
    private final FileHashing hashing;

    private Sieve(final Context context, final String shellCmd, final String path) {
        this.context = context;
        this.mainPath = Paths.get(path).toAbsolutePath().normalize();
        this.doubletPath = Paths.get(mainPath.toString() + ".(sieved-moved)");
        this.indexPath = mainPath.resolve("(sieved-unique).txt");
        this.hashing = LazyHashing.of(StrictHashing.SHA_1);

        if (!Files.isDirectory(mainPath, LinkOption.NOFOLLOW_LINKS)) {
            final String problem = String.format("Problem:%n%n    not a directory:%n    %s%n%n", mainPath);
            throw new BadRequestException(String.format(HELP_FORMAT, problem, shellCmd));
        }

        this.index = readIndex();
    }

    public static Runnable runnable(final Context context, final String shellCmd, final List<String> args) {
        final int size = args.size();
        if (1 == size) {
            return new Sieve(context, shellCmd, args.get(0));
        } else {
            throw new BadRequestException(String.format(HELP_FORMAT, "", shellCmd));
        }
    }

    private HashMap<String, Entry> readIndex() {
        try {
            return Files.readAllLines(indexPath, StandardCharsets.UTF_8)
                        .stream()
                        .map(Entry::parse)
                        .collect(HashMap::new, (map, entry) -> map.put(entry.hash, entry), Map::putAll);
        } catch (final IOException ignored) {
            return new HashMap<>();
        }
    }

    private void writeIndex() {
        try (final BufferedWriter writer = Files.newBufferedWriter(indexPath,
                                                                   StandardOpenOption.CREATE,
                                                                   StandardOpenOption.TRUNCATE_EXISTING)) {
            for (final Entry entry : index.values()) {
                writer.append(entry.hash)
                      .append(Entry.SEPARATOR)
                      .append(entry.path.toString());
                writer.newLine();
            }
            writer.flush();
        } catch (final IOException e) {
            throw new IllegalStateException("could not write index <" + indexPath + ">", e);
        }
    }

    @Override
    public void run() {
        context.printf("%s ...%n", mainPath);
        FileIndex.of(mainPath, LinkOption.NOFOLLOW_LINKS)
                 .skipPath(doubletPath::equals)
                 .stream()
                 .filter(FileEntry::isRegularFile)
                 .map(FileEntry::path)
                 .filter(not(indexPath::equals))
                 .filter(not(this::isUnique))
                 .forEach(this::move);
        writeIndex();
    }

    private boolean isUnique(final Path path) {
        final String hash = hashing.hash(path);
        final Path relative = mainPath.relativize(path);
        if (index.containsKey(hash)) {
            return index.get(hash).path.equals(relative);
        } else {
            index.put(hash, new Entry(hash, relative));
            return true;
        }
    }

    private void move(final Path path) {
        context.printf("%s ...%n", path);
        final Path newPath = doubletPath.resolve(mainPath.relativize(path));
        context.printf("--> %s ... ", newPath);

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

    private static final class Entry {

        static final String SEPARATOR = ":";
        static final Pattern PATTERN = Pattern.compile(Pattern.quote(SEPARATOR));

        private final String hash;
        private final Path path;

        public Entry(final String hash, final Path path) {
            this.hash = hash;
            this.path = path;
        }

        static Entry parse(final String entry) {
            final String[] split = PATTERN.split(entry);
            return new Entry(split[0], Path.of(split[1]));
        }
    }
}
