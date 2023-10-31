package com.resources.uploadlib.http;

import com.resources.uploadlib.bean.ResourcesBean;

/**
 * @author LZY
 * @time 2021/4/8.
 */
public interface ProgressListener {

    void onProgress(int progress, ResourcesBean resBean);

    void onProgressDone( ResourcesBean resBean);
}
