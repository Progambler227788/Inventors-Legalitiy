package com.axiskyc.custom.apis;

/**
 * Created by Ahmed on 4/1/2017.
 */

public class CustomHttpClient extends org.apache.http.impl.client.DefaultHttpClient {

    public CustomHttpClient() {
        super();
        org.apache.http.conn.ssl.SSLSocketFactory socketFactory = org.apache.http.conn.ssl.SSLSocketFactory.getSocketFactory();
        socketFactory.setHostnameVerifier(new CustomHostnameVerifier());
        org.apache.http.conn.scheme.Scheme scheme = (new org.apache.http.conn.scheme.Scheme("https", socketFactory, 443));
        getConnectionManager().getSchemeRegistry().register(scheme);
    }
}