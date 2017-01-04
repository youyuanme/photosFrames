package com.sibozn.peo.interf;

import com.yolanda.nohttp.Headers;

/**
 * Created by Administrator on 2016/11/25.
 */

public interface ProgressBarListener {
    void onDownloadError(int what, Exception exception);

    void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long
            allCount);

    void onProgress(int what, int progress, long fileCount);

    void onFinish(int what, String filePath);

    void onCancel(int what);
}
