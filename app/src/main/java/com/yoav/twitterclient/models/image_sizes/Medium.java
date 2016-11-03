package com.yoav.twitterclient.models.image_sizes;

import org.parceler.Parcel;

@Parcel
public class Medium {
    private Integer w;
    private Integer h;
    private String resize;

    public Integer getW() {
        return w;
    }

    public Integer getH() {
        return h;
    }

    public String getResize() {
        return resize;
    }
}