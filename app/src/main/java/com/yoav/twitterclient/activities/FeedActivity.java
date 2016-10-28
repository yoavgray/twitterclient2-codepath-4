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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yoav.twitterclient.fragments.ComposeTweetFragment;
import com.yoav.twitterclient.utils.DividerItemDecoration;
import com.yoav.twitterclient.utils.EndlessRecyclerViewScrollListener;
import com.yoav.twitterclient.utils.ItemClickSupport;
import com.yoav.twitterclient.R;
import com.yoav.twitterclient.adapters.TweetsAdapter;
import com.yoav.twitterclient.TwitterApplication;
import com.yoav.twitterclient.TwitterClient;
import com.yoav.twitterclient.models.Tweet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class FeedActivity extends AppCompatActivity implements ComposeTweetFragment.TweetComposedListener {
    @BindView(R.id.swipe_refresh_container) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_view_feed) RecyclerView feedRecyclerView;
    @BindView(R.id.fab_compose_tweet) FloatingActionButton composeFab;

    List<Tweet> tweetsList = new ArrayList<>();
    TweetsAdapter tweetsAdapter;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        setFeedRecyclerView();
        setRecyclerViewsListeners();
        setupSwipeRefreshLayout();

        client = TwitterApplication.getRestClient();
        loadTweets(1);
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
            RecyclerView.ItemDecoration itemDecoration = new
                    DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        feedRecyclerView.addItemDecoration(itemDecoration);

        feedRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                int i = 4;
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
    }

    public void loadTweets(int page) {
        client.getHomeTimeline(page, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Gson gson = new GsonBuilder().create();
                Tweet[] tweets = gson.fromJson(response.toString(), Tweet[].class);
                tweetsList.clear();
                tweetsList.addAll(Arrays.asList(tweets));
                tweetsAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getBaseContext(), "Failed: " + errorResponse.toString(), Toast.LENGTH_LONG).show();
                Log.d("ON_FAILURE", errorResponse.toString());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
