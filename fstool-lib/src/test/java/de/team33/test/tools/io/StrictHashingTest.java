package de.team33.test.tools.io;

import de.team33.patterns.testing.titan.io.FileIO;
import de.team33.tools.io.StrictHashing;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StrictHashingTest {

    private static final Path TEST_PATH = Paths.get("target",
                                                    "testing",
                                                    StrictHashingTest.class.getSimpleName());
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
        MD5("e52ed608fd50e10d0a5d0ca1c6e3123f"),
        SHA_1("cf6a4b2695fda6b7fe1a2539ba9ac20fb28ea826"),
        SHA_256("77bab720a7b227ae967263f31c52d05f742c109e54afbb6c6ac32e003f78e4f9"),
        SHA_512("43e67fc2bad6c0d60e19ad863a8fa65cfbc2f804535bb55e9c3703ca4f9e9cdd1bf2a6a03513cd6c8998709512d58d18e114b1568ae70459556bf230198c4b24");

        private final String expected;
        private final StrictHashing hashing;

        HashCase(final String expected) {
            this.expected = expected;
            this.hashing = StrictHashing.valueOf(name());
        }
    }
}
