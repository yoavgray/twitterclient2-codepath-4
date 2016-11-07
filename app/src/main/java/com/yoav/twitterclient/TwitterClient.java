package com.yoav.twitterclient;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "7VnT8jrXALc1VxKUETxu3V3vO";
	public static final String REST_CONSUMER_SECRET = "RE7kMRNKDiMwvMb2VqzklhckOrD7YuA5rDLBZyI7qJScbkT8Hn";
	public static final String REST_CALLBACK_URL = "oauth://codepathtwittsyoavibavi"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getHomeTimeline(String maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
        if (maxId != null) {
            params.put("max_id", maxId);
        } else {
            params.put("count", String.valueOf(20));
        }
		getClient().get(apiUrl, params, handler);
	}

    public void getMentionsTimeline(String maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        if (maxId != null) {
            params.put("max_id", maxId);
        } else {
            params.put("count", String.valueOf(20));
        }
        getClient().get(apiUrl, params, handler);
    }

    public void getUserTimeline(String userId, String maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("user_id", userId);
        if (maxId != null) {
            params.put("max_id", maxId);
        } else {
            params.put("count", String.valueOf(20));
        }
        getClient().get(apiUrl, params, handler);
    }

    public void getUserFavorites(String userId, String maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("favorites/list.json");
        RequestParams params = new RequestParams();
        params.put("user_id", userId);
        if (maxId != null) {
            params.put("max_id", maxId);
        } else {
            params.put("count", String.valueOf(20));
        }
        getClient().get(apiUrl, params, handler);
    }

	public void getCurrentUser(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		RequestParams params = new RequestParams();
        params.put("include_entities", true);
        params.put("include_email", true);
		getClient().get(apiUrl, params, handler);
	}

    public void getUser(String userId, String screenName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("users/show.json");
        RequestParams params = new RequestParams();
        if (userId == null) {
            params.put("screen_name", screenName);
        } else {
            params.put("user_id", userId);
        }
        getClient().get(apiUrl, params, handler);
    }

    public void getFollowersList(String userId, String nextCursor, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("followers/list.json");
        RequestParams params = new RequestParams();
        params.put("user_id", userId);
        params.put("cursor", nextCursor);
        getClient().get(apiUrl, params, handler);
    }

    public void getFollowingList(String userId, String nextCursor, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("friends/list.json");
        RequestParams params = new RequestParams();
        params.put("user_id", userId);
        params.put("cursor", nextCursor);
        getClient().get(apiUrl, params, handler);
    }

	public void postTweet(String body, String screenName, String statusId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", body);
        if (!statusId.equals("")) {
            params.put("in_reply_to_status_id", statusId);
        }
        getClient().post(apiUrl, params, handler);
	}

    public void postRetweet(String statusId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/retweet/" + statusId + ".json");
        RequestParams params = new RequestParams();
        params.put("id", statusId);
        getClient().post(apiUrl, params, handler);
    }

    public void postUnRetweet(String statusId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/unretweet/" + statusId + ".json");
        RequestParams params = new RequestParams();
        params.put("id", statusId);
        getClient().post(apiUrl, params, handler);
    }

    public void postFavorite(String statusId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("favorites/create.json");
        RequestParams params = new RequestParams();
        params.put("id", statusId);
        getClient().post(apiUrl, params, handler);
    }

    public void postunFavorite(String statusId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("favorites/destroy.json");
        RequestParams params = new RequestParams();
        params.put("id", statusId);
        getClient().post(apiUrl, params, handler);
    }

    public void postDeleteTweet(String statusId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/destroy/" + statusId + ".json");
        RequestParams params = new RequestParams();
        params.put("id", statusId);
        getClient().post(apiUrl, params, handler);
    }

    public void postFollowUser(String userId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("friendships/create.json");
        RequestParams params = new RequestParams();
        params.put("user_id", userId);
        // Allow notifications from user
        params.put("follow", true);
        getClient().post(apiUrl, params, handler);
    }

    public void postUnfollowUser(String userId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("friendships/destroy.json");
        RequestParams params = new RequestParams();
        params.put("user_id", userId);
        // Allow notifications from user
        params.put("follow", true);
        getClient().post(apiUrl, params, handler);
    }

    public void searchTwitter(String maxId, String query, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("search/tweets.json");
        RequestParams params = new RequestParams();
        try {
            if (maxId != null) {
                params.put("max_id", maxId);
            }
            params.put("q", URLEncoder.encode(query, "UTF-8"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getClient().get(apiUrl, params, handler);
    }
}