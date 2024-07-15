package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.BadRequestException;
import de.team33.cmd.fstool.main.common.Context;

import java.util.List;

public class Setup {

    public static Runnable runnable(Context context, String shellCmd, List<String> args) {
        throw new BadRequestException("not yet implemented");
    }

    public static Runnable runnable(final Context context, final List<String> args) {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
