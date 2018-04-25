package com.dmt.skindoc.httpTask;

public class Message implements Runnable {

    private Response response;
    private HttpListener httpListener;

    public Message(Response response, HttpListener httpListener) {
        this.response = response;
        this.httpListener = httpListener;
    }

    //  回调到主线程
    @Override
    public void run() {

        Exception e=response.getException();
        if (e!=null)
            httpListener.onFailed(e);
        else
            httpListener.onSucceed(response);

    }
}
