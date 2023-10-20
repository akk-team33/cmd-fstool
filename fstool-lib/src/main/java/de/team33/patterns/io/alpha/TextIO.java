package de.team33.patterns.io.alpha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class TextIO {

    private static final String NEW_LINE = String.format("%n");

    public static String read(final Class<?> refClass, final String rsrcName) {
        try (final InputStream in = refClass.getResourceAsStream(rsrcName)) {
            return read(in);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static String read(final Path path) throws IOException {
        try (final InputStream in = Files.newInputStream(path)) {
            return read(in);
        }
    }

    public static String read(final InputStream in) throws IOException {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining(NEW_LINE));
        }
    }
}
