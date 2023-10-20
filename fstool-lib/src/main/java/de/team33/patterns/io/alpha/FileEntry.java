package de.team33.patterns.io.alpha;

import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

/**
 * Use {@link FileProcessing#fileEntry(Path, LinkOption...)} or {@link FileProcessing#entry(Path)} to get an instance.
 */
public interface FileEntry {

    Path path();

    default boolean isDirectory() {
        return false;
    }

    default boolean isRegularFile() {
        return false;
    }

    default boolean isSymbolicLink() {
        return false;
    }

    default boolean isOther() {
        return false;
    }

    default boolean exists() {
        return false;
    }

    default Instant lastModified() {
        throw new UnsupportedOperationException("a non existing file cannot have a timestamp");
    }

    default Instant lastAccess() {
        throw new UnsupportedOperationException("a non existing file cannot have a timestamp");
    }

    default Instant creation() {
        throw new UnsupportedOperationException("a non existing file cannot have a timestamp");
    }

    default long size() {
        throw new UnsupportedOperationException("a non existing file cannot have a size");
    }

    default List<FileEntry> content() {
        throw new UnsupportedOperationException("only directories can have a content");
    }
}
