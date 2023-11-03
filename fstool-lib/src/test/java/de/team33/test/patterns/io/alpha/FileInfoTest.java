package de.team33.test.patterns.io.alpha;

import de.team33.patterns.io.alpha.FileInfo;
import de.team33.patterns.io.alpha.TextIO;
import de.team33.patterns.testing.titan.io.ZipIO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileInfoTest {

    private static final Path TEST_PATH = Path.of("target", "testing", UUID.randomUUID().toString())
                                              .toAbsolutePath()
                                              .normalize();
    private static final Path FIRST_PATH = TEST_PATH.resolve("first");
    private static final Path SECOND_PATH = TEST_PATH.resolve("second");

    @BeforeAll
    static void init() {
        ZipIO.unzip(FileInfoTest.class, "/fstool-main.zip", FIRST_PATH);
        ZipIO.unzip(FileInfoTest.class, "/fstool-main.zip", SECOND_PATH);
    }

    @Test
    final void testToString() {
        final FileInfo result = FileInfo.of(FIRST_PATH);
        final String expected = TextIO.read(FileInfoTest.class, "/fstool-main.FileInfo.txt");
        assertEquals(expected, result.toString());
    }

    @Test
    final void testEquals() {
        final FileInfo expected = FileInfo.of(FIRST_PATH.resolve("fstool-main"));
        final FileInfo result = FileInfo.of(SECOND_PATH.resolve("fstool-main"));
        assertEquals(expected, result);
    }

    @Test
    final void testHashCode() {
        final FileInfo expected = FileInfo.of(FIRST_PATH.resolve("fstool-main"));
        final FileInfo result = FileInfo.of(SECOND_PATH.resolve("fstool-main"));
        assertEquals(expected.hashCode(), result.hashCode());
    }
}
