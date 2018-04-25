package com.dmt.skindoc.httpTask;

import java.util.List;
import java.util.Map;

public class Response<T> {

    /**
     * 响应码
     */
    private int  responseCode;
    /**
     * 响应结果
     */
    private T responseBody;
    /**
     * 错误
     */
    private Exception exception;
    /**
     * 请求
     */
    private Request request;

    private Map<String,List<String>> responseHeaders;

    public int getResponseCode() {
        return responseCode;
    }

    /**
     * 设置响应，默认访问控制
     * @param responseResult
     */
    void setResponseResult(T responseResult)
    {
        this.responseBody=responseResult;
    }

    /**
     * 获取服务器响应
     * @return
     */
    public T get() {
        return responseBody;
    }

    public Exception getException() {
        return exception;
    }

    public Request getRequest() {
        return request;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    public Response(int responseCode, Map<String,List<String>> responseHeaders,
                    Exception exception, Request request) {
        this.responseCode = responseCode;
        this.exception = exception;
        this.request = request;
        this.responseHeaders=responseHeaders;
    }
}
