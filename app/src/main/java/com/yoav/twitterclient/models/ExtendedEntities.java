package com.yoav.twitterclient.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class ExtendedEntities {

    private List<Media> media = new ArrayList<Media>();

    /**
     *
     * @return
     * The media
     */
    public List<Media> getMedia() {
        return media;
    }
}
