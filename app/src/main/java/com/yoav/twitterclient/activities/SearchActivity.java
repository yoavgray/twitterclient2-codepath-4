package com.yoav.twitterclient.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yoav.twitterclient.R;
import com.yoav.twitterclient.TwitterClient;
import com.yoav.twitterclient.adapters.TweetsAdapter;
import com.yoav.twitterclient.models.SearchResult;
import com.yoav.twitterclient.models.Tweet;
import com.yoav.twitterclient.utils.EndlessRecyclerViewScrollListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements TweetsAdapter.OnTweetChangedListener {
    @BindView(R.id.recycler_view_feed) RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_container) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.relative_layout_loading_tweets)
    RelativeLayout relativeLayoutLoadingTweets;

    @BindString(R.string.load_tweets_error) String loadTweetsErrorString;
    @BindString(R.string.retry) String retryString;


    TwitterClient client;
    List<Tweet> tweetsList;
    TweetsAdapter adapter;
    String maxId = null;
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        client = new TwitterClient(this);
        tweetsList = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweetsList);
        setupRecyclerView();
        setupSwipeRefreshLayout();
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            searchTwitter();
        }
    }

    private void setupRecyclerView() {
        recyclerView.setAdapter(adapter);
        // Change number of columns when changing screen orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // Attach the layout manager to the recycler view
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (checkConnectivity()) {
//                    showProgressBar();
                    searchTwitter();
                } else {
                    //renderConnectionErrorSnackBar(feedRecyclerView);
                }
            }
        });
    }

    /**
     * This method sets the SwipeRefreshLayout on start
     */
    public void setupSwipeRefreshLayout() {
        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
//                        showProgressBar();
                        if (checkConnectivity()) {
                            maxId = null;
                            searchTwitter();
                        } else {
//                            renderConnectionErrorSnackBar(feedRecyclerView);
                        }
                    }
                });
    }

    private void searchTwitter() {
        client.searchTwitter(maxId, query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new GsonBuilder().create();
                SearchResult searchResult = gson.fromJson(response.toString(), SearchResult.class);
                Tweet[] tweets = searchResult.getStatuses();

                if (maxId == null) {
                    tweetsList.clear();
                    tweetsList.addAll(Arrays.asList(tweets));
                    adapter.notifyDataSetChanged();
                } else {
                    int listSize = tweetsList.size();
                    tweetsList.addAll(Arrays.asList(tweets).subList(1, tweets.length - 1));
                    adapter.notifyItemRangeInserted(listSize,20);
                }
                maxId = tweets[tweets.length-1].getIdStr();
                swipeRefreshLayout.setRefreshing(false);
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

    @Override
    public void onTweetFavorited(boolean isFavorited, String statusId) {
        if (isFavorited) {
            postFavorite(statusId);
        } else {
            postUnFavorite(statusId);
        }
    }

    @Override
    public void onTweetRetweeted(String statusId) {
        postRetweeted(statusId);
    }

    private void postRetweeted(final String statusId) {
        client.postRetweet(statusId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(getBaseContext(), "Retweeted Tweet!", Toast.LENGTH_SHORT).show();
                Gson gson = new GsonBuilder().create();
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                //tweetsListFragment.onRetweetSuccess(statusId);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null) {
                    Log.d("ON_FAILURE", errorResponse.toString());
                }

                checkConnectivity();
                Toast.makeText(getBaseContext(), "Failed Retweeting Tweet!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTweetDeleted(String statusId) {
        postDeleteTweet(statusId);
    }

    private void postDeleteTweet(final String statusId) {
        client.postDeleteTweet(statusId, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(getBaseContext(), "Deleted Tweet!", Toast.LENGTH_SHORT).show();
                Gson gson = new GsonBuilder().create();
                //tweetsListFragment.onDeleteSuccess(statusId);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null) {
                    Log.d("ON_FAILURE", errorResponse.toString());
                }

                checkConnectivity();
                Toast.makeText(getBaseContext(), "Failed deleting Tweet!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postUnFavorite(final String statusId) {
        client.postunFavorite(statusId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(getBaseContext(), "Unfavorited Tweet!", Toast.LENGTH_SHORT).show();
                Gson gson = new GsonBuilder().create();
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                //tweetsListFragment.favoriteTweet(false, statusId);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null) {
                    Log.d("ON_FAILURE", errorResponse.toString());
                }

                checkConnectivity();
                Toast.makeText(getBaseContext(), "Failed unfavoriting Tweet!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postFavorite(final String statusId) {
        client.postFavorite(statusId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(getBaseContext(), "Favorited Tweet!", Toast.LENGTH_SHORT).show();
                Gson gson = new GsonBuilder().create();
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                //tweetsListFragment.favoriteTweet(true, statusId);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null) {
                    Log.d("ON_FAILURE", errorResponse.toString());
                }

                checkConnectivity();
                Toast.makeText(getBaseContext(), "Failed favoriting Tweet!", Toast.LENGTH_SHORT).show();
            }
        });
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
                    .make(recyclerView,
                            loadTweetsErrorString,
                            Snackbar.LENGTH_INDEFINITE)
                    .setAction(retryString, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            loadCurrentUserDetails();
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
