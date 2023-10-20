package de.team33.patterns.test.testing.io;

import de.team33.patterns.testing.io.FileIO;
import de.team33.patterns.testing.io.ZipIO;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ZipIOTest {

    private static final Path TEST_PATH = Path.of("target", "testing")
                                              .toAbsolutePath()
                                              .normalize();
    private static final Path TARGET_PATH = TEST_PATH.resolve(UUID.randomUUID().toString());
    private static final String RESOURCE_ZIP = "ZipIOTest.zip";
    private static final String RESOURCE_NO_ZIP = "FileIOTest.file";

    @RepeatedTest(2) // must work no matter if already existing or not ...
    final void unzip_rsrc() {
        ZipIO.unzip(ZipIOTest.class, RESOURCE_ZIP, TARGET_PATH);
        assertTrue(Files.isDirectory(TARGET_PATH.resolve("fstool-testing")));
        assertTrue(Files.isDirectory(TARGET_PATH.resolve("fstool-testing")
                                                .resolve("src")));
        assertTrue(Files.isRegularFile(TARGET_PATH.resolve("fstool-testing")
                                                  .resolve("pom.xml")));
    }

    @Test
    final void unzip_rsrc_noZip() {
        final Path targetPath = TEST_PATH.resolve(UUID.randomUUID().toString());
        try {
            ZipIO.unzip(ZipIOTest.class, RESOURCE_NO_ZIP, targetPath);
            fail("should fail - but unzipped to <" + targetPath + ">");
        } catch (final IllegalStateException e) {
            // as expected, and ...
            assertTrue(e.getMessage().contains(RESOURCE_NO_ZIP));
        }
    }

    @Test
    final void unzip_file() {
        final Path sourcePath = TEST_PATH.resolve(UUID.randomUUID().toString()).resolve(RESOURCE_ZIP);
        FileIO.copy(ZipIOTest.class, RESOURCE_ZIP, sourcePath);
        final Path targetPath = TEST_PATH.resolve(UUID.randomUUID().toString());
        ZipIO.unzip(sourcePath, targetPath);
        assertTrue(Files.isDirectory(targetPath.resolve("fstool-testing")));
        assertTrue(Files.isDirectory(targetPath.resolve("fstool-testing")
                                               .resolve("src")));
        assertTrue(Files.isRegularFile(targetPath.resolve("fstool-testing")
                                                 .resolve("pom.xml")));
    }

    @Test
    final void unzip_file_noZip() {
        final Path sourcePath = TEST_PATH.resolve(UUID.randomUUID().toString()).resolve(RESOURCE_ZIP);
        FileIO.copy(ZipIOTest.class, RESOURCE_NO_ZIP, sourcePath);
        final Path targetPath = TEST_PATH.resolve(UUID.randomUUID().toString());
        try {
            ZipIO.unzip(sourcePath, targetPath);
            fail("should fail - but unzipped to <" + targetPath + ">");
        } catch (final IllegalStateException e) {
            // as expected, and ...
            assertTrue(e.getMessage().contains(sourcePath.toString()));
        }
    }
}
