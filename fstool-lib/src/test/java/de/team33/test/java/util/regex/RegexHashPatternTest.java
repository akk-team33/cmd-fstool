package de.team33.test.java.util.regex;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegexHashPatternTest {

    private static final String REGEX = "#[0123456789abcdefABCDEF]{8}(\\.|$)";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    @ParameterizedTest
    @EnumSource
    final void test(final Case testCase) {
        final String result = PATTERN.matcher(testCase.sample)
                                     .results()
                                     .findAny()
                                     .map(mr -> testCase.sample.substring(mr.start(), mr.end()))
                                     .orElse(null);
        assertEquals(testCase.expected, result);
    }

    enum Case {
        NOTHING("32d10c7b8cf96570ca04ce37f2a19d84240d3a89a", null),
        NOTHING_WITH_INTRO("#32d10c7b8cf96570ca04ce37f2a19d84240d3a89a", null),
        NOTHING_WITH_ENDING("32d10c7b.8cf96570ca04ce37f2a19d84240d3a89a", null),
        NOTHING_WITH_INTRO_AND_ENDING_LONG("32d10c7b#8cf96570ca04ce37f.2a19d84240d3a89a", null),
        NOTHING_WITH_INTRO_AND_ENDING_SHORT("32d10c7b8cf96570ca0#4ce37.f2a19d84240d3a89a", null),
        MINIMUM("#96570ca0", "#96570ca0"),
        PREFIXED_("32d10c7h8cf96570ca04ce37f2a19d84#240d3a89", "#240d3a89"),
        POSTFIXED("#32d10c7b.8cf96570ca04ce37f2a19d84240d3a8", "#32d10c7b."),
        NORMAL("32d10c7b8cf965#70ca04ce.37f2a19d84240d3a89a", "#70ca04ce.");

        final String sample;
        final String expected;

        Case(final String sample, final String expected) {
            this.sample = sample;
            this.expected = expected;
        }
    }
}
