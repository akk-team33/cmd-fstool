package de.team33.cmd.fstool.main.job;

import de.team33.cmd.fstool.main.common.Context;
import de.team33.patterns.io.deimos.TextIO;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

class Help implements Runnable {

    private final Context context;
    private final List<Consumer<Context>> chain;

    Help(final Context context) {
        this.context = context;
        this.chain = new LinkedList<>();
        this.add(ctx -> ctx.printf("%s%n", TextIO.read(Help.class, "Help-head.txt")));
    }

    final Help add(final Consumer<Context> consumer) {
        chain.add(consumer);
        return this;
    }

    @Override
    public final void run() {
        chain.forEach(this::run);
    }

    private void run(final Consumer<Context> consumer) {
        consumer.accept(context);
    }
}