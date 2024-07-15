package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.BadRequestException;
import de.team33.cmd.fstool.main.common.Context;

import java.util.List;

public class Setup {

    public static final String EXCERPT = "GET or SET the user specific setup.";

    public static Runnable job(Context context, String shellCmd, List<String> args) {
        throw new BadRequestException("not yet implemented");
    }

    public static Runnable job(final Context context, final List<String> args) {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
