package de.team33.patterns.io.delta;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceTest {

    private static final String RESOURCE_NAME = ResourceTest.class.getSimpleName() + ".txt";

    private static final Path RESOURCE_PATH = Paths.get("target",
                                                        "testing",
                                                        ResourceTest.class.getSimpleName(),
                                                        UUID.randomUUID().toString(),
                                                        RESOURCE_NAME);

    @BeforeAll
    static void beforeAll() throws IOException {
        Files.createDirectories(RESOURCE_PATH.getParent());
        Resource.by(ResourceTest.class, RESOURCE_NAME)
                .readByteStream(in -> Files.copy(in, RESOURCE_PATH));
    }

    @ParameterizedTest
    @EnumSource
    void readText(final TestCase testCase) {
        final String expected = String.format("p1=v1%np2=v2%np3=v3");
        final Resource subject = testCase.newResource.get();

        final String result = subject.readText();

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @EnumSource
    final void readProperties(final TestCase testCase) {
        final Map<?,?> expected = new HashMap<>() {{
            put("p1", "v1");
            put("p2", "v2");
            put("p3", "v3");
        }};
        final Resource subject = testCase.newResource.get();

        final Properties result = subject.readProperties();
        //result.store(System.out, "no comment ;-)");

        assertEquals(expected, result);
    }

    enum TestCase {
        BY_RESOURCE(() -> Resource.by(ResourceTest.class, RESOURCE_NAME)),
        BY_FILE(() -> Resource.by(RESOURCE_PATH));

        private final Supplier<Resource> newResource;

        TestCase(Supplier<Resource> newResource) {
            this.newResource = newResource;
        }
    }
}
