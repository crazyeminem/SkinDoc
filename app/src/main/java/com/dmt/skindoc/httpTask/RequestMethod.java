package com.dmt.skindoc.httpTask;

public enum  RequestMethod {

    GET("GET"),
    HEAD("HEAD"),
    POST("POST"),
    DELETE("DELETE");

    private String value;
    RequestMethod(String value) {

        this.value=value;

    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString();
    }
    public boolean isOutPutMethod()
    {
        switch (this)
        {
            case GET:
                return false;
            case HEAD:
                return false;
            case POST:
                return true;
            case DELETE:
                return true;
                default:
                    return false;
        }
    }
}
