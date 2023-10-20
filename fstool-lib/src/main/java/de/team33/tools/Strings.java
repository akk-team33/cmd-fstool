package de.team33.tools;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Strings {

    public static final String NEW_LINE = String.format("%n");

    private Strings() {
    }

    public static String concat(final String delimiter, final String... fragments) {
        return Stream.of(fragments)
                     .filter(Objects::nonNull)
                     .collect(Collectors.joining(delimiter));
    }
}
