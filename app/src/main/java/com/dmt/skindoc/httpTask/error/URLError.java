package com.dmt.skindoc.httpTask.error;

public class URLError extends Exception {

    public URLError() {
    }

    public URLError(String message) {
        super(message);
    }

    public URLError(String message, Throwable cause) {
        super(message, cause);
    }

    public URLError(Throwable cause) {
        super(cause);
    }


}
