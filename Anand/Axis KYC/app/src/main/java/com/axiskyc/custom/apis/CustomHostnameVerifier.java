package com.axiskyc.custom.apis;

/**
 * Created by Ahmed on 4/1/2017.
 */

public class CustomHostnameVerifier implements org.apache.http.conn.ssl.X509HostnameVerifier {

    @Override
    public boolean verify(String host, javax.net.ssl.SSLSession session) {
        javax.net.ssl.HostnameVerifier hv = javax.net.ssl.HttpsURLConnection.getDefaultHostnameVerifier();
        return hv.verify(host, session);
    }

    @Override
    public void verify(String host, javax.net.ssl.SSLSocket ssl) throws java.io.IOException {
    }

    @Override
    public void verify(String host, java.security.cert.X509Certificate cert) throws javax.net.ssl.SSLException {

    }

    @Override
    public void verify(String host, String[] cns, String[] subjectAlts) throws javax.net.ssl.SSLException {

    }
}
