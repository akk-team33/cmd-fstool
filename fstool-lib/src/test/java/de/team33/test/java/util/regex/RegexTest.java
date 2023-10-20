package de.team33.test.java.util.regex;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegexTest {

    private static final String REGEX = "[^#]*#[0123456789abcdefABCDEF]{40}(\\.[^.]*){0,1}";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    @ParameterizedTest
    @EnumSource
    final void test(final Case testCase) {
        final boolean result = PATTERN.matcher(testCase.sample).matches();
        assertEquals(testCase.expected, result);
    }

    enum Case {
        MATCHING_01("#32d10c7b8cf96570ca04ce37f2a19d84240d3a89", true),
        NO_DIGIT_01("#32d10c7h8cf96570ca04ce37f2a19d84240d3a89", false),
        TOO_SHRT_01("#32d10c7b8cf96570ca04ce37f2a19d84240d3a8", false),
        TOO_LONG_01("#32d10c7b8cf96570ca04ce37f2a19d84240d3a89a", false),
        NO_HASH_01("32d10c7b8cf96570ca04ce37f2a19d84240d3a89", false),
        MATCHING_02("Prefix#32d10c7b8cf96570ca04ce37f2a19d84240d3a89", true),
        NO_DIGIT_02("Prefix#32d10c7b8cf9G570ca04ce37f2a19d84240d3a89", false),
        TOO_SHRT_02("Prefix#32d10c7b8cf96570ca04ce37f2a19d84240d3a8", false),
        TOO_LONG_02("Prefix#32d10c7b8cf96570ca04ce37f2a19d84240d3a89a", false),
        NO_HASH_02("Prefix32d10c7b8cf96570ca04ce37f2a19d84240d3a89", false),
        MATCHING_03("Prefix.#32d10c7b8cf96570ca04ce37f2a19d84240d3a89.jpg", true),
        NO_EXT_03("Prefix.#32d10c7b8cf96570ca04ce37f2a19d84240d3a89_jpg", false),
        MORE_EXT_03("Prefix.#32d10c7b8cf96570ca04ce37f2a19d84240d3a89.jpg.bak", false),
        MATCHING_04("#32d10c7b8cf96570ca04ce37f2a19d84240d3a89.jpg", true),
        NO_EXT_04("#32d10c7b8cf96570ca04ce37f2a19d84240d3a89_jpg", false),
        MORE_EXT_04("#32d10c7b8cf96570ca04ce37f2a19d84240d3a89.jpg.bak", false);

        final String sample;
        final boolean expected;

        Case(final String sample, final boolean expected) {
            this.sample = sample;
            this.expected = expected;
        }
    }
}
