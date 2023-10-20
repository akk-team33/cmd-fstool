package de.team33.patterns.test.testing.io;

import de.team33.patterns.testing.io.FileIO;
import de.team33.patterns.testing.io.ZipIO;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class FileIOTest {

    private static final Path TEST_PATH = Path.of("target", "testing")
                                              .toAbsolutePath()
                                              .normalize();
    private static final Path SOURCE_PATH = TEST_PATH.resolve(UUID.randomUUID().toString());
    private static final Path TARGET_PATH = TEST_PATH.resolve(UUID.randomUUID().toString());
    private static final String RESOURCE_FILE = "FileIOTest.file";

    @RepeatedTest(2) // must work no matter if already existing or not ...
    final void copy_rsrc() {
        final Path target = TARGET_PATH.resolve(RESOURCE_FILE);
        FileIO.copy(FileIOTest.class, RESOURCE_FILE, target);
        assertTrue(Files.isRegularFile(target));
    }

    @Test
    final void copy_rsrc_missing() {
        final Path targetPath = TEST_PATH.resolve(UUID.randomUUID().toString())
                                         .resolve(RESOURCE_FILE);
        final String missingResource = UUID.randomUUID().toString();
        try {
            FileIO.copy(FileIOTest.class, missingResource, targetPath);
            fail("should fail - but copied to <" + targetPath + ">");
        } catch (final IllegalStateException e) {
            // as expected, and ...
            assertTrue(e.getMessage().contains(missingResource));
        }
    }

    @Test
    final void copy_file() {
        final Path sourcePath = TEST_PATH.resolve(UUID.randomUUID().toString())
                                         .resolve(RESOURCE_FILE);
        FileIO.copy(FileIOTest.class, RESOURCE_FILE, sourcePath);
        final Path targetPath = TEST_PATH.resolve(UUID.randomUUID().toString())
                                         .resolve(RESOURCE_FILE);
        FileIO.copy(sourcePath, targetPath);
        assertTrue(Files.isRegularFile(targetPath));
    }

    @Test
    final void copy_file_missing() {
        final Path sourcePath = TEST_PATH.resolve(UUID.randomUUID().toString())
                                         .resolve(RESOURCE_FILE);
        final Path targetPath = TEST_PATH.resolve(UUID.randomUUID().toString())
                                         .resolve(RESOURCE_FILE);
        try {
            FileIO.copy(sourcePath, targetPath);
            fail("should fail - but copied <" + sourcePath + "> to <" + targetPath + ">");
        } catch (final IllegalStateException e) {
            // as expected, and ...
            assertTrue(e.getMessage().contains(sourcePath.toString()));
        }
    }
}
