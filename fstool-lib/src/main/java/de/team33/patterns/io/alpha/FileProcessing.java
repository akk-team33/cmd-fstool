package de.team33.patterns.io.alpha;

import de.team33.patterns.lazy.narvi.Lazy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class FileProcessing {

    private static final LinkOption[] NO_OPTIONS = {};

    private final BiConsumer<Path, Exception> readDirFailed;
    private final Predicate<FileEntry> readContent;
    private final LinkOption[] options;

    private FileProcessing(final BiConsumer<Path, Exception> readDirFailed,
                           final Predicate<FileEntry> readContent,
                           final LinkOption[] options) {
        this.options = options;
        this.readContent = readContent;
        this.readDirFailed = readDirFailed;
    }

    public static FileProcessing withoutOptions() {
        return with(NO_OPTIONS);
    }

    public static FileProcessing with(final LinkOption ... options) {
        return new FileProcessing((path, caught) -> {
            throw new FileProcessingException("could not read directory <" + path + ">", caught);
        }, entry -> true, options);
    }

    public static FileEntry fileEntry(final Path path, final LinkOption... options) {
        return FileProcessing.with(options).entry(path);
    }

    public final FileProcessing setReadDirectory(final Predicate<Path> readContent) {
        return setReadDirectoryEntry(entry -> readContent.test(entry.path()));
    }

    public final FileProcessing setReadDirectoryEntry(final Predicate<FileEntry> readContent) {
        return new FileProcessing(readDirFailed, readContent, options);
    }

    public final FileProcessing onReadDirFailed(final BiConsumer<Path, Exception> readDirFailed) {
        return new FileProcessing(readDirFailed, readContent, options);
    }

    public final FileEntry entry(final Path path) {
        final Path normalPath = path.toAbsolutePath().normalize();
        try {
            final BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class, options);
            return attributes.isDirectory() ? new DirEntry(normalPath, attributes) : new Entry(normalPath, attributes);
        } catch (final IOException ignored) {
            return () -> normalPath;
        }
    }

    public final Stream<FileEntry> stream(final Path path) {
        return stream(path, Integer.MAX_VALUE);
    }

    public final Stream<FileEntry> stream(final Path path, final int depth) {
        final FileEntry entry = entry(path);
        final Stream<FileEntry> result = Stream.of(entry);
        if (depth > 0 && entry.isDirectory() && readContent.test(entry)) {
            return Stream.concat(result, content(entry, depth - 1));
        } else {
            return result;
        }
    }

    private Stream<FileEntry> content(final FileEntry entry, final int depth) {
        return entry.content()
                    .stream()
                    .map(FileEntry::path)
                    .flatMap(path -> stream(path, depth));
    }

    private class DirEntry extends Entry {

        private final Lazy<List<FileEntry>> content;

        DirEntry(final Path path, final BasicFileAttributes attributes) {
            super(path, attributes);
            content = Lazy.init(() -> {
                try (final Stream<Path> paths = Files.list(path)) {
                    return paths.map(FileProcessing.this::entry)
                                .toList();
                } catch (final IOException caught) {
                    readDirFailed.accept(path, caught);
                    return List.of();
                }
            });
        }

        @Override
        public final List<FileEntry> content() {
            return content.get();
        }
    }

    private static class Entry implements FileEntry {

        private final Path path;
        private final BasicFileAttributes attributes;

        Entry(final Path path, final BasicFileAttributes attributes) {
            this.path = path;
            this.attributes = attributes;
        }

        @Override
        public final Path path() {
            return path;
        }

        @Override
        public final boolean isDirectory() {
            return attributes.isDirectory();
        }

        @Override
        public final boolean isRegularFile() {
            return attributes.isRegularFile();
        }

        @Override
        public final boolean isSymbolicLink() {
            return attributes.isSymbolicLink();
        }

        @Override
        public final boolean isOther() {
            return attributes.isOther();
        }

        @Override
        public final boolean exists() {
            return true;
        }

        public final Instant lastModified() {
            return attributes.lastModifiedTime().toInstant();
        }

        public final Instant lastAccess() {
            return attributes.lastAccessTime().toInstant();
        }

        public final Instant creation() {
            return attributes.creationTime().toInstant();
        }

        public final long size() {
            return attributes.size();
        }
    }
}
