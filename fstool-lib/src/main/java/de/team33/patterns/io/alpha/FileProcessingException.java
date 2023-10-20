package de.team33.patterns.io.alpha;

public class FileProcessingException extends RuntimeException {

    FileProcessingException(final String message, Exception cause) {
        super(message, cause);
    }
}
