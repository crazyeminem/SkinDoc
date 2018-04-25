package com.dmt.skindoc.httpTask;

public class StringRequest extends Request<String> {

    public StringRequest(String url) {
        this(url,RequestMethod.GET);
    }

    public StringRequest(String url, RequestMethod method) {
        super(url, method);
        //接受所有类型
        this.setHead("Accept","*");
    }

    @Override
    public String parseResponse(byte[] responseBody)throws Exception {
        return  parseResponseToString(responseBody);

    }
    public static String parseResponseToString(byte[] responseBody)
    {
        if (responseBody!=null&&responseBody.length>0)
            return new String(responseBody);
        return "";
    }
}
