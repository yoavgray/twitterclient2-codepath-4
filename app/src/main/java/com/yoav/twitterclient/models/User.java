package com.yoav.twitterclient.models;

import org.parceler.Parcel;

@Parcel
public class User {

    private String name;
    private String profile_background_tile;
    private Boolean profileBackgroundTile;
    private String profile_image_url;
    private String created_at;
    private String location;
    private Boolean follow_request_sent;
    private String id_str;
    private Boolean isTranslator;
    private Entities entities;
    private Boolean default_profile;
    private String url;
    private Integer favourites_count;
    private Integer utc_offset;
    private String profile_image_url_https;
    private Long id;
    private Integer listed_count;
    private Boolean profile_use_background_image;
    private String profile_text_color;
    private Integer followers_count;
    private String lang;
    private Boolean notifications;
    private String description;
    private String profile_background_color;
    private Boolean verified;
    private String time_zone;
    private String profile_background_image_url_https;
    private Integer statuses_count;
    private String profile_background_image_url;
    private Boolean default_profile_image;
    private Integer friends_count;
    private Boolean following;
    private String screen_name;

    public User() {}

    public User(CurrentUser otherUser) {
        name = otherUser.getName();
        screen_name = otherUser.getScreenName();
        profile_image_url = otherUser.getProfileImageUrl();
    }

    public String getName() {
        return name;
    }

    public String getPublishTime() {
        return created_at;
    }

    public String getNickname() {
        return screen_name;
    }

    public String getProfileImageUrl() {
        return profile_image_url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfileImageUrl(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public void setScreenName(String screen_name) {
        this.screen_name = screen_name;
    }
}




