package com.yoav.twitterclient.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yoav.twitterclient.R;
import com.yoav.twitterclient.TwitterClient;
import com.yoav.twitterclient.models.User;

import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfileActivity extends AppCompatActivity {
    private static final String USER_KEY = "userKey";
    private static final String USER_ID_KEY = "userId";
    @BindView(R.id.image_view_user_cover_photo) ImageView userCoverPhoto;
    @BindView(R.id.image_view_user_profile_photo) ImageView userProfilePhoto;
    @BindView(R.id.text_view_user_name) TextView userNameTextView;
    @BindView(R.id.text_view_user_screen_name) TextView userScreenNameTextView;
    @BindView(R.id.text_view_following_count) TextView followingCountTextView;
    @BindView(R.id.text_view_followers_count) TextView followersCountTextView;

    TwitterClient client;
    User user;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        client = new TwitterClient(this);

        if (savedInstanceState != null) {
            user = Parcels.unwrap(savedInstanceState.getParcelable(USER_KEY));
            loadProfile();
            loadUserTweets();
        } else {
            userId = getIntent().getStringExtra(USER_ID_KEY);
            loadUser(userId);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(USER_KEY, Parcels.wrap(user));
        super.onSaveInstanceState(outState);
    }

    private void loadUserTweets() {
    }

    private void loadProfile() {
        Glide.with(this).load(user.getCoverPhotoUrl().replace("normal", "bigger")).centerCrop().into(userCoverPhoto);
        Glide.with(this).load(user.getProfilePhotoUrl().replace("normal", "bigger")).centerCrop()
                .bitmapTransform(new RoundedCornersTransformation(this, 5, 5))
                .into(userProfilePhoto);
        userNameTextView.setText(user.getName());
        String screenName = "@" + user.getScreenName();
        userScreenNameTextView.setText(screenName);
        followersCountTextView.setText(String.valueOf(user.getFollowersCount()));
        followingCountTextView.setText(String.valueOf(user.getFollowingCount()));
    }

    private void loadUser(String userId) {
        client.getUser(userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new GsonBuilder().create();
                user = gson.fromJson(response.toString(), User.class);
//                Glide.with(getBaseContext()).load(currentUser.getProfileImageUrlHttps()).fitCenter()
//                        .bitmapTransform(new RoundedCornersTransformation(getBaseContext(), 5, 5))
//                        .into(userProfileImage);
                loadProfile();
                loadUserTweets();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null) {
                    Log.d("ON_FAILURE", errorResponse.toString());
                }
            }
        });

    }
}
