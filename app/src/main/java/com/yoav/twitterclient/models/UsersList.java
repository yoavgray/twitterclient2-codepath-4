package com.yoav.twitterclient.models;

import org.parceler.Parcel;

@Parcel
public class UsersList {

    private String previous_cursor_str;
    private String next_cursor_str;
    private User[] users;

    public String getPrevious_cursor_str() {
        return previous_cursor_str;
    }

    public String getNext_cursor_str() {
        return next_cursor_str;
    }

    public User[] getUsers() {
        return users;
    }

}
