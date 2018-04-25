package com.dmt.skindoc.httpTask;

import com.dmt.skindoc.httpTask.error.ParseError;
import com.dmt.skindoc.httpTask.error.URLError;
import com.dmt.skindoc.httpTask.urlconnection.URLConnectionFactory;
import com.dmt.skindoc.utils.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public class RequestTask<T> implements Runnable {

    private Request<T> request;
    private HttpListener<T> httpListener;

    public RequestTask(Request<T> request, HttpListener<T> httpListener) {
        this.request = request;
        this.httpListener = httpListener;
    }

    /**
     * 判断是否有响应包体
     * @param requestMethod
     * @param responseCode
     * @return
     */
    private boolean hasResponseBody(RequestMethod requestMethod,int responseCode)
    {
        return requestMethod!=RequestMethod.HEAD
                &&!(100<=responseCode&&responseCode<200)
                &&responseCode!=204
                &&responseCode!=205
                &&!(300<=responseCode&&responseCode<400);
    }

    /**
     * 获得服务器的流
     * @param urlConnection
     * @param responseCode
     * @return
     */
    private InputStream getInputStream(HttpURLConnection urlConnection, int responseCode) throws IOException {
        InputStream inputStream;

        if (responseCode>=400)
            inputStream=urlConnection.getErrorStream();
        else
            inputStream=urlConnection.getInputStream();

        //流解压
        String contentEncoding=urlConnection.getContentEncoding();
        if (contentEncoding!=null&&contentEncoding.contains("gzip"))
        {
            inputStream=new GZIPInputStream(inputStream);
        }
        return inputStream;
    }
    @Override
    public void run() {
        //执行请求的线程
        Exception exception=null;
        int responseCode=-1;
        Map<String,List<String>> responseHeaders=null;
        HttpURLConnection urlConnection=null;
        OutputStream outputStream=null;
        InputStream inputStream=null;
        byte[] responseBody=null;

        String urlstr=request.getUrl();
        RequestMethod method=request.getMethod();
        try {
            //建立连接
            URL url=new URL(urlstr);
            //封装OKhttp
            //Okhttp
            urlConnection= URLConnectionFactory.getInstance().openUrl(url);

            //httpurlconnection
            //urlConnection= (HttpURLConnection) url.openConnection();

            //https类型处理
            if (urlConnection instanceof HttpsURLConnection)
            {
                HttpsURLConnection httpsURLConnection= (HttpsURLConnection) urlConnection;
                SSLSocketFactory sslSocketFactory=request.getmSslSocketFactory();
                HostnameVerifier hostnameVerifier=request.getmHostnameVerifier();

                if (sslSocketFactory!=null)
                    httpsURLConnection.setSSLSocketFactory(sslSocketFactory);//http证书
                if(hostnameVerifier!=null)
                    httpsURLConnection.setHostnameVerifier(hostnameVerifier);//主机认证
            }
            //设置头和基础信息,发送文件时需要添加Content-Length头
            urlConnection.setRequestMethod(method.getValue());
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(method.isOutPutMethod());
            setHeader(urlConnection,request);

            if (method.isOutPutMethod())
            {
                outputStream=urlConnection.getOutputStream();
                request.onWriteBody(outputStream);
            }
            //读取响应
            responseCode=urlConnection.getResponseCode();

            Logger.d("responseCode"+responseCode);
            //读取响应头
            responseHeaders=urlConnection.getHeaderFields();

            if (hasResponseBody(method,responseCode))
            {
                inputStream=getInputStream(urlConnection,responseCode);

                //读结果
                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                int len;
                byte[] buffer=new byte[2048];
                while((len=inputStream.read(buffer))!=-1)
                {
                    byteArrayOutputStream.write(buffer,0,len);
                }

                //关闭流
                byteArrayOutputStream.close();
                responseBody=byteArrayOutputStream.toByteArray();

            }
            else
            {
                Logger.d("没有包体!");
            }

        }catch (SocketTimeoutException e){
            exception =new SocketTimeoutException("Timeout!");

        }catch (MalformedURLException e){
            exception=new URLError("URL Error");

        }catch (UnknownHostException e){
            exception=new UnknownHostException("Unknown Host!");

        }catch (Exception e) {
            e.printStackTrace();
            exception=e;
        }finally {
            if (urlConnection!=null)
            {
                urlConnection.disconnect();
            }

            if(outputStream!=null)
            {
                try {
                    outputStream.close();
                } catch (IOException e) {
                   exception=e;
                }
            }
        }



        Logger.i("执行请求"+request.toString());

        //解析服务器响应数据

        T t= null;
        try {
            t = request.parseResponse(responseBody);
        } catch (Exception e) {
            exception=new ParseError("The data parse error");
        }
        Response<T> response=new Response<>(responseCode,responseHeaders,exception,request);
        response.setResponseResult(t);

        //发送响应数据到主线程
        Message message=new Message(response,httpListener);

        Poster.getInstance().post(message);


    }

    /**
     * 给URLconnection设置请求头
     * @param urlConnection
     * @param request
     */
    private void setHeader(HttpURLConnection urlConnection, Request request)
    {
        Map<String,String> requestHeaders=request.getmRequestHead();
        String contetnType=request.getContentType();
        requestHeaders.put("Content-Type",contetnType);
        //处理ContentLength
        long contentLength=request.getContentLength();
        requestHeaders.put("Content-Length", Long.toString(contentLength));


        for (Map.Entry<String,String> stringStringEntry:requestHeaders.entrySet())
        {
            String headKey=stringStringEntry.getKey();
            String headValue=stringStringEntry.getValue();

            Logger.d(headKey+"="+headValue);
            urlConnection.setRequestProperty(headKey,headValue);

        }
    }
}
