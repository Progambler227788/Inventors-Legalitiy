package com.axiskyc.custom.apis;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;

public class HttpGetWithEntity extends HttpEntityEnclosingRequestBase implements HttpUriRequest {
    public final static String METHOD_NAME = "GET";

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}
