package com.yoav.twitterclient.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.yoav.twitterclient.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.List;

/*
 * This is a temporary, sample model that demonstrates the basic structure
 * of a SQLite persisted Model object. Check out the DBFlow wiki for more details:
 * https://github.com/pardom/ActiveAndroid/wiki/Creating-your-database-model
 *
 */

@Parcel
@Table(database = MyDatabase.class)
public class Tweet extends BaseModel {

	@PrimaryKey @Column Long tweetId;
	@Column String name;
	@Column String created_at;
	@Column String text;
	User user;

	public Tweet() {}

	public Long getTweetId() {
		return tweetId;
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

//
//	// Add a constructor that creates an object from the JSON response
//	public Tweet(JSONObject object){
//		super();
//
//		try {
//			this.userId = object.getString("id");
//			this.userHandle = object.getString("user_username");
//			this.timestamp = object.getString("timestamp");
//			this.body = object.getString("body");
//			this.user = object.getJSONObject("user");
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}

//	public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
//		ArrayList<Tweet> tweets = new ArrayList<>(jsonArray.length());
//
//		for (int i=0; i < jsonArray.length(); i++) {
//			JSONObject tweetJson;
//			try {
//				tweetJson = jsonArray.getJSONObject(i);
//			} catch (Exception e) {
//				e.printStackTrace();
//				continue;
//			}
//
//			Tweet tweet = new Tweet(tweetJson);
//			tweet.save();
//			tweets.add(tweet);
//		}
//
//		return tweets;
//	}



	// Record Finders
	public static Tweet byId(long id) {
		return new Select().from(Tweet.class).where(Tweet_Table.tweetId.eq(id)).querySingle();
	}

	public static List<Tweet> recentItems() {
		return new Select().from(Tweet.class).orderBy(Tweet_Table.tweetId, false).limit(300).queryList();
	}
}
