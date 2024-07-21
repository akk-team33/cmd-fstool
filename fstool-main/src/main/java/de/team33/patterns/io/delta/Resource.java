package de.team33.patterns.io.delta;

import de.team33.patterns.exceptional.dione.XFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Resource {

    private static final String CANNOT_READ_RESOURCE = "cannot read resource%n" +
            "    resource name   : %s%n" +
            "    referring class : %s%n" +
            "    cause           : %s%n" +
            "                      (%s)%n";

    private final Class<?> refClass;
    private final String name;

    private Resource(final Class<?> refClass, final String name) {
        this.refClass = refClass;
        this.name = name;
    }

    public static Resource by(final Class<?> refClass, final String name) {
        return new Resource(refClass, name);
    }

    private static <R> R readText(final InputStream in,
                                  final XFunction<BufferedReader, R, IOException> function) throws IOException {
        try (final Reader rin = new InputStreamReader(in, StandardCharsets.UTF_8);
             final BufferedReader bin = new BufferedReader(rin)) {
            return function.apply(bin);
        }
    }

    private static Properties readProperties(final Reader in) throws IOException {
        final Properties result = new Properties();
        result.load(in);
        return result;
    }

    public final <R> R readBin(final XFunction<InputStream, R, IOException> function) {
        try (final InputStream in = refClass.getResourceAsStream(name)) {
            return function.apply(in);
        } catch (final NullPointerException | IOException e) {
            throw new IllegalStateException(String.format(CANNOT_READ_RESOURCE,
                                                          name, refClass,
                                                          e.getMessage(), e.getClass().getCanonicalName()), e);
        }
    }

    public final <R> R readText(final XFunction<BufferedReader, R, IOException> function) {
        return readBin(in -> readText(in, function));
    }

    public final Properties toProperties() {
        return readText(Resource::readProperties);
    }
}
