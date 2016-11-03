package com.yoav.twitterclient.models;

import org.parceler.Parcel;

@Parcel
public class CurrentUser {
    private String created_at;
    private String description;
    private Integer favourites_count;
    private Integer followers_count;
    private Integer friends_count;
    private Integer statuses_count;
    private String id_str;
    private String name;
    private String location;
    private String profile_image_url_https;
    private String profile_background_image_url_https;
    private String screen_name;
    private Entities entities;

    public CurrentUser() {}

    public String getCreatedAt() {
        return created_at;
    }

    public String getDescription() {
        return description;
    }

    public Integer getFavouritesCount() {
        return favourites_count;
    }

    public Integer getFollowersCount() {
        return followers_count;
    }

    public Integer getFriendsCount() {
        return friends_count;
    }

    public Integer getStatusesCount() {
        return statuses_count;
    }

    public String getIdStr() {
        return id_str;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getProfileImageUrlHttps() {
        return profile_image_url_https;
    }

    public String getProfileBackgroundImageUrlHttps() {
        return profile_background_image_url_https;
    }

    public String getScreenName() {
        return screen_name;
    }

    public Entities getEntities() {
        return entities;
    }
}
