package com.yoav.twitterclient.models;

import java.util.Arrays;
import java.util.List;

public class TweetResults {
    private Tweet[] results;

    public TweetResults(Tweet[] results) {
        this.results = results;
    }

    public List<Tweet> getResults() {
        return Arrays.asList(results);
    }
}
