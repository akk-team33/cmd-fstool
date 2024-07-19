package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.cmd.fstool.main.common.Resolving;
import de.team33.patterns.io.deimos.TextIO;

import java.util.List;

class Help {

    private final Context context;
    private final String cmdLine;
    private final String cmdName;
    private String problem;
    private String cmdArgs;

    private Help(final Context context, final List<String> args) {
        this.context = context;
        this.cmdLine = String.join(" ", args);
        this.cmdName = args.get(0);
        this.cmdArgs = "";
    }

    static Help by(final Context context, final List<String> args) {
        return new Help(context, args);
    }

    final Help setCmdArgs(final String cmdArgs) {
        this.cmdArgs = cmdArgs;
        return this;
    }

    final Help setProblem(final String problem) {
        this.problem = problem;
        return this;
    }

    final Runnable runnable() {
        return () -> {
            context.printf("%s%n", TextIO.read(Resolving.class, "head.txt"));
            context.printf("Your request:%n%n    %s%n%n", cmdLine);
            context.printf("Expected request scheme:%n%n    %s %s%n%n", cmdName, cmdArgs);
        };
    }
}
