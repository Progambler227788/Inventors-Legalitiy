package com.axiskyc.custom.apis;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import com.axiskyc.R;
import com.axiskyc.custom.Global;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebService {

    private boolean bypass_200 = false;

    private boolean is_file_uploading = false;

    public static java.util.ArrayList<WebServicesCustomKeys> headers = new java.util.ArrayList<>();

    private HTTPRequestMethod mMethod = null;

    private org.apache.http.client.HttpClient httpClient;
    private OkHttpClient OKhttpClient = null;

    public String mPostJson = "";

    public static android.app.ProgressDialog cancelDialog = null;
    private Context backup_con;
    private ProgressBar backup_pbar;
    private String backup_request = "";
    private String backup_file_key = "";
    private File backup_file = null;

    public static String domain_name =
                "https://anikdevnath.com/APIS_AXISREWARD3/";

    public static String base_url =
            domain_name;

    private myCallback mCallback;
    public static String responseJson = "";

    private String link = "";
    public static int response_status_code = 0;

    public enum HTTPRequestMethod {
        GET, POST, PUT, DELETE, POST_METHOD2
    }

    public interface myCallback {
        public void onServiceResponse(String responseJson);
    }

    public WebService(Context mContext, String request,
                      myCallback MyCall, HTTPRequestMethod method, final String PostJson,
                      ProgressBar pbar, String file_upload_key, File uploaded_file) {

        if (!Global.CheckInternetConnectivity(mContext)) {
            return;
        }

        backup_con = mContext;
        mMethod = method;
        mCallback = MyCall;
        backup_request = request;
        backup_file_key = file_upload_key;
        backup_file = uploaded_file;
        backup_pbar = pbar;
        mPostJson = PostJson;

        base_url = domain_name + Global.webservice_file_name;

        response_status_code = 200;
        responseJson = "";

        is_file_uploading = false;
        try {
            if (backup_file.exists()) {
                is_file_uploading = true;
            }
        } catch (Exception ee) {
        }

        if (request.startsWith("http")) {
            link = request;
        } else {
            link = base_url + request;
        }

        if (backup_pbar == null) {

            Global.ShowLogs("link = " + link);
            Global.ShowLogs("PostJson = " + mPostJson);
            Global.ShowLogs("method = " + mMethod.name());

            if (!Global.webservice_is_show_loader) {
                Global.webservice_is_show_loader = true;
            } else {
                cancelDialog = new android.app.ProgressDialog(backup_con, Global.dialog_theme);
                cancelDialog.setCanceledOnTouchOutside(false);
                cancelDialog.setMessage("Please Wait");
                cancelDialog.setCancelable(true);
                cancelDialog.show();
            }

            Global.ShowLogs("is_file_uploading = " + is_file_uploading);

            if (is_file_uploading) {

            } else {
                //backup_pbar.setVisibility(View.VISIBLE);
            }

            Runnable priceRunnable = new Runnable() {
                public void run() {

                    switch (mMethod) {
                        case GET: {
                            try {

                                httpClient = new CustomHttpClient();

                                HttpGetWithEntity e = new HttpGetWithEntity();
                                e.setURI(URI.create(link));
                                e.setHeader("Content-Type", "application/json");
                                if (headers.size() > 0) {
                                    for (int a = 0; a < headers.size(); a++) {
                                        e.setHeader(headers.get(a).key, headers.get(a).value);
                                    }
                                }
                                StringEntity se = new StringEntity(mPostJson);
                                e.setEntity(se);
                                org.apache.http.HttpResponse response = httpClient.execute(e);

                                response_status_code = response.getStatusLine().getStatusCode();
                                Global.ShowLogs("response_status_code = " + response_status_code);
                                Global.ShowLogs("getReasonPhrase = " + response.getStatusLine().getReasonPhrase());

                                if (bypass_200) {
                                    java.io.BufferedReader br = new java.io.BufferedReader(
                                            new java.io.InputStreamReader((response.getEntity().getContent())));

                                    String output;
                                    StringBuffer str_buffer = new StringBuffer();
                                    while ((output = br.readLine()) != null) {
                                        str_buffer.append(output);
                                    }
                                    responseJson = str_buffer.toString();

                                } else {

                                    if (response_status_code == 200) {

                                        java.io.BufferedReader br = new java.io.BufferedReader(
                                                new java.io.InputStreamReader((response.getEntity().getContent())));

                                        String output;
                                        StringBuffer str_buffer = new StringBuffer();
                                        while ((output = br.readLine()) != null) {
                                            str_buffer.append(output);
                                        }
                                        responseJson = str_buffer.toString();

                                    } else {
                                        responseJson = "Failed : HTTP error code : " + response_status_code + ", " +
                                                response.getStatusLine().getReasonPhrase();
                                    }
                                }

                                try {
                                    httpClient.getConnectionManager().shutdown();
                                } catch (Exception eee) {
                                }

                                try {
                                    httpClient.getConnectionManager().closeExpiredConnections();
                                } catch (Exception eee) {
                                }

                            } catch (org.apache.http.client.ClientProtocolException e) {
                                Global.ShowLogs("ClientProtocolException-error = " + e.getMessage());
                                e.printStackTrace();
                                responseJson = "";
                            } catch (IOException e) {
                                Global.ShowLogs("IOException-error = " + e.getMessage());
                                e.printStackTrace();
                                responseJson = "";
                            }

                            break;
                        }
                        case POST: {

                            try {
                                OKhttpClient = new OkHttpClient.Builder()
                                        .connectTimeout(30, TimeUnit.SECONDS)
                                        .readTimeout(30, TimeUnit.SECONDS)
                                        .writeTimeout(30, TimeUnit.SECONDS)
                                        .build();

                                MultipartBody.Builder my_builder = new MultipartBody.Builder();

                                my_builder.setType(MultipartBody.FORM);
                                try {
                                    JSONObject mainObj = new JSONObject(mPostJson);
                                    java.util.Iterator<String> iterator = mainObj.keys();
                                    while (iterator.hasNext()) {
                                        String key = iterator.next();
                                        String value = "";
                                        try {
                                            value = "" + mainObj.opt(key);
                                        } catch (Exception exc) {
                                            Global.ShowLogs("post-exc " + exc.getMessage());
                                            value = mainObj.optString(key);
                                        }
                                        Global.ShowLogs("post-key: " + key + ", Value: " + value);

                                        my_builder.addFormDataPart(key, value);
                                    }
                                } catch (Exception ee) {
                                }

                                try {
                                    if (backup_file.exists()) {
                                        my_builder.addFormDataPart(backup_file_key, backup_file.getName(),
                                                RequestBody.create(MediaType.parse("application/zip"), backup_file));
                                    }
                                } catch (Exception e) {
                                }

                                Request.Builder req_builder = new Request.Builder();

                                if (is_file_uploading) {
                                    com.axiskyc.custom.apis.ExMultipartBody exMultipartBody = new com.axiskyc.custom.apis.ExMultipartBody(my_builder.build(),
                                            pbar);
                                    req_builder.url(link);

                                    req_builder.addHeader("Content-Type", "application/json");

                                    if (headers.size() > 0) {
                                        for (int a = 0; a < headers.size(); a++) {
                                            req_builder.addHeader(headers.get(a).key, headers.get(a).value);
                                        }
                                    }

                                    req_builder.post(exMultipartBody);

                                } else {

                                    RequestBody formBody = my_builder.build();
                                    req_builder.url(link);

                                    req_builder.addHeader("Content-Type", "application/json");

                                    if (headers.size() > 0) {
                                        for (int a = 0; a < headers.size(); a++) {
                                            req_builder.addHeader(headers.get(a).key, headers.get(a).value);
                                        }
                                    }
                                    req_builder.post(formBody);
                                }

                                Request _request = req_builder.build();

                                if (headers.size() > 0) {
                                    for (int a = 0; a < headers.size(); a++) {
                                        _request.header(headers.get(a).value);
                                    }
                                }

                                try {
                                    Response response = OKhttpClient.newCall(_request).execute();
                                    response_status_code = response.code();
                                    Global.ShowLogs("response_status_code = " + response_status_code);

                                    if (bypass_200) {
                                        responseJson = "" + response.body().string();
                                    } else {

                                        if (response_status_code == 200) {
                                            responseJson = "" + response.body().string();
                                        } else {
                                            responseJson = "Failed : HTTP error code : " + response_status_code + ", " + response.message();
                                        }
                                    }

                                } catch (IOException e) {
                                    //e.printStackTrace();
                                    Global.ShowLogs("uploading-error = " + e.getMessage());
                                } catch (Exception ee) {
                                    Global.ShowLogs("uploading-Exception-error = " + ee.getMessage());
                                }

                            } catch (Exception e) {
                                Global.ShowLogs("last-error = " + e.getMessage());
                            }

                            break;
                        }
                        case DELETE: {
                            String link_append = "?";
                            try {
                                JSONObject mainObj = new JSONObject(mPostJson);
                                java.util.Iterator<String> iterator = mainObj.keys();
                                while (iterator.hasNext()) {
                                    String key = iterator.next();
                                    String value = mainObj.optString(key);
                                    value = URLEncoder.encode(value);
                                    Global.ShowLogs("getParams-key = " + key + " , value = " + value);
                                    link_append += key + "=" + value + "&";
                                }
                            } catch (Exception ee) {
                                Global.ShowLogs("getParams-error = " + ee.getMessage());
                            }

                            link = link + link_append;
                            Global.ShowLogs("getParams-link = " + link);

                            try {
                                httpClient = new CustomHttpClient();

                                org.apache.http.client.methods.HttpDelete httpDelete = new org.apache.http.client.methods.HttpDelete(link);

                                httpDelete.setHeader("Accept", "application/json");
                                httpDelete.setHeader("Content-Type", "application/json");

                                if (headers.size() > 0) {
                                    for (int a = 0; a < headers.size(); a++) {
                                        httpDelete.setHeader(headers.get(a).key, headers.get(a).value);
                                    }
                                }

                                try {
                                    for (int a = 0; a < httpDelete.getAllHeaders().length; a++) {
                                        Global.ShowLogs("header-key = " + httpDelete.getAllHeaders()[a].getName() + " , value = " + httpDelete.getAllHeaders()[a].getValue());
                                    }
                                } catch (Exception ee) {
                                }

                                org.apache.http.HttpResponse httpResponse = httpClient.execute(httpDelete);

                                response_status_code = httpResponse.getStatusLine().getStatusCode();
                                Global.ShowLogs("response_status_code = " + response_status_code);

                                if (bypass_200) {
                                    java.io.BufferedReader br = new java.io.BufferedReader(
                                            new java.io.InputStreamReader((httpResponse.getEntity().getContent())));

                                    StringBuffer str_buffer = new StringBuffer();
                                    while ((responseJson = br.readLine()) != null) {
                                        str_buffer.append(responseJson);
                                    }
                                    responseJson = str_buffer.toString();
                                } else {
                                    if (response_status_code == 200) {
                                        java.io.BufferedReader br = new java.io.BufferedReader(
                                                new java.io.InputStreamReader((httpResponse.getEntity().getContent())));

                                        StringBuffer str_buffer = new StringBuffer();
                                        while ((responseJson = br.readLine()) != null) {
                                            str_buffer.append(responseJson);
                                        }
                                        responseJson = str_buffer.toString();
                                    }
                                }

                                try {
                                    httpClient.getConnectionManager().shutdown();
                                } catch (Exception e) {
                                }

                                try {
                                    httpClient.getConnectionManager().closeExpiredConnections();
                                } catch (Exception e) {
                                }

                            } catch (org.apache.http.client.ClientProtocolException e) {

                            } catch (IOException e) {

                            }

                            break;
                        }
                        case PUT: {
                            try {
                                httpClient = new CustomHttpClient();

                                org.apache.http.client.methods.HttpPut httpDelete = new org.apache.http.client.methods.HttpPut(link);

                                StringEntity se = new StringEntity(mPostJson);
                                httpDelete.setEntity(se);

                                httpDelete.setHeader("Content-Type", "application/json");

                                if (headers.size() > 0) {
                                    for (int a = 0; a < headers.size(); a++) {
                                        httpDelete.setHeader(headers.get(a).key, headers.get(a).value);
                                    }
                                }

                                try {
                                    for (int a = 0; a < httpDelete.getAllHeaders().length; a++) {
                                        Global.ShowLogs("header-key = " + httpDelete.getAllHeaders()[a].getName() + " , value = " + httpDelete.getAllHeaders()[a].getValue());
                                    }
                                } catch (Exception ee) {
                                }

                                org.apache.http.HttpResponse httpResponse = httpClient.execute(httpDelete);

                                response_status_code = httpResponse.getStatusLine().getStatusCode();
                                Global.ShowLogs("response_status_code = " + response_status_code);

                                if (bypass_200) {
                                    java.io.BufferedReader br = new java.io.BufferedReader(
                                            new java.io.InputStreamReader((httpResponse.getEntity().getContent())));

                                    StringBuffer str_buffer = new StringBuffer();
                                    while ((responseJson = br.readLine()) != null) {
                                        str_buffer.append(responseJson);
                                    }
                                    responseJson = str_buffer.toString();
                                } else {
                                    if (response_status_code == 200) {
                                        java.io.BufferedReader br = new java.io.BufferedReader(
                                                new java.io.InputStreamReader((httpResponse.getEntity().getContent())));

                                        StringBuffer str_buffer = new StringBuffer();
                                        while ((responseJson = br.readLine()) != null) {
                                            str_buffer.append(responseJson);
                                        }
                                        responseJson = str_buffer.toString();
                                    }
                                }

                                try {
                                    httpClient.getConnectionManager().shutdown();
                                } catch (Exception e) {
                                }

                                try {
                                    httpClient.getConnectionManager().closeExpiredConnections();
                                } catch (Exception e) {
                                }

                            } catch (org.apache.http.client.ClientProtocolException e) {

                            } catch (IOException e) {

                            }
                        }
                        case POST_METHOD2: {
                            try {
                                httpClient = new CustomHttpClient();

                                org.apache.http.client.methods.HttpPost httpDelete = new org.apache.http.client.methods.HttpPost(link);

                                StringEntity se = new StringEntity(mPostJson);
                                httpDelete.setEntity(se);

                                httpDelete.setHeader("Content-Type", "application/json");

                                if (headers.size() > 0) {
                                    for (int a = 0; a < headers.size(); a++) {
                                        httpDelete.setHeader(headers.get(a).key, headers.get(a).value);
                                    }
                                }

                                try {
                                    for (int a = 0; a < httpDelete.getAllHeaders().length; a++) {
                                        Global.ShowLogs("header-key = " + httpDelete.getAllHeaders()[a].getName() + " , value = " + httpDelete.getAllHeaders()[a].getValue());
                                    }
                                } catch (Exception ee) {
                                }

                                org.apache.http.HttpResponse httpResponse = httpClient.execute(httpDelete);

                                response_status_code = httpResponse.getStatusLine().getStatusCode();
                                Global.ShowLogs("response_status_code = " + response_status_code);

                                if (bypass_200) {
                                    java.io.BufferedReader br = new java.io.BufferedReader(
                                            new java.io.InputStreamReader((httpResponse.getEntity().getContent())));

                                    StringBuffer str_buffer = new StringBuffer();
                                    while ((responseJson = br.readLine()) != null) {
                                        str_buffer.append(responseJson);
                                    }
                                    responseJson = str_buffer.toString();
                                } else {
                                    if (response_status_code == 200) {
                                        java.io.BufferedReader br = new java.io.BufferedReader(
                                                new java.io.InputStreamReader((httpResponse.getEntity().getContent())));

                                        StringBuffer str_buffer = new StringBuffer();
                                        while ((responseJson = br.readLine()) != null) {
                                            str_buffer.append(responseJson);
                                        }
                                        responseJson = str_buffer.toString();
                                    }
                                }

                                try {
                                    httpClient.getConnectionManager().shutdown();
                                } catch (Exception e) {
                                }

                                try {
                                    httpClient.getConnectionManager().closeExpiredConnections();
                                } catch (Exception e) {
                                }

                            } catch (org.apache.http.client.ClientProtocolException e) {
                                Global.ShowLogs("ClientProtocolException-error = " + e.getMessage());
                            } catch (IOException e) {
                                Global.ShowLogs("IOException-error = " + e.getMessage());
                            }
                        }
                    }

                    android.os.Message msg = new android.os.Message();
                    msg.what = 1;
                    ResponseHandler.sendMessage(msg);
                }
            };
            Thread th = new Thread(null, priceRunnable, "thread");
            th.start();
        }
    }

    public android.os.Handler ResponseHandler = new android.os.Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case 1:

                    try {

                        try {
                            httpClient = null;
                        } catch (Exception e) {
                        }

                        try {
                            OKhttpClient = null;
                        } catch (Exception e) {
                        }

                        try {
                            backup_pbar.setVisibility(View.INVISIBLE);
                        } catch (Exception ee) {
                        }

                        try {
                            cancelDialog.dismiss();
                            cancelDialog.dismiss();
                        } catch (Exception ee) {
                        }

                        mCallback.onServiceResponse(responseJson);

                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    super.handleMessage(msg);
            }
        }
    };
}
