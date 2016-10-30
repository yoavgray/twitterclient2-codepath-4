package com.yoav.twitterclient.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class VideoInfo {
    private List<Integer> aspect_ratio = new ArrayList<>();
    private Integer duration_millis;
    private List<Variant> variants = new ArrayList<>();

    public VideoInfo() {}

    public List<Integer> getAspectRatio() {
        return aspect_ratio;
    }

    public Integer getDurationMillis() {
        return duration_millis;
    }

    public List<Variant> getVariants() {
        return variants;
    }
}
