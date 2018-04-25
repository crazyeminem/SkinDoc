package com.dmt.skindoc.httpTask.error;

public class TimeOutError extends Exception {
    public TimeOutError() {
    }

    public TimeOutError(String message) {
        super(message);
    }

    public TimeOutError(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeOutError(Throwable cause) {
        super(cause);
    }


}
