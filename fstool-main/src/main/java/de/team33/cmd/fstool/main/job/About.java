package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.patterns.io.deimos.TextIO;

import java.util.List;

public class About {

    public static final String EXCERPT = "Get basic info about this application.";

    public static Runnable job(final Context context, final List<String> args) {
        final String cmdLine = String.join(" ", args);
        return () -> context.printf(TextIO.read(About.class, "About.txt"), cmdLine);
    }
}
