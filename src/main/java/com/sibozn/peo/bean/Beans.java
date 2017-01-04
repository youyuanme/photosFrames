package com.sibozn.peo.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2016/11/21.
 */
@Entity
public class Beans {
    @Id
    public Long id;
    //public String my_id;
    public String icon;
    public String name;
    public String Abstract;
    public String topic;
    public String Description;
    public String downlink;
    public String uplink;
    public String effectlink;
    public String landscape;
    public String price;
    public String online;
    public String feature;
    public String rank;
    public String isDownload;

    @Override
    public String toString() {
        return id + "#" + online + "#" + name + "#" + feature + "#" + topic + "#" + Description + "#" + downlink
                + "#" + uplink + "#" + isDownload + "#" + landscape + "#" + price + "#" + icon + "#" + Abstract
                + "#" + effectlink + "#" + rank;
    }

    @Generated(hash = 1874097928)
    public Beans(Long id, String icon, String name, String Abstract, String topic, String Description,
                 String downlink, String uplink, String effectlink, String landscape, String price, String online,
                 String feature, String rank, String isDownload) {
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.Abstract = Abstract;
        this.topic = topic;
        this.Description = Description;
        this.downlink = downlink;
        this.uplink = uplink;
        this.effectlink = effectlink;
        this.landscape = landscape;
        this.price = price;
        this.online = online;
        this.feature = feature;
        this.rank = rank;
        this.isDownload = isDownload;
    }

    @Generated(hash = 625343725)
    public Beans() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbstract() {
        return this.Abstract;
    }

    public void setAbstract(String Abstract) {
        this.Abstract = Abstract;
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDescription() {
        return this.Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getDownlink() {
        return this.downlink;
    }

    public void setDownlink(String downlink) {
        this.downlink = downlink;
    }

    public String getUplink() {
        return this.uplink;
    }

    public void setUplink(String uplink) {
        this.uplink = uplink;
    }

    public String getEffectlink() {
        return this.effectlink;
    }

    public void setEffectlink(String effectlink) {
        this.effectlink = effectlink;
    }

    public String getLandscape() {
        return this.landscape;
    }

    public void setLandscape(String landscape) {
        this.landscape = landscape;
    }

    public String getPrice() {
        return this.price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOnline() {
        return this.online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getFeature() {
        return this.feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getRank() {
        return this.rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getIsDownload() {
        return this.isDownload;
    }

    public void setIsDownload(String isDownload) {
        this.isDownload = isDownload;
    }

}
