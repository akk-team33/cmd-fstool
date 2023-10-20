package de.team33.cmd.fstool.main.move;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Pattern1Test {

    private static final Pattern PATTERN = Pattern.compile("\\$.");

    @ParameterizedTest
    @EnumSource
    void parse(final Case testCase) {
        final String segment = testCase.segment;
        final List<String> expected = testCase.expected;
        assertEquals(expected, parsed(segment));
    }

    static List<String> parsed(final String segment) {
        final Matcher matcher = PATTERN.matcher(segment);
        final List<String> result = new LinkedList<>();
        final Iterator<MatchResult> matches = matcher.results().toList().iterator();
        int start = 0;
        while (matches.hasNext()) {
            final MatchResult head = matches.next();
            result.add(segment.substring(start, head.start()));
            result.add(segment.substring(head.start(), head.end()));
            start = head.end();
        }
        result.add(segment.substring(start));
        return List.copyOf(result);
    }

    @SuppressWarnings("unused")
    enum Case {
        EMPTY("", List.of("")),
        PLAIN_ONLY("plain", List.of("plain")),
        TOKEN_ONLY("$X", List.of("", "$X", "")),
        TK_TK("$F$X", List.of("", "$F", "", "$X", "")),
        TK_PL_TK("$F.$X", List.of("", "$F", ".", "$X", "")),
        TK_TK_PL("$F$Xpostfix", List.of("", "$F", "", "$X", "postfix")),
        PL_TK_TK("prefix$F$X", List.of("prefix", "$F", "", "$X", "")),
        TK_PL_TK_PL("$F.$Xpostfix", List.of("", "$F", ".", "$X", "postfix")),
        PL_TK_PL_TK("prefix$F.$X", List.of("prefix", "$F", ".", "$X", "")),
        PL_TK_TK_PL("prefix$F$Xpostfix", List.of("prefix", "$F", "", "$X", "postfix")),
        PL_TK_PL_TK_PL("prefix$F.$Xpostfix", List.of("prefix", "$F", ".", "$X", "postfix"));

        final String segment;
        final List<String> expected;

        Case(String segment, List<String> expected) {
            this.segment = segment;
            this.expected = expected;
        }
    }
}
