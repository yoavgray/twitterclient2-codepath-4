package com.yoav.twitterclient.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Url_ {
    @SerializedName("expanded_url")
    @Expose
    private String expandedUrl;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("indices")
    @Expose
    private List<Integer> indices = new ArrayList<>();
    @SerializedName("display_url")
    @Expose
    private String displayUrl;

    /**
     *
     * @return
     * The expandedUrl
     */
    public String getExpandedUrl() {
        return expandedUrl;
    }

    /**
     *
     * @param expandedUrl
     * The expanded_url
     */
    public void setExpandedUrl(String expandedUrl) {
        this.expandedUrl = expandedUrl;
    }

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *
     * @return
     * The indices
     */
    public List<Integer> getIndices() {
        return indices;
    }

    /**
     *
     * @param indices
     * The indices
     */
    public void setIndices(List<Integer> indices) {
        this.indices = indices;
    }

    /**
     *
     * @return
     * The displayUrl
     */
    public String getDisplayUrl() {
        return displayUrl;
    }

    /**
     *
     * @param displayUrl
     * The display_url
     */
    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }
}
