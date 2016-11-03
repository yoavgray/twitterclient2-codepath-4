package com.yoav.twitterclient.models.image_sizes;

import org.parceler.Parcel;

@Parcel
public class Small {
    private Integer w;
    private Integer h;
    private String resize;

    /**
     *
     * @return
     * The w
     */
    public Integer getW() {
        return w;
    }

    /**
     *
     * @return
     * The h
     */
    public Integer getH() {
        return h;
    }

    /**
     *
     * @return
     * The resize
     */
    public String getResize() {
        return resize;
    }
}