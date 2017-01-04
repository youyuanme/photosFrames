package com.sibozn.peo.utils;

import com.sibozn.peo.interf.ProgressBarListener;

/**
 * Created by Administrator on 2016/11/28.
 */

public class MessageEvent {

    private ProgressBarListener progressBarListener;


    private int progress;
    private long what;
    private boolean isFinish;

    private boolean isCancleDownload;
    private String feature;//1是top,2是热门，3是最新


    public MessageEvent(boolean isCancleDownload, String feature) {
        this.isCancleDownload = isCancleDownload;
        this.feature = feature;
    }

    public MessageEvent(int progress,long what) {
        this.progress = progress;
        this.what = what;
    }

    public MessageEvent(boolean isFinish) {
        this.isFinish = isFinish;
    }

    public void setWhat(long what) {
        this.what = what;
    }

    public boolean isCancleDownload() {
        return isCancleDownload;
    }

    public void setCancleDownload(boolean cancleDownload) {
        isCancleDownload = cancleDownload;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public long getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public ProgressBarListener getProgressBarListener() {
        return progressBarListener;
    }

    public void setProgressBarListener(ProgressBarListener progressBarListener) {
        this.progressBarListener = progressBarListener;
    }
}
