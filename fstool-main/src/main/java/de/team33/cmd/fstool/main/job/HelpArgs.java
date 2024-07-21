package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.Context;

import java.util.List;

class HelpArgs extends Help {

    final String cmdLine;
    final String cmdName;

    HelpArgs(final Context context, final List<String> args, final String cmdArgs) {
        super(context);
        this.cmdLine = String.join(" ", args);
        this.cmdName = args.get(0);
        this.add(ctx -> ctx.printf("Your request:%n%n    %s%n%n", cmdLine));
        this.add(ctx -> ctx.printf("Expected request scheme:%n%n    %s %s%n%n", cmdName, cmdArgs));
    }
}
