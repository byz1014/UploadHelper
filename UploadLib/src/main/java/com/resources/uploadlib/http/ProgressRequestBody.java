package com.resources.uploadlib.http;

import com.resources.uploadlib.bean.ResourcesBean;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class ProgressRequestBody extends RequestBody {
    private RequestBody requestBody;
    private ProgressListener listener;
    private ResourcesBean useResourcesBean;

    private BufferedSink bufferedSink;

    public ProgressRequestBody(RequestBody requestBody) {
        this(requestBody,null, null);
    }

    public ProgressRequestBody(RequestBody requestBody, ResourcesBean resBean, ProgressListener listener) {
        this.requestBody = requestBody;
        this.listener = listener;
        this.useResourcesBean = resBean;
    }


    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        // 因为拦截器里面有个writeTo，导致你上传文件里面的RequestBody里面的writeTo触发了两次，数据翻倍，导致异常。
        // 所以这里去掉if判空
//        if (bufferedSink == null) {
        bufferedSink = Okio.buffer(sink(sink));
//        }
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    private Sink sink(BufferedSink sink) {
        return new ForwardingSink(sink) {
            //当前写入的字节
            long bytesWritten = 0;
            //总字节长度,避免多次调用contentLength()方法
            long contentLength = 0;
            int prevProgress;


            @Override
            public void write(@NotNull Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    contentLength = contentLength();
                    if (contentLength <= 0) {
                        contentLength = 1;
                    }
                }
                bytesWritten += byteCount;
                if (listener != null) {
                    int progress = (int) (1.0f * bytesWritten / contentLength * 100);
                    if (prevProgress != progress) {
                        prevProgress = progress;
                        if (bytesWritten >= contentLength) {
                            listener.onProgressDone(useResourcesBean);
                        } else {
                            listener.onProgress(progress,useResourcesBean);
                        }
                    }
                }
            }
        };
    }

}
