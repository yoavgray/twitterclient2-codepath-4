package com.yoav.twitterclient.models;

import android.text.format.DateUtils;

import com.google.gson.Gson;

import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Parcel
public class Tweet {
	private String name;
	private String created_at;
	private String text;
	private Entities entities;
	private ExtendedEntities extended_entities;
    private User user;
    private Integer favorite_count;
    private Integer retweet_count;
    private String in_reply_to_screen_name;

	public Tweet() {}

    public Integer getFavoriteCount() {
        return favorite_count;
    }

    public Integer getRetweetCount() {
        return retweet_count;
    }

    public String getInReplyToScreenName() {
        return in_reply_to_screen_name;
    }

    public String getName() {
		return name;
	}

	public String getCreatedAt() {
		return created_at;
	}

	public String getText() {
		return text;
	}

	public User getUser() {
		return user;
	}

	public Entities getEntities() {
		return entities;
	}

    public ExtendedEntities getExtendedEntities() {
        return extended_entities;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setEntities(Entities entities) {
        this.entities = entities;
    }

    public void setExtendedEntities(ExtendedEntities extended_entities) {
        this.extended_entities = extended_entities;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String toJsonString() {
        return new Gson().toJson(this);
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Making the string look like "1h", "1m", etc.
        String[] relativeDateSplitted = relativeDate.split(" ");
        if (relativeDateSplitted[1].contains("second")) {
            return (relativeDateSplitted[0] + "s");
        } else if (relativeDateSplitted[1].contains("minute")) {
            return relativeDateSplitted[0] + "m";
        } else if (relativeDateSplitted[1].contains("hour")) {
            return relativeDateSplitted[0] + "h";
        } else if (relativeDateSplitted[1].contains("day")) {
            return relativeDateSplitted[0] + "d";
        } else if (relativeDateSplitted[1].contains("month")) {
            return relativeDateSplitted[0] + "M";
        } else {
            return relativeDateSplitted[0] + "y";
        }
    }
}
