package de.team33.cmd.fstool.main;

import de.team33.cmd.fstool.main.common.Context;

import java.util.List;

public class Main implements Context {

    private final List<String> args;

    private Main(final List<String> args) {
        this.args = args;
    }

    public static void main(final String... args) {
        new Main(List.of(args)).run();
    }

    private void run() {
        Case.select(args)
            .task(this, args)
            .run();
    }
}
