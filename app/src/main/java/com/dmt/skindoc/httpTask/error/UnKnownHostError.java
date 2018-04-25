package com.dmt.skindoc.httpTask.error;

public class UnKnownHostError extends Exception {

    public UnKnownHostError() {
    }

    public UnKnownHostError(String message) {
        super(message);
    }

    public UnKnownHostError(String message, Throwable cause) {
        super(message, cause);
    }

    public UnKnownHostError(Throwable cause) {
        super(cause);
    }

}
