package de.team33.cmd.fstool.main.common;

public class JobException extends Exception {

    private final Class<?> originatorClass;

    public JobException(final Class<?> originatorClass, final String message) {
        super(message, null);
        this.originatorClass = originatorClass;
    }

    public final Class<?> getOriginatorClass() {
        return originatorClass;
    }
}
