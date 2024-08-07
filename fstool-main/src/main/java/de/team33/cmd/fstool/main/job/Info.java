package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.BadRequestException;
import de.team33.cmd.fstool.main.common.Context;
import de.team33.patterns.io.phobos.FileIndex;

import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Info implements Runnable {

    public static final String EXCERPT = "Get information about a given file or directory.";
    private static final String HELP_FORMAT = //
            "Expected request scheme:%n" +
            "%n" +
            "    %s info PATH%n" +
            "%n" +
            "    to get information about a given file or directory%n" +
            "%n" +
            "Required argument:%n" +
            "%n" +
            "    PATH: a path to the file or directory in question.";

    private final Context context;
    private final Path tgtPath;

    private Info(final Context context, final String shellCmd, final List<String> args) {
        if (1 != args.size()) {
            throw new BadRequestException(String.format(HELP_FORMAT, shellCmd));
        }
        this.context = context;
        this.tgtPath = Paths.get(args.get(0)).toAbsolutePath().normalize();
    }

    public static Runnable job(final Context context, final List<String> args) {
        return new Info(context, args.get(0), args.subList(1, args.size()));
    }

    @Override
    public void run() {
        context.printf("%s%n%n", FileIndex.of(tgtPath, LinkOption.NOFOLLOW_LINKS).toString());
    }
}
