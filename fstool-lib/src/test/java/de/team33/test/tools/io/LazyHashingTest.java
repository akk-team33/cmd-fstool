package de.team33.test.tools.io;

import de.team33.patterns.testing.io.FileIO;
import de.team33.tools.io.StrictHashing;
import de.team33.tools.io.LazyHashing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LazyHashingTest {

    private static final Path TEST_PATH = Paths.get("target", "testing", LazyHashingTest.class.getSimpleName());
    private static final String TEST_FILE = "HasherTest.file";

    private Path testPath;

    @BeforeEach
    final void beforeEach() throws IOException {
        this.testPath = TEST_PATH.resolve(UUID.randomUUID().toString()).resolve(TEST_FILE);
        Files.createDirectories(testPath.getParent());
        FileIO.copy(LazyHashingTest.class, TEST_FILE, testPath);
    }

    @ParameterizedTest
    @EnumSource
    void algorithm(final StrictHashing fileHashing) {
        final LazyHashing lazyHashing = LazyHashing.of(fileHashing);
        assertEquals(fileHashing.algorithm(), lazyHashing.algorithm());
    }

    @ParameterizedTest
    @EnumSource
    void resultLength(final StrictHashing fileHashing) {
        final LazyHashing lazyHashing = LazyHashing.of(fileHashing);
        final String result = lazyHashing.hash(testPath);
        assertEquals(lazyHashing.resultLength(), result.length());
    }

    @ParameterizedTest
    @EnumSource
    void hash_normal(final StrictHashing fileHashing) {
        final LazyHashing lazyHashing = LazyHashing.of(fileHashing);
        final String expected = fileHashing.hash(testPath);

        final String result = lazyHashing.hash(testPath);

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @EnumSource
    void hash_from_filename(final StrictHashing fileHashing) throws IOException {
        final LazyHashing hashing = LazyHashing.of(fileHashing);
        final String expected = hashing.hash(testPath);
        final Path copyPath = testPath.getParent().resolve("#" + expected + ".file");
        Files.writeString(copyPath, UUID.randomUUID().toString(), StandardCharsets.UTF_8);

        // when ...
        final String result = hashing.hash(copyPath);

        // then ...
        assertEquals(expected, result);
    }
}