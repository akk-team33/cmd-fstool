package de.team33.patterns.testing.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileIO {

    /**
     * Copy a resource file to a given target directory.
     */
    public static void copy(final Class<?> refClass, final String rsrcName, final Path target) {
        try (final InputStream in = refClass.getResourceAsStream(rsrcName)) {
            copy(in, target);
        } catch (final IOException | NullPointerException e) {
            throw new IllegalStateException("cannot copy resource <" + rsrcName + ">", e);
        }
    }

    /**
     * Copy a source file to a given target directory.
     */
    public static void copy(final Path source, final Path target) {
        try (final InputStream in = Files.newInputStream(source)) {
            copy(in, target);
            Files.setLastModifiedTime(target, Files.getLastModifiedTime(source));
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * Copy a source input stream to a given target directory.
     */
    public static void copy(final InputStream in, final Path target) throws IOException {
        Files.createDirectories(target.getParent());
        Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
    }
}
