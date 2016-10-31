package com.yoav.twitterclient.models;

import com.google.gson.Gson;

import org.parceler.Parcel;

@Parcel
public class Tweet {
	private String name;
	private String created_at;
	private String text;
	private Entities entities;
	private ExtendedEntities extended_entities;
    private User user;

	public Tweet() {}

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
}
