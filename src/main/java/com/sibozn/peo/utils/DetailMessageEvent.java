package com.sibozn.peo.utils;

/**
 * Created by Administrator on 2016/11/30.
 */

public class DetailMessageEvent {

    private int progress;
    private String feature;//1是top,2是热门，3是最新
    private int position;
    private boolean isFinish;

    public DetailMessageEvent(boolean isFinish,String feature) {
        this.isFinish = isFinish;
        this.feature = feature;
    }

    public DetailMessageEvent(int progress, String feature, int position, boolean isFinish) {
        this.feature = feature;
        this.progress = progress;
        this.position = position;
        this.isFinish = isFinish;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }
}
