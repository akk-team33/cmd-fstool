package de.team33.cmd.fstool.main.move;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Pattern2Test {

    private static final Pattern PATTERN = Pattern.compile("[\\\\/]");

    @ParameterizedTest
    @EnumSource
    void parse(final Case testCase) {
        final String path = testCase.path;
        final List<String> expected = testCase.expected;
        final List<String> result = PATTERN.splitAsStream(path).toList();
        assertEquals(expected, result);
    }

    enum Case {
        EMPTY("", List.of("")),
        NO_MATCH("plain", List.of("plain")),
        SINGLE_SLASH("pre/post", List.of("pre", "post")),
        SINGLE_BACKSLASH("pre\\post", List.of("pre", "post")),
        SLASH_BACKSLASH("pre/mid\\post", List.of("pre", "mid", "post")),
        BACKSLASH_SLASH("pre\\mid/post", List.of("pre", "mid", "post"));

        final String path;
        final List<String> expected;

        Case(final String path, final List<String> expected) {
            this.path = path;
            this.expected = expected;
        }
    }
}
