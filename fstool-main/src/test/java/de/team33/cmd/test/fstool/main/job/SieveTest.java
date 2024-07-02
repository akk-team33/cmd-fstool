package de.team33.cmd.test.fstool.main.job;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.cmd.fstool.main.job.Move;
import de.team33.cmd.fstool.main.job.Sieve;
import de.team33.patterns.io.deimos.TextIO;
import de.team33.patterns.testing.titan.io.FileInfo;
import de.team33.patterns.testing.titan.io.ZipIO;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SieveTest implements Context {

    private static final Path TEST_PATH = Path.of("target", "testing")
                                              .toAbsolutePath()
                                              .normalize();

    @Test
    void run() throws IOException {
        // given ...
        final String uuid1 = UUID.randomUUID().toString();
        final Path testPath1 = TEST_PATH.resolve(uuid1);
        final String uuid2 = UUID.randomUUID().toString();
        final Path testPath2 = TEST_PATH.resolve(uuid2);
        final Path mainPath = testPath1.resolve("main");
        final Path indexPath = mainPath.resolve("(sieved-unique).txt");
        ZipIO.unzip(getClass(), "SomeFiles.zip", testPath1);
        ZipIO.unzip(getClass(), "SomeFiles.zip", testPath2);
        Move.runnable(this, getClass().getSimpleName(), List.of(
                testPath2.resolve("main").toString(),
                "../../" + testPath1.getFileName() + "/main/alt/@P/@F",
                "-r"))
            .run();
        assertFalse(Files.exists(indexPath));
        assertEquals(TextIO.read(getClass(), "Sieve.initial.txt"),
                     FileInfo.of(mainPath).toString(),
                     () -> "initial state of <" + mainPath + "> is not as expected!");

        // when...
        Sieve.runnable(this, getClass().getSimpleName(), List.of(mainPath.toString()))
             .run();

        // then ...
        assertTrue(Files.exists(indexPath));
        Files.setLastModifiedTime(indexPath, FileTime.from(Instant.ofEpochMilli(0L))); // must be normalized to match!
        assertEquals(TextIO.read(getClass(), "Sieve.result.txt"),
                     FileInfo.of(mainPath.getParent()).toString().replace(uuid1, "[UUID1]"),
                     () -> "final state of <" + mainPath + "> is not as expected (Sieve.result.txt)!");

        // again: when...
        Sieve.runnable(this, getClass().getSimpleName(), List.of(mainPath.toString()))
             .run();

        // again: then ...
        assertTrue(Files.exists(indexPath));
        Files.setLastModifiedTime(indexPath, FileTime.from(Instant.ofEpochMilli(0L))); // must be normalized to match!
        assertEquals(TextIO.read(getClass(), "Sieve.result.txt"),
                     FileInfo.of(mainPath.getParent()).toString().replace(uuid1, "[UUID1]"),
                     () -> "final state of <" + mainPath + "> is not as expected (Sieve.result.txt)!");
    }

    @Override
    public void printf(final String format, final Object... values) {
        // System.out.printf(format, values);
    }
}
