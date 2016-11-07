package com.yoav.twitterclient.models;

import org.parceler.Parcel;

@Parcel
public class SearchResult {
    Tweet[] statuses;

    public Tweet[] getStatuses() {
        return statuses;
    }
}
