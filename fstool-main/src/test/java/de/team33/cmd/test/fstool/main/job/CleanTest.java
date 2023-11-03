package de.team33.cmd.test.fstool.main.job;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.cmd.fstool.main.job.Clean;
import de.team33.patterns.exceptional.dione.Conversion;
import de.team33.patterns.io.alpha.FileEntry;
import de.team33.patterns.io.alpha.FileInfo;
import de.team33.patterns.io.alpha.FileProcessing;
import de.team33.patterns.testing.titan.io.TextIO;
import de.team33.patterns.testing.titan.io.ZipIO;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static java.util.function.Predicate.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CleanTest implements Context {

    private static final Path TEST_PATH = Path.of("target", "testing")
                                              .toAbsolutePath()
                                              .normalize();

    @Test
    void run() {
        // given ...
        final Path testPath = TEST_PATH.resolve(UUID.randomUUID().toString());
        ZipIO.unzip(CleanTest.class, "SomeFiles.zip", testPath);
        final Path mainPath = testPath.resolve("main");
        // removeAllNonDirectories ...
        FileProcessing.with(LinkOption.NOFOLLOW_LINKS)
                      .stream(mainPath)
                      .filter(not(FileEntry::isDirectory))
                      .map(FileEntry::path)
                      .forEach(Conversion.consumer(Files::delete));
        assertEquals(TextIO.read(CleanTest.class, "CleanTest.initial.txt"),
                     FileInfo.of(mainPath).toString(),
                     () -> "initial state of <" + mainPath + "> is not as expected!");

        // when...
        Clean.runnable(this,
                       CleanTest.class.getSimpleName(),
                       List.of(mainPath.toString()))
             .run();

        // then ...
        assertEquals(TextIO.read(CleanTest.class, "CleanTest.result.txt"),
                     FileInfo.of(mainPath).toString(),
                     () -> "final state of <" + mainPath + "> is not as expected (CleanTest.result.txt)!");
    }

    @Override
    public void printf(final String format, final Object... values) {
        // System.out.printf(format, values);
    }
}
