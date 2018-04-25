package com.dmt.skindoc.httpTask;

import android.text.TextUtils;

import com.dmt.skindoc.utils.CounterOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

public abstract class Request<T> {

    //地址
    private String url;

    //方法
    private RequestMethod method;

    //参数
    private List<KeyValue> mKeyValues;

    //请求头
    private Map<String,String> mRequestHead;

    //contentType
    private String mContentType;

    private String mCharset="utf-8";

    //http证书
    private SSLSocketFactory mSslSocketFactory;

    //主机认证
    private HostnameVerifier mHostnameVerifier;

    //表单分隔符
    private String boundary=createBoundary();

    private String startBoundary="--"+boundary;

    private String endBoundary=startBoundary+"--";

    //是否强制表单
    private boolean enableFormData;
    public Request(String url){


        this(url,RequestMethod.GET);
    }

    public Request(String url, RequestMethod method) {
        this.url = url;
        this.method = method;

        mRequestHead=new HashMap<>();
        mKeyValues =new ArrayList<>();


    }

    public String getmCharset() {
        return mCharset;
    }

    /**
     *设置编码格式
     * @param mCharset 编码方式
     */
    public void setmCharset(String mCharset) {
        this.mCharset = mCharset;
    }

    protected String createBoundary(){

        return "--SkinDoc"+ UUID.randomUUID();
    }
    //请求头设置
    public void setHead(String key, String value)
    {
        mRequestHead.put(key,value);

    }

    public void setContentType(String contentType)
    {
        this.mContentType=contentType;
    }

    /**
     * 获取请求头
     * @return 请求头
     */
    public Map<String, String> getmRequestHead() {
        return mRequestHead;
    }

    public String getContentType() {
        if(!TextUtils.isEmpty(mContentType))

            //返回开发者设计的特殊ContentType:
            return mContentType;
        else if (enableFormData||hasFile()){//是否强制表单提交、是否有文件（文件只能通过表单和body提交）

            //Content-Type:multipart/form-data;boundary=xxxxxxxxxxx(随机字符串)
            //----------------------
            //=============表单中的String item=============
            //--boundary
            //Content-Disposition: form-data;name="KeyName"
            //Content-Type:text/plain;charset="utf-8"
            //
            //String 数据
            //
            //=====================
            //=========表单中的File Item============
            //--boundary
            //Content-Disposition: form-data;name="KeyName";filename="XXX.jpg"
            //Content-Type: image/jpeg;
            //file stream
            //startboudary--
            return "multipart/form-data;boundary="+boundary;

        }
        //如果用户没有设置并且没有文件，则视为一般性的提交
        return "application/x-www-form-urlencoded";
    }

    /**
     * 获取包体的大小
     * @return 内容长度
     */
    public long getContentLength()  {
        //post类型才需要有ContentLength且普通post类型不需要
        //form:1.普通string表单2.带文件的表单
        CounterOutputStream counterOutputStream=new CounterOutputStream();

        try {
            onWriteBody(counterOutputStream);
        } catch (Exception e) {
            return 0;
        }
        return counterOutputStream.get();

    }

    /**
     * 写包体的方法
     * @param outputStream 写出流
     * @throws IOException 错误信息
     */
    public void onWriteBody(OutputStream outputStream) throws IOException
    {
        if (!enableFormData&&!hasFile())
        {
            writeStringData(outputStream);
        }else
        {
            writeFormData(outputStream);
        }
    }
    /**
     * 写普通数据
     * @param outputStream 写出流
     */
    private void writeStringData(OutputStream outputStream) throws IOException {
        String parameters=buildParametersString();
        outputStream.write(parameters.getBytes());
    }

    /**
     * 写表单数据
     * @param outputStream 写出流
     */
    private void writeFormData(OutputStream outputStream)throws IOException
    {
        for (KeyValue mKeyValue : mKeyValues) {

            String key=mKeyValue.getKey();
            Object value=mKeyValue.getValue();
            if (value instanceof Binary)
            {
                writeFormFileData(outputStream,key,(Binary) value);
            }else
            {
                writeFormStringData(outputStream,key,(String)value);
            }
            outputStream.write("\r\n".getBytes());
        }
        outputStream.write(endBoundary.getBytes());
    }

    /**
     * string型表单
     * @param outputStream 写出流
     * @param key 键
     * @param value 值
     */
    private void writeFormStringData(OutputStream outputStream, String key, String value) throws IOException {
        //--boundary
        //Content-Disposition: form-data;name="KeyName"
        //Content-Type:text/plain;charset="utf-8"
        //
        //String 数据
        //
        String builder = startBoundary + "\r\n" +
                "Content-Disposition: form-data;name=\"" +
                key + "\"" + "\r\n" +
                "Content-Type:text/plain;charset=\"" +
                mCharset + "\"" +
                "\r\n\r\n" +
                value;
        outputStream.write(builder.getBytes(mCharset));


    }

    /**
     * 含文件的表单
     * @param outputStream 写出流
     * @param key   键
     * @param value 值
     * @throws IOException IO错误信息
     */
    private void writeFormFileData(OutputStream outputStream, String key, Binary value) throws IOException {

        //--boundary
        //Content-Disposition: form-data;name="KeyName";filename="XXX.jpg"
        //Content-Type: image/jpeg;
        //
        //file stream

        String filename=value.getFileName();
        String mimeType=value.getMimeType();

        String builder = startBoundary + "\r\n" +
                "Content-Disposition: form-data;name=\"" +
                key + "\"" + "filename=\"" +
                filename + "\"" + "\r\n" +
                "Content-Type:" + mimeType +
                "\r\n\r\n";
        outputStream.write(builder.getBytes(mCharset));

        if (outputStream instanceof CounterOutputStream)
        {
            ((CounterOutputStream)outputStream).write(value.getFileLength());
        }else
        {
            value.onWriteBinary(outputStream);

        }
        //outputStream.write(endBoundary.getBytes());



    }


    protected String buildParametersString()
    {
        StringBuilder stringBuilder=new StringBuilder();
        for (KeyValue keyValue:mKeyValues)
        {
            Object value=keyValue.getValue();
            if (value instanceof String)
            {
                //url?key=value
                stringBuilder.append("&");
                try {
                    stringBuilder.append(URLEncoder.encode(keyValue.getKey(),mCharset));
                    stringBuilder.append("=");
                    stringBuilder.append(URLEncoder.encode((String) value,mCharset));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }



            }


        }
        if (stringBuilder.length()>0)
        {
            stringBuilder.deleteCharAt(0);

        }
        return stringBuilder.toString();
    }
    /**
     * 判断提交数据是否有文件
     * @return 布尔值
     */
    protected  boolean hasFile()
    {
        for (KeyValue keyValue: mKeyValues){

            Object value=keyValue.getValue();
            if (value instanceof Binary)
                return true;

        }
        return false;
    }
    /**
     * 强制开启表单提交
     * @param enable 是否开启表单
     */
    public void formData(boolean enable){

        if (!method.isOutPutMethod())
            throw new IllegalArgumentException(method.getValue()+"is not supported");
        enableFormData=enable;

    }
    public String getUrl() {
        StringBuilder urlBuilder =new StringBuilder(url);
        String parameters=buildParametersString();

        if (!method.isOutPutMethod())//若不是需要Outputstream类型的请求如get，则返回带参数的url
            if (parameters.length()>0&&url.contains("?")&&url.contains("="))
            {
                urlBuilder.append("&");
            }else if (parameters.length()>0&&!url.endsWith("?")) {
                urlBuilder.append("?");
            }
            urlBuilder.append(parameters);
        return urlBuilder.toString();
    }

    public RequestMethod getMethod() {
        return method;
    }

    public List<KeyValue> getmKeyValues() {
        return mKeyValues;
    }

    public void add(String key, String value)
    {
        mKeyValues.add(new KeyValue(key,value));
    }
    public void add(String key, Binary value){

        mKeyValues.add(new KeyValue(key,value));

    }
    public void add(String key, int value){

        mKeyValues.add(new KeyValue(key, Integer.toString(value)));
    }
    public void add(String key, long value){

        mKeyValues.add(new KeyValue(key, Long.toString(value)));
    }
    void setSSLSocketFactory(SSLSocketFactory sslSocketFactory)
    {
        this.mSslSocketFactory=sslSocketFactory;
    }
    void setHostnameVerifier(HostnameVerifier hostnameVerifier)
    {
        this.mHostnameVerifier=hostnameVerifier;
    }

    //包保护方法
    SSLSocketFactory getmSslSocketFactory() {
        return mSslSocketFactory;
    }
    HostnameVerifier getmHostnameVerifier() {
        return mHostnameVerifier;
    }

    /**
     * 解析服务器数据
     * @param responseBody 包体数据
     * @return 解析后的结果
     */
    public abstract T parseResponse(byte[] responseBody) throws Exception;

    @Override
    public String toString() {

        return "url:"+url+";method:"+method+";parameters:"+ mKeyValues.toString();
    }
}
