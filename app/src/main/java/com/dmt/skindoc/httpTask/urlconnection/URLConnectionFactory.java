package com.dmt.skindoc.httpTask.urlconnection;

import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.internal.huc.OkHttpURLConnection;
import okhttp3.internal.huc.OkHttpsURLConnection;

/**
 * OKHTTP->URLConnection
 */
public class URLConnectionFactory {
    private static URLConnectionFactory instance;

    public  static URLConnectionFactory getInstance()
    {
        if (instance==null)
        {
            synchronized (URLConnectionFactory.class){
                if (instance==null){
                    instance=new URLConnectionFactory();
                }
            }

        }
        return instance;
    }
    private OkHttpClient okHttpClient;
    private URLConnectionFactory ()
    {
        okHttpClient=new OkHttpClient();
    }
    public HttpURLConnection openUrl(URL url)
    {
        return openUrl(url,null);
    }

    /**
     * （弃置用法）
     * @param url url
     * @param proxy 代理
     * @return
     */
    public HttpURLConnection openUrl(URL url, Proxy proxy)
    {
        String protocol=url.getProtocol();
        OkHttpClient copy=okHttpClient.newBuilder().proxy(proxy).build();
        if (protocol.equals("http"))return new OkHttpURLConnection(url,copy);
        if (protocol.equals("https"))return new OkHttpsURLConnection(url,copy);
        throw new IllegalArgumentException("Unexpected Protocol:"+ protocol);
    }

}
