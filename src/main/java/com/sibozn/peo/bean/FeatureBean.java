package com.sibozn.peo.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2016/11/21.
 */

@Entity
public class FeatureBean {
    @Id
    private Long id;
    private String icon;
    private String name;
    private String Abstract;
    private String topic;
    private String Description;
    private String downlink;
    private String uplink;
    private String effectlink;
    private String landscape;
    private String price;
    private String online;
    private String feature;
    private String rank;

    @Generated(hash = 601503588)
    public FeatureBean(Long id, String icon, String name, String Abstract,
                       String topic, String Description, String downlink, String uplink,
                       String effectlink, String landscape, String price, String online,
                       String feature, String rank) {
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
    }

    @Override
    public String toString() {
        return "FeatureBean{" +
                "id=" + id +
                ", icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                ", Abstract='" + Abstract + '\'' +
                ", topic='" + topic + '\'' +
                ", Description='" + Description + '\'' +
                ", downlink='" + downlink + '\'' +
                ", uplink='" + uplink + '\'' +
                ", effectlink='" + effectlink + '\'' +
                ", landscape='" + landscape + '\'' +
                ", price='" + price + '\'' +
                ", online='" + online + '\'' +
                ", feature='" + feature + '\'' +
                ", rank='" + rank + '\'' +
                '}';
    }

    @Generated(hash = 1520028243)
    public FeatureBean() {
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

}
