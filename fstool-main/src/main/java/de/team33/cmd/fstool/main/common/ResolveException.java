package de.team33.cmd.fstool.main.common;

public class ResolveException extends Exception {

    public ResolveException() {
        super(null, null);
    }

    public ResolveException(final String message) {
        super(message, null);
    }
}
