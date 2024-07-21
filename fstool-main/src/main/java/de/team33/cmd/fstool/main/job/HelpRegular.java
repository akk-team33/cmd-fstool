package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.Context;

import java.util.List;

class HelpRegular extends HelpArgs {

    HelpRegular(final Context context, final List<String> args, final Regular job) {
        super(context, args, job.cmdArgs);
    }
}
