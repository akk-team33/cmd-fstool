package de.team33.cmd.fstool.main.move;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Pattern3Test {

    private static final Pattern PATTERN = Pattern.compile("#[0123456789abcdefABCDEF]{8}");

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
        SINGLE_MATCH("pre#268ffae23c", List.of("pre", "3c")),
        DUAL_MATCH("pre#268ffae23c#268ffae2post", List.of("pre", "3c", "post"));

        final String path;
        final List<String> expected;

        Case(final String path, final List<String> expected) {
            this.path = path;
            this.expected = expected;
        }
    }
}
