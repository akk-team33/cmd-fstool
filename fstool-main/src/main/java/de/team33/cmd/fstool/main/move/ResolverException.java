package de.team33.cmd.fstool.main.move;

public class ResolverException extends RuntimeException {

    ResolverException(String message) {
        super(message);
    }

    ResolverException(final Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
