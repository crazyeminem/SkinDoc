package com.dmt.skindoc.httpTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum RequestExecutor {

    //枚举，全局单例
    INSTANCE;
    private ExecutorService mExecutorService;
    RequestExecutor(){
        mExecutorService= Executors.newSingleThreadExecutor();
    }

    /**
     * 执行请求
     * @param request 请求
     */
    public void execute(Request request,HttpListener httpListener){

        mExecutorService.execute(new RequestTask(request,httpListener));
    }
}
