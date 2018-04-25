package com.dmt.skindoc.httpTask;

public class KeyValue {

    String key;

    Object value;

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }
    public KeyValue(String key, Binary value) {
        this.key = key;
        this.value = value;
    }
    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "key="+key+";value="+value;
    }
}
