package com.yoav.twitterclient.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class Entities {

    @SerializedName("url")
    @Expose
    private Url url;

    public Url getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(Url url) {
        this.url = url;
    }
}