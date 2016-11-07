package com.yoav.twitterclient.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.yoav.twitterclient.fragments.TweetDetailsFragment;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class FeedActivity extends AppCompatActivity {
    public final static String TWEETS_FILE_NAME = "tweetsFileName";

    @BindView(R.id.swipe_refresh_container) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_view_feed) RecyclerView feedRecyclerView;
    @BindView(R.id.fab_compose_tweet) FloatingActionButton composeFab;
    @BindView(R.id.relative_layout_loading_tweets) RelativeLayout loadingTweetsRelativeLayout;
    @BindView(R.id.image_view_user_profile_image) ImageView userProfileImage;

    @BindString(R.string.load_tweets_error) String loadTweetsErrorString;
    @BindString(R.string.retry) String retryString;
    @BindString(R.string.cant_compose) String cantComposeString;


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

        setFeedRecyclerView();
        setRecyclerViewsListeners();
        setupSwipeRefreshLayout();
        if (checkConnectivity()) {
            loadCurrentUserDetails();
            loadTweets(1);
        } else {
            loadTweetsFromFile();
        }
    }

    private void loadCurrentUserDetails() {
        client.getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new GsonBuilder().create();
                currentUser = gson.fromJson(response.toString(), CurrentUser.class);
                getSupportActionBar().setDisplayUseLogoEnabled(true);
                toolbar.setTitle("@" + currentUser.getScreenName());
                Glide.with(getBaseContext()).load(currentUser.getProfileImageUrlHttps()).fitCenter()
                        .bitmapTransform(new RoundedCornersTransformation(getBaseContext(), 5, 5))
                        .into(userProfileImage);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null) {

                    Log.d("ON_FAILURE", errorResponse.toString());
                }
                checkConnectivity();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void setRecyclerViewsListeners() {
        // Open the clicked article with a Chrome Custom Tab
        ItemClickSupport.addTo(feedRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    FragmentManager fm = getFragmentManager();
                    TweetDetailsFragment tweetDetailsFragment = TweetDetailsFragment.newInstance(tweetsList.get(position));
                    tweetDetailsFragment.show(fm, "fragment_details");
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
                if (checkConnectivity()) {
                    loadingTweetsRelativeLayout.setVisibility(View.VISIBLE);
                    loadTweets(page + 1);
                }
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
                        if (checkConnectivity()) {
                            loadCurrentUserDetails();
                            loadTweets(1);
                        }
                    }
                });
        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }


    public void loadTweets(final int page) {
        client.getHomeTimeline("", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Gson gson = new GsonBuilder().create();
                Tweet[] tweets = gson.fromJson(response.toString(), Tweet[].class);

                if (page == 1) {
                    tweetsList.clear();
                    tweetsList.addAll(Arrays.asList(tweets));
                    tweetsAdapter.notifyDataSetChanged();
                    persistToFile(true, tweetsList);
                } else {
                    // If page > 1 we want to persist only the new list
                    int listSize = tweetsList.size();
                    tweetsList.addAll(Arrays.asList(tweets));
                    tweetsAdapter.notifyItemRangeInserted(listSize,20);
                    persistToFile(false, Arrays.asList(tweets));
                }

                swipeRefreshLayout.setRefreshing(false);
                loadingTweetsRelativeLayout.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null) {
                    Log.d("ON_FAILURE", errorResponse.toString());
                }
                swipeRefreshLayout.setRefreshing(false);
                loadingTweetsRelativeLayout.setVisibility(View.GONE);
            }
        });
    }

    public void persistToFile(final boolean isNew, final List<Tweet> newTweets) {
        // Persist to file another thread to not clog the main thread
        Thread otherThread = new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream outputStream;
                try {
                    if (isNew) {
                        clearTweetsFile();
                    }
                    outputStream = openFileOutput(TWEETS_FILE_NAME, Context.MODE_PRIVATE);
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                    for (int i = 0; i < newTweets.size(); i++) {
                        writer.write(newTweets.get(i).toJsonString() + '\n');
                    }
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        otherThread.run();
    }

    public void loadTweetsFromFile() {
        BufferedReader input;
        Gson gson = new GsonBuilder().create();
        try {
            input = new BufferedReader(
                    new InputStreamReader(openFileInput(TWEETS_FILE_NAME)));
            String line;
            while ((line = input.readLine()) != null) {
                Tweet currentTweet = gson.fromJson(line, Tweet.class);
                tweetsList.add(currentTweet);
            }
            tweetsAdapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void clearTweetsFile() {
        PrintWriter writer;
        try {
            writer = new PrintWriter(TWEETS_FILE_NAME);
            writer.print("");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method checks connectivity and renders a Snackbar if there's a problem
     */
    private boolean checkConnectivity() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
        if (!isConnected || !isOnline()) {
            Snackbar
                .make(feedRecyclerView,
                        loadTweetsErrorString,
                        Snackbar.LENGTH_INDEFINITE)
                .setAction(retryString, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadTweets(1);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                })
                .setActionTextColor(Color.RED).show();
            return false;
        }
        return true;
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e)
        { e.printStackTrace(); }
        return false;
    }
}
