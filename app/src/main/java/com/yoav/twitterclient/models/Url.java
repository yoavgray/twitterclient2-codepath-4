package com.yoav.twitterclient.models;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Url {

    private String url;
    private String expanded_url;
    private String display_url;
    private List<Integer> indices = new ArrayList<>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Url() {
    }

    /**
     *
     * @param displayUrl
     * @param indices
     * @param expandedUrl
     * @param url
     */
    public Url(String url, String expandedUrl, String displayUrl, List<Integer> indices) {
        this.url = url;
        this.expanded_url = expandedUrl;
        this.display_url = displayUrl;
        this.indices = indices;
    }

    public String getUrl() {
        return url;
    }

    public String getExpandedUrl() {
        return expanded_url;
    }

    public String getDisplayUrl() {
        return display_url;
    }

    public List<Integer> getIndices() {
        return indices;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
