package com.yoav.twitterclient.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yoav.twitterclient.fragments.ComposeTweetFragment;
import com.yoav.twitterclient.models.CurrentUser;
import com.yoav.twitterclient.models.Entities;
import com.yoav.twitterclient.models.ExtendedEntities;
import com.yoav.twitterclient.models.User;
import com.yoav.twitterclient.utils.EndlessRecyclerViewScrollListener;
import com.yoav.twitterclient.utils.ItemClickSupport;
import com.yoav.twitterclient.R;
import com.yoav.twitterclient.adapters.TweetsAdapter;
import com.yoav.twitterclient.TwitterApplication;
import com.yoav.twitterclient.TwitterClient;
import com.yoav.twitterclient.models.Tweet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class FeedActivity extends AppCompatActivity implements ComposeTweetFragment.TweetComposedListener {
    @BindView(R.id.swipe_refresh_container) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_view_feed) RecyclerView feedRecyclerView;
    @BindView(R.id.fab_compose_tweet) FloatingActionButton composeFab;
    @BindView(R.id.relative_layout_loading_tweets) RelativeLayout loadingTweetsRelativeLayout;
    @BindView(R.id.image_view_user_profile_image) ImageView userProfileImage;

    List<Tweet> tweetsList = new ArrayList<>();
    TweetsAdapter tweetsAdapter;
    TwitterClient client;
    CurrentUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        ButterKnife.bind(this);
        client = TwitterApplication.getRestClient();
        setSupportActionBar(toolbar);
        loadCurrentUserDetails();

        setFeedRecyclerView();
        setRecyclerViewsListeners();
        setupSwipeRefreshLayout();
        loadTweets(1);
    }

    private void loadCurrentUserDetails() {
        client.getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new GsonBuilder().create();
                currentUser = gson.fromJson(response.toString(), CurrentUser.class);
                getSupportActionBar().setDisplayUseLogoEnabled(true);
                toolbar.setTitle("@" + currentUser.getScreenName());
                Glide.with(getBaseContext()).load(currentUser.getProfileImageUrl()).fitCenter()
                        .bitmapTransform(new RoundedCornersTransformation(getBaseContext(), 5, 5))
                        .into(userProfileImage);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null) {
                    Toast.makeText(getBaseContext(), "Failed: " + errorResponse.toString(), Toast.LENGTH_LONG).show();
                    Log.d("ON_FAILURE", errorResponse.toString());
                }
                swipeRefreshLayout.setRefreshing(false);
                loadingTweetsRelativeLayout.setVisibility(View.GONE);
            }
        });
    }

    private void setRecyclerViewsListeners() {
        // Open the clicked article with a Chrome Custom Tab
        ItemClickSupport.addTo(feedRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    Toast.makeText(getBaseContext(), "Tweet clicked!", Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    private void setFeedRecyclerView() {
        tweetsAdapter = new TweetsAdapter(this, tweetsList);
        feedRecyclerView.setAdapter(tweetsAdapter);
        // Change number of columns when changing screen orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // Attach the layout manager to the recycler view
        feedRecyclerView.setLayoutManager(linearLayoutManager);
        feedRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadingTweetsRelativeLayout.setVisibility(View.VISIBLE);
                loadTweets(page + 1);
            }
        });
    }

    /**
     * This method sets the SwipeRefreshLayout on start
     */
    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadTweets(1);
                    }
                });
        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @OnClick(R.id.fab_compose_tweet)
    public void launchComposeTweetDialog() {
        FragmentManager fm = getFragmentManager();
        ComposeTweetFragment composeTweetFragment = ComposeTweetFragment.newInstance();
        composeTweetFragment.show(fm, "fragment_filter");
    }

    @Override
    public void onTweetComposed(String tweet) {
//        client.postTweet(tweet, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                loadTweets(1);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                super.onFailure(statusCode, headers, throwable, errorResponse);
//                Toast.makeText(getBaseContext(), "Failed: " + errorResponse.toString(), Toast.LENGTH_LONG).show();
//                Log.d("ON_FAILURE", errorResponse.toString());
//            }
//        });
        Tweet newTweet = new Tweet();
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
        String formattedDate = df.format(c.getTime());
        newTweet.setCreatedAt(formattedDate);
        newTweet.setText(tweet);
        newTweet.setUser(new User(currentUser));
        newTweet.setEntities(new Entities());
        newTweet.setExtendedEntities(new ExtendedEntities());
        tweetsList.add(0,newTweet);
        tweetsAdapter.notifyItemInserted(0);
        feedRecyclerView.smoothScrollToPosition(0);
    }

    public void loadTweets(final int page) {
        client.getHomeTimeline(page, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Gson gson = new GsonBuilder().create();
                Tweet[] tweets = gson.fromJson(response.toString(), Tweet[].class);
                if (page == 1) tweetsList.clear();
                tweetsList.addAll(Arrays.asList(tweets));
                tweetsAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                loadingTweetsRelativeLayout.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null) {
                    Toast.makeText(getBaseContext(), "Failed: " + errorResponse.toString(), Toast.LENGTH_LONG).show();
                    Log.d("ON_FAILURE", errorResponse.toString());
                }
                swipeRefreshLayout.setRefreshing(false);
                loadingTweetsRelativeLayout.setVisibility(View.GONE);
            }
        });
    }
}
