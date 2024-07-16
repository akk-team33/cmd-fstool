package de.team33.cmd.fstool.main.common;

import java.util.Optional;

public class ValidationException extends Exception {

    public ValidationException() {
        super();
    }

    public ValidationException(final String message) {
        super(message);
    }

    final Optional<String> getProblem() {
        return Optional.ofNullable(getMessage());
    }
}
