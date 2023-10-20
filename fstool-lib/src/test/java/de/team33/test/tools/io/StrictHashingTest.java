package de.team33.test.tools.io;

import de.team33.patterns.testing.io.FileIO;
import de.team33.tools.io.StrictHashing;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StrictHashingTest {

    private static final Path TEST_PATH = Paths.get("target", "testing", StrictHashingTest.class.getSimpleName());
    private static final String TEST_FILE = "HasherTest.file";
    private static final Path TEMP_PATH = TEST_PATH.resolve(TEST_FILE);

    StrictHashingTest() throws IOException {
        Files.createDirectories(TEMP_PATH.getParent());
        FileIO.copy(StrictHashingTest.class, TEST_FILE, TEMP_PATH);
    }

    @ParameterizedTest
    @EnumSource
    void hash_length(final StrictHashing hashing) {
        final String result = hashing.hash(TEMP_PATH);
        assertEquals(hashing.resultLength(), result.length());
    }

    @ParameterizedTest
    @EnumSource
    void hash_result(final HashCase testCase) {
        final StrictHashing hashing = testCase.hashing;
        final String result = hashing.hash(TEMP_PATH);
        assertEquals(testCase.expected, result);
    }

    private enum HashCase {
        MD5("178cddbb94007010f07fe5fbe82d86fb"),
        SHA_1("1189a58fcac49bdccdedeec9498a39e76d7251fc"),
        SHA_256("e6cad9894adb64851b12474088db9252604a386c9be8da3a7370f78166dcbe28"),
        SHA_512("d3c2ec65ae67a3ce1e6ca920e365bcd4072b4b3885774373c182bffb191002ffd14e5977478a5d287fdddb416838ce30617b9973b25819e12e6f34f5264f37e6");

        private final String expected;
        private final StrictHashing hashing;

        HashCase(final String expected) {
            this.expected = expected;
            this.hashing = StrictHashing.valueOf(name());
        }
    }
}
