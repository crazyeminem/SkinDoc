package com.dmt.skindoc.httpTask.error;

public class ParseError extends Exception {

    public ParseError() {
    }

    public ParseError(String message) {
        super(message);
    }

    public ParseError(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseError(Throwable cause) {
        super(cause);
    }


}
