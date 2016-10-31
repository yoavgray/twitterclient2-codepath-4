package com.yoav.twitterclient.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Entities {

    private List<Url> urls = new ArrayList<>();
    private List<Medium> media = new ArrayList<>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Entities() {
    }


    public Entities(List<Url> urls, List<Medium> media) {
        this.urls = urls;
        this.media = media;
    }

    public List<Url> getUrls() {
        return urls;
    }

    public List<Medium> getMedia() {
        return media;
    }
}