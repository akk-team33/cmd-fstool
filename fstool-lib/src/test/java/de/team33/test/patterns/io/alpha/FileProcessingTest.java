package de.team33.test.patterns.io.alpha;

import de.team33.patterns.io.alpha.FileEntry;
import de.team33.patterns.io.alpha.FileProcessing;
import de.team33.patterns.testing.titan.io.TextIO;
import de.team33.patterns.testing.titan.io.ZipIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.nio.file.Path;
import java.util.UUID;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileProcessingTest {

    private static final Class<FileProcessingTest> CLASS = FileProcessingTest.class;
    private static final Path TEST_PATH = Path.of("target", "testing", CLASS.getSimpleName())
                                              .toAbsolutePath()
                                              .normalize();
    private static final String NEW_LINE = String.format("%n");

    @Test
    void stream() {
        final Case tc = Case.DEPTH_MAX;
        final Path rootPath = TEST_PATH.resolve(UUID.randomUUID().toString());
        ZipIO.unzip(FileProcessingTest.class, "/fstool-main.zip", rootPath);
        final Path mainPath = rootPath.resolve(tc.mainPath);
        final String result = FileProcessing.withoutOptions()
                                            .stream(mainPath)
                                            .map(FileEntry::path)
                                            .map(rootPath::relativize)
                                            .map(Path::toString)
                                            .sorted()
                                            .collect(joining(NEW_LINE));
        final String expected = TextIO.read(FileProcessingTest.class, tc.expected);
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @EnumSource
    void stream_with_depth(final Case tc) {
        final Path rootPath = TEST_PATH.resolve(UUID.randomUUID().toString());
        ZipIO.unzip(FileProcessingTest.class, "/fstool-main.zip", rootPath);
        final Path mainPath = rootPath.resolve(tc.mainPath);
        final String result = FileProcessing.withoutOptions()
                                            .stream(mainPath, tc.depth)
                                            .map(FileEntry::path)
                                            .map(rootPath::relativize)
                                            .map(Path::toString)
                                            .sorted()
                                            .collect(joining(NEW_LINE));
        final String expected = TextIO.read(FileProcessingTest.class, tc.expected);
        assertEquals(expected, result);
    }

    @SuppressWarnings("unused")
    enum Case {
        MISSING_0("missing", 0, "FileProcessingTest.missing.txt"),
        MISSING_3("missing", 3, "FileProcessingTest.missing.txt"),
        MISSING_MAX("missing", Integer.MAX_VALUE, "FileProcessingTest.missing.txt"),
        DEPTH_0("fstool-main", 0, "FileProcessingTest.depth.0.txt"),
        DEPTH_1("fstool-main", 1, "FileProcessingTest.depth.1.txt"),
        DEPTH_2("fstool-main", 2, "FileProcessingTest.depth.2.txt"),
        DEPTH_3("fstool-main", 3, "FileProcessingTest.depth.3.txt"),
        DEPTH_5("fstool-main", 5, "FileProcessingTest.depth.5.txt"),
        DEPTH_8("fstool-main", 8, "FileProcessingTest.depth.8.txt"),
        DEPTH_MAX("fstool-main", Integer.MAX_VALUE, "FileProcessingTest.depth.max.txt");

        final String mainPath;
        final int depth;
        final String expected;

        Case(final String mainPath, final int depth, final String expected) {
            this.mainPath = mainPath;
            this.depth = depth;
            this.expected = expected;
        }
    }
}
