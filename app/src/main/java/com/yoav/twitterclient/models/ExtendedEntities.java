package com.yoav.twitterclient.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class ExtendedEntities {

    private List<Medium> media = new ArrayList<Medium>();

    /**
     *
     * @return
     * The media
     */
    public List<Medium> getMedia() {
        return media;
    }
}
