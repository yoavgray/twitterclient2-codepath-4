package com.yoav.twitterclient.models;

import org.parceler.Parcel;

@Parcel
public class Variant {
    private Integer bitrate;
    private String content_type;
    private String url;

    /**
     *
     * @return
     * The bitrate
     */
    public Integer getBitrate() {
        return bitrate;
    }

    /**
     *
     * @return
     * The contentType
     */
    public String getContentType() {
        return content_type;
    }

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

}