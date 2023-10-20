package de.team33.cmd.fstool.main.move;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SegmentTest {

    private static final Path TEST_PATH = Path.of("target", "testing", SegmentTest.class.getSimpleName())
                                              .toAbsolutePath()
                                              .normalize();
    private static final String EXTENSION = "ext";
    private static final String DOT_EXTENSION = "." + EXTENSION;

    @ParameterizedTest
    @EnumSource
    void parse_name_ext(final Case testCase) {
        final Path path = TEST_PATH.resolve(UUID.randomUUID() + DOT_EXTENSION);
        final Segment segment = Segment.parse(testCase.rule);

        final String expected = testCase.expected.apply(path);
        final String result = segment.map(new FileInfo(TEST_PATH, path));

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @EnumSource
    void parse_name(final Case testCase) {
        final Path path = TEST_PATH.resolve(UUID.randomUUID().toString());
        final Segment segment = Segment.parse(testCase.rule);

        final String expected = testCase.expected.apply(path);
        final String result = segment.map(new FileInfo(TEST_PATH, path));

        assertEquals(expected, result);
    }

    @SuppressWarnings("unused")
    enum Case {
        PLAIN_ONLY("plain", path -> "plain"),
        DOLLAR("$$", path -> "$"),
        FULL_NAME("$F", path -> path.getFileName().toString()),
        NAME_ONLY("$N", Util::nameOnly),
        EXT_ONLY("$X", Util::extOnly),
        NAME_EXT("$N.$X", path -> path.getFileName().toString()),
        PRE_NAME_EXT_POST("pre.$N.$X.post", path -> "pre." + path.getFileName().toString() + ".post");

        final String rule;
        final Function<Path, String> expected;

        Case(final String rule, final Function<Path, String> expected) {
            this.rule = rule;
            this.expected = expected;
        }
    }

    private static class Util {

        static String nameOnly(final Path path) {
            final String fullName = path.getFileName().toString();
            final int index = fullName.lastIndexOf('.');
            return 0 > index ? fullName : fullName.substring(0, index);
        }

        static String extOnly(final Path path) {
            final String fullName = path.getFileName().toString();
            final int index = fullName.lastIndexOf('.');
            return 0 > index ? "" : fullName.substring(index + 1);
        }
    }
}
