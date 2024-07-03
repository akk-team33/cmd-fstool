package de.team33.cmd.test.fstool.main.job;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.cmd.fstool.main.job.Move;
import de.team33.patterns.io.deimos.TextIO;
import de.team33.patterns.testing.titan.io.FileInfo;
import de.team33.patterns.testing.titan.io.ZipIO;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoveTest implements Context {

    private static final String UUID = java.util.UUID.randomUUID().toString();
    private static final Path TEST_PATH = Path.of("target", "testing", UUID);

    @ParameterizedTest
    @EnumSource
    void run(final Case testCase) {
        // given ...
        final Path testPath = TEST_PATH.resolve("run(" + testCase + ")");
        ZipIO.unzip(MoveTest.class, "SomeFiles.zip", testPath);
        final Path mainPath = testPath.resolve("main");
        assertEquals(TextIO.read(MoveTest.class, "SomeFiles.initial.txt"),
                     FileInfo.of(mainPath).toString(),
                     () -> "initial state of <" + mainPath + "> is not as expected!");

        // when...
        Move.runnable(this,
                      MoveTest.class.getSimpleName(),
                      testCase.args.apply(mainPath))
            .run();

        // then ...
        assertEquals(TextIO.read(MoveTest.class, testCase.rsrcName),
                     FileInfo.of(testPath).toString(),
                     () -> "final state of <" + mainPath + "> is not as expected (" + testCase.rsrcName + ")!");
    }

    @Override
    public void printf(final String format, final Object... values) {
        // System.out.printf(format, values);
    }

    @SuppressWarnings("unused")
    enum Case {

        FLAT_Y_M_D(path -> List.of(path.toString(), "(moved)/@Y/@M/@D/@N.@X"),
                   "MoveTest.flat.Y.M.D.txt"),
        FLAT_Y_n_x(path -> List.of(path.toString(), "(moved)\\@Y\\@N\\@X\\@F"),
                   "MoveTest.flat.Y.n.x.txt"),
        DEEP_Y_M_D(path -> List.of(path.toString(), "../(moved)/@Y/@M\\@D/@N.@X", "-r"),
                   "MoveTest.deep.Y.M.D.txt"),
        DEEP_Y_M_D_IGNORE(path -> List.of(path.toString(), "..\\(moved)/@Y\\@M/@D\\@F", "-R", "api", "job"),
                          "MoveTest.deep.Y.M.D.ignore.txt"),
        DEEP_Y_M_D_HASH(path -> List.of(path.toString(), "(moved)/@Y/@M/@D/@#@X", "-r"),
                        "MoveTest.deep.Y.M.D.#.txt"),
        DEEP_R_HASH(path -> List.of(path.toString(), "@P/@N@#@X", "-r"),
                    "MoveTest.deep.R.#.txt"),
        NOTHING_TO_DO(path -> List.of(path.toString(), "@P/@F", "-r"),
                      "MoveTest.nothing.txt");

        final Function<Path, List<String>> args;
        final String rsrcName;

        Case(final Function<Path, List<String>> args, String rsrcName) {
            this.args = args;
            this.rsrcName = rsrcName;
        }
    }
}
