package com.sibozn.peo.bean;

/**
 * Created by Administrator on 2016/11/26.
 */

public class DetailBean {
    private String id;
    private String low_pic;
    private String pic;
    private String name;
    private String api_id;

    @Override
    public String toString() {
        return "DetailBean{" +
                "id='" + id + '\'' +
                ", low_pic='" + low_pic + '\'' +
                ", pic='" + pic + '\'' +
                ", name='" + name + '\'' +
                ", api_id='" + api_id + '\'' +
                '}';
    }

    public DetailBean(String id, String low_pic, String pic, String name, String api_id) {
        this.id = id;
        this.low_pic = low_pic;
        this.pic = pic;
        this.name = name;
        this.api_id = api_id;
    }

    public DetailBean(String id, String low_pic, String pic) {
        this.id = id;
        this.low_pic = low_pic;
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApi_id() {
        return api_id;
    }

    public void setApi_id(String api_id) {
        this.api_id = api_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLow_pic() {
        return low_pic;
    }

    public void setLow_pic(String low_pic) {
        this.low_pic = low_pic;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
