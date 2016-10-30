package com.yoav.twitterclient.models;

import org.parceler.Parcel;

@Parcel
public class CurrentUser {
    private String created_at;
    private String description;
    private Integer followersCount;
    private Integer friendsCount;
    private String id_str;
    private String name;
    private String profile_image_url;
    private String profile_image_url_https;
    private String screen_name;

    public CurrentUser() {}

    public CurrentUser(String created_at, String description, Integer followersCount, Integer friendsCount, String id_str, String name, String profile_image_url, String profile_image_url_https, String screenName) {
        this.created_at = created_at;
        this.description = description;
        this.followersCount = followersCount;
        this.friendsCount = friendsCount;
        this.id_str = id_str;
        this.name = name;
        this.profile_image_url = profile_image_url;
        this.profile_image_url_https = profile_image_url_https;
        this.screen_name = screenName;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public String getDescription() {
        return description;
    }

    public Integer getFollowersCount() {
        return followersCount;
    }

    public Integer getFriendsCount() {
        return friendsCount;
    }

    public String getIdStr() {
        return id_str;
    }

    public String getName() {
        return name;
    }

    public String getProfileImageUrl() {
        return profile_image_url;
    }

    public String getProfileImageUrlHttps() {
        return profile_image_url_https;
    }

    public String getScreenName() {
        return screen_name;
    }
}
