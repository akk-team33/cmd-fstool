package de.team33.test.java.security;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageDigestTest {

    private static final Path PATH = Path.of("..", "fstool-main", "");
    public static final int ONE_KB = 1024;

    @Test
    final void digest() throws IOException, NoSuchAlgorithmException {
        final MessageDigest md = MessageDigest.getInstance("MD5");
        try (final InputStream in = getClass().getResourceAsStream("short.txt")) {
            for (byte[] bytes = in.readNBytes(ONE_KB); bytes.length > 0; bytes = in.readNBytes(ONE_KB)) {
                md.update(bytes);
            }
        }
        final byte[] digest = md.digest();
        assertEquals(16, digest.length);

        final StringBuilder sb = new StringBuilder();
        for (final byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        final String result = sb.toString();
        assertEquals("e2fc714c4727ee9395f324cd2e7f331f", result);

        final byte[] bytes = new byte[digest.length + 1];
        for (int index = 0; index < digest.length; ++index) {
            bytes[index + 1] = digest[index];
        }
        assertEquals(new BigInteger("e2fc714c4727ee9395f324cd2e7f331f", 16), new BigInteger(bytes));
    }
}
