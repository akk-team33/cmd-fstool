package de.team33.cmd.fstool.main.common;

import de.team33.patterns.exceptional.dione.XSupplier;
import de.team33.patterns.io.deimos.TextIO;

import java.util.List;

public class Validator {
    private static final String PROBLEM_FMT = "Problem:%n%n    %s%n%n";

    private final Context context;
    private final Class<?> clientClass;
    private final List<String> args;

    private Validator(final Class<?> clientClass, final Context context, final List<String> args) {
        this.context = context;
        this.clientClass = clientClass;
        this.args = args;
    }

    public static Validator by(final Class<?> clientClass, final Context context, final List<String> args) {
        return new Validator(clientClass, context, args);
    }

    public Runnable validated(final XSupplier<Runnable, ValidationException> primary) {
        try {
            return primary.get();
        } catch (final ValidationException e) {
            final String cmdLine = String.join(" ", args);
            final String cmdName = args.get(0);
            final String problem = e.getProblem()
                                    .map(text -> String.format(PROBLEM_FMT, text))
                                    .orElse("");
            final String format = TextIO.read(clientClass, clientClass.getSimpleName() + ".txt");
            return () -> context.printf(format, cmdLine, cmdName, problem);
        }
    }
}
