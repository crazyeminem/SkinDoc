package com.dmt.skindoc.httpTask;

public interface HttpListener<T> {

    /**
     * 成功请求
     * @param response 响应
     */
    void onSucceed(Response<T> response);

    /**
     * 请求失败
     * @param e 错误信息
     */
    void onFailed(Exception e);

}
