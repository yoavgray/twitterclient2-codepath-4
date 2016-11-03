package com.yoav.twitterclient.models;

import com.yoav.twitterclient.models.image_sizes.Sizes;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Media {

    private Long id;
    private String id_str;
    private List<Integer> indices = new ArrayList<Integer>();
    private String media_url;
    private String media_url_https;
    private String url;
    private String display_url;
    private String expanded_url;
    private String type;
    private VideoInfo video_info;
    private Sizes sizes;

    /**
     * No args constructor for use in serialization
     *
     */
    public Media() {
    }

    public Long getId() {
        return id;
    }

    public String getIdStr() {
        return id_str;
    }

    public List<Integer> getIndices() {
        return indices;
    }

    public String getMediaUrl() {
        return media_url;
    }

    public String getMediaUrlHttps() {
        return media_url_https;
    }

    public String getUrl() {
        return url;
    }

    public String getDisplayUrl() {
        return display_url;
    }

    public String getExpandedUrl() {
        return expanded_url;
    }

    public String getType() {
        return type;
    }

    public VideoInfo getVideoInfo() {
        return video_info;
    }

    public Sizes getSizes() {
        return sizes;
    }

}