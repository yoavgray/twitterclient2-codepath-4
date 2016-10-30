package com.yoav.twitterclient.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Entities {

//    private List<Object> hashtags = new ArrayList<>();
//    private List<Object> symbols = new ArrayList<>();
//    private List<Object> user_mentions = new ArrayList<>();
    private List<Url> urls = new ArrayList<>();
    private List<Medium> media = new ArrayList<>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Entities() {
    }

    /**
     *
     * @param symbols
     * @param urls
     * @param hashtags
     * @param media
     * @param userMentions
     */
    public Entities(List<Object> hashtags, List<Object> symbols, List<Object> userMentions, List<Url> urls, List<Medium> media) {
//        this.hashtags = hashtags;
//        this.symbols = symbols;
//        this.user_mentions = userMentions;
        this.urls = urls;
        this.media = media;
    }

//    public List<Object> getHashtags() {
//        return hashtags;
//    }
//
//    public List<Object> getSymbols() {
//        return symbols;
//    }
//
//    public List<Object> getUserMentions() {
//        return user_mentions;
//    }

    public List<Url> getUrls() {
        return urls;
    }

    public List<Medium> getMedia() {
        return media;
    }
}