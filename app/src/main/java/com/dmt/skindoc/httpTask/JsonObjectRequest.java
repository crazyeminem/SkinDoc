package com.dmt.skindoc.httpTask;


import com.alibaba.fastjson.JSONObject;

public class JsonObjectRequest extends Request<JSONObject> {
    public JsonObjectRequest(String url) { this(url,RequestMethod.GET); }

    public JsonObjectRequest(String url, RequestMethod method) {

        super(url, method);
        setHead("Accept","application/json");
    }

    /**
     * 解析响应数据
     * @param responseBody 包体数据
     * @return json对象
     * @throws Exception 异常
     */
    @Override
    public JSONObject parseResponse(byte[] responseBody) throws Exception {


        String result=StringRequest.parseResponseToString(responseBody);
                return JSONObject.parseObject(result);
    }
}
