package de.team33.cmd.fstool.main.common;

import de.team33.patterns.exceptional.dione.XBiFunction;
import de.team33.patterns.io.deimos.TextIO;

import java.util.List;
import java.util.Optional;

public class Resolving {
    private static final String PROBLEM_FMT = "Problem:%n%n    %s%n%n";

    private final Context context;
    private final Class<?> clientClass;
    private final List<String> args;

    private Resolving(final Class<?> clientClass, final Context context, final List<String> args) {
        this.context = context;
        this.clientClass = clientClass;
        this.args = args;
    }

    public static Runnable job(final Class<?> clientClass, final Context context, final List<String> args,
                               final XBiFunction<Context, List<String>, Runnable, ResolveException> method) {
        return new Resolving(clientClass, context, args).job(method);
    }

    private Runnable job(final XBiFunction<Context, List<String>, Runnable, ResolveException> method) {
        try {
            return method.apply(context, args);
        } catch (final ResolveException e) {
            final String cmdLine = String.join(" ", args);
            final String cmdName = args.get(0);
            final String problem = Optional.ofNullable(e.getMessage())
                                           .map(message -> String.format(PROBLEM_FMT, message))
                                           .orElse("");
            final String format = TextIO.read(clientClass, clientClass.getSimpleName() + ".txt");
            return () -> context.printf(format, cmdLine, cmdName, problem);
        }
    }
}
