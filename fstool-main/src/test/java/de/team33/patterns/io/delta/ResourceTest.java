package de.team33.patterns.io.delta;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ResourceTest {

    @Test
    void readBin() {
        final String result = Resource.by(getClass(), getClass().getSimpleName() + ".readBin.txt")
                                      .readBin(in -> new String(in.readAllBytes(), StandardCharsets.UTF_8));
        assertEquals(getClass().getCanonicalName(), result);
    }

    @Test
    void readText() {
        final String result = Resource.by(getClass(), getClass().getSimpleName() + ".readText.txt")
                                      .readText(BufferedReader::readLine);
        assertEquals(getClass().getCanonicalName(), result);
    }

    @Test
    void toProperties() {
        final Map<Object, Object> expected = new HashMap<>() {{
            put("value", ResourceTest.class.getCanonicalName());
        }};
        final Properties result = Resource.by(getClass(), getClass().getSimpleName() + ".toProperties.txt")
                                          .toProperties();
        assertEquals(expected, result);
    }
}