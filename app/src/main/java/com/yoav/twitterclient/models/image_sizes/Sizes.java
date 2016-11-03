package com.yoav.twitterclient.models.image_sizes;

import org.parceler.Parcel;

@Parcel
public class Sizes {
    private Medium medium;
    private Thumb thumb;
    private Small small;
    private Large large;

    /**
     *
     * @return
     * The medium
     */
    public Medium getMedium() {
        return medium;
    }

    /**
     *
     * @return
     * The thumb
     */
    public Thumb getThumb() {
        return thumb;
    }

    /**
     *
     * @return
     * The small
     */
    public Small getSmall() {
        return small;
    }

    /**
     *
     * @return
     * The large
     */
    public Large getLarge() {
        return large;
    }
}
