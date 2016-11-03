package com.yoav.twitterclient.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Entities {

    private List<Url> urls = new ArrayList<>();
    private List<Media> media = new ArrayList<>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Entities() {
    }


    public Entities(List<Url> urls, List<Media> media) {
        this.urls = urls;
        this.media = media;
    }

    public List<Url> getUrls() {
        return urls;
    }

    public List<Media> getMedia() {
        return media;
    }
}