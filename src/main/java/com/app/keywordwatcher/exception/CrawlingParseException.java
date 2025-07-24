package com.app.keywordwatcher.exception;

public class CrawlingParseException extends RuntimeException {
    public CrawlingParseException(String message) {
        super(message);
    }

    public CrawlingParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public CrawlingParseException(Throwable cause) {
        super(cause);
    }
}
