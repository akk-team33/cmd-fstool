package de.team33.patterns.io.alpha;

import de.team33.patterns.lazy.narvi.Lazy;

import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

public class FileInfo {

    private static final FileProcessing PROCESSING = FileProcessing.with(LinkOption.NOFOLLOW_LINKS); // TODO?
    private static final Comparator<FileEntry> ORDER = Comparator.comparing(entry -> entry.path()
                                                                                          .getFileName()
                                                                                          .toString());

    private final String name;
    private final Type type;
    private final List<FileInfo> content;
    private final long size;
    private final Instant lastModified;
    private final transient Lazy<List<Object>> list;
    private final transient Lazy<Integer> hash;

    private FileInfo(final FileEntry entry) {
        this.name = entry.path().getFileName().toString();
        this.type = Type.of(entry);
        this.content = entry.isDirectory()
                ? entry.content()
                         .stream()
                         .sorted(ORDER)
                         .map(FileInfo::new)
                         .toList()
                : List.of();
        this.size = entry.isDirectory()
                ? content.stream()
                         .map(info -> info.size)
                         .reduce(0L, Long::sum)
                : entry.exists() ? entry.size() : 0L;
        this.lastModified = entry.isDirectory()
                ? content.stream()
                         .map(info -> info.lastModified)
                         .filter(Objects::nonNull)
                         .max(Instant::compareTo)
                         .orElse(null)
                : entry.exists() ? entry.lastModified() : null;
        this.list = Lazy.init(() -> Arrays.asList(name, content, size, lastModified));
        this.hash = Lazy.init(() -> list.get().hashCode());
    }

    private static String noDetail(final Long size, final Instant lastModified) {
        return "";
    }

    private static String regularDetail(final Long size, final Instant lastModified) {
        return String.format(" (%,d, %s)", size, lastModified);
    }

    private static String noTail(final Integer indent, final List<FileInfo> content) {
        return "";
    }

    private static String dirTail(Integer indent, List<FileInfo> content) {
        if (content.isEmpty()) {
            return " {}";
        } else {
            return String.format(" {%s%s}", dirTailBody(content, indent + 1), newLine(indent));
        }
    }

    private static String dirTailBody(final List<FileInfo> content, final int indent) {
        final String newLine = newLine(indent);
        return content.stream()
                      .map(fi -> fi.toString(indent))
                      .collect(Collectors.joining(newLine, newLine, ""));
    }

    private static String newLine(final int indent) {
        return String.format("%n%s", Stream.generate(() -> "    ")
                                           .limit(indent)
                                           .collect(Collectors.joining()));
    }

    public static FileInfo of(final Path path) {
        return new FileInfo(PROCESSING.entry(path));
    }

    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof FileInfo) && list.get().equals(((FileInfo) obj).list.get()));
    }

    @Override
    public final int hashCode() {
        return hash.get();
    }

    @Override
    public final String toString() {
        return toString(0);
    }

    public final String toString(final int indent) {
        return String.format("%s : %s%s%s;",
                             name, type, type.details.apply(size, lastModified), type.toTail.apply(indent, content));
    }

    public enum Type {

        REGULAR(FileEntry::isRegularFile, FileInfo::regularDetail, FileInfo::noTail),
        DIRECTORY(FileEntry::isDirectory, FileInfo::regularDetail, FileInfo::dirTail),
        SYMLINK(FileEntry::isSymbolicLink, FileInfo::regularDetail, FileInfo::noTail),
        OTHER(FileEntry::exists, FileInfo::noDetail, FileInfo::noTail),
        MISSING(not(FileEntry::exists), FileInfo::noDetail, FileInfo::noTail);

        private final Predicate<FileEntry> filter;
        private final BiFunction<Long, Instant, String> details;
        private final BiFunction<Integer, List<FileInfo>, String> toTail;

        Type(final Predicate<FileEntry> filter,
             final BiFunction<Long, Instant, String> details,
             final BiFunction<Integer, List<FileInfo>, String> toTail) {
            this.filter = filter;
            this.details = details;
            this.toTail = toTail;
        }

        static Type of(final FileEntry entry) {
            return Stream.of(values())
                         .filter(type -> type.filter.test(entry))
                         .findAny()
                         .orElseThrow(() -> new NoSuchElementException("Unknown type: " + entry.path()));
        }
    }
}
