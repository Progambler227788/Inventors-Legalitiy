package com.axiskyc.custom.apis;


import android.util.Log;
import android.widget.ProgressBar;

import com.axiskyc.custom.Global;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;

public class ExMultipartBody extends RequestBody {
    private RequestBody mRequestBody;
    private int mCurrentLength;
    private ProgressBar mProgressListener;

    public ExMultipartBody(MultipartBody requestBody) {
        this.mRequestBody = requestBody;
    }

    public ExMultipartBody(MultipartBody requestBody, ProgressBar progressListener) {
        this.mRequestBody = requestBody;
        this.mProgressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        // The static proxy ultimately still calls the method of the proxy object
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Log.e("TAG", "Monitor");
        // total length
        final long contentLength = contentLength();
        // Get how much data is currently written? BufferedSink Sink (okio is io) is the output stream of a server, I still don't know how much data was written

        // Another agent ForwardingSink
        ForwardingSink forwardingSink = new ForwardingSink(sink) {
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                // come here every time I write
                mCurrentLength += byteCount;

                try {
                    mProgressListener.setMax((int) contentLength);
                    mProgressListener.setProgress(mCurrentLength);
                } catch (Exception ee) {
                    Global.ShowLogs("progress-error = " + ee.getMessage());
                }

                Global.ShowLogs("mCurrentLength = " + mCurrentLength + " , contentLength = " + contentLength + " , byteCount = " + byteCount);

                super.write(source, byteCount);
            }
        };
        // Turn around
        BufferedSink bufferedSink = Okio.buffer(forwardingSink);
        mRequestBody.writeTo(bufferedSink);
        // Refresh, RealConnection connection pool
        bufferedSink.flush();
    }
}
