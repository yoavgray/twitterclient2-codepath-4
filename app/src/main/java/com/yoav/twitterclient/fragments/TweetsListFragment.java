package com.yoav.twitterclient.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yoav.twitterclient.models.CurrentUser;
import com.yoav.twitterclient.models.Tweet;
import com.yoav.twitterclient.utils.EndlessRecyclerViewScrollListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TweetsListFragment extends BaseTweetListFragment {
    public final static String TWEETS_FILE_NAME = "tweetsFileName";


    public TweetsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (checkConnectivity()) {
            loadTweets();
        } else {
            renderConnectionErrorSnackBar(feedRecyclerView);
            loadTweetsFromFile();
        }
    }

    public void setFeedRecyclerView() {
        super.setFeedRecyclerView();

        feedRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (checkConnectivity()) {
                    showProgressBar();
                    loadTweets();
                } else {
                    renderConnectionErrorSnackBar(feedRecyclerView);
                }
            }
        });
    }

    /**
     * This method sets the SwipeRefreshLayout on start
     */
    public void setupSwipeRefreshLayout() {
        super.setupSwipeRefreshLayout();
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        showProgressBar();
                        if (checkConnectivity()) {
                            maxId = null;
                            loadTweets();
                        } else {
                            renderConnectionErrorSnackBar(feedRecyclerView);
                        }
                    }
                });
        }

    @Override
    public void reloadList() {
        maxId = null;
        loadTweets();
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
                    outputStream = getActivity().openFileOutput(TWEETS_FILE_NAME, Context.MODE_PRIVATE);
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                    for (int i = 0; i < newTweets.size(); i++) {
                        writer.write(newTweets.get(i).toJsonString() + '\n');
                    }
                    writer.close();
                } catch (IOException e) {
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
                    new InputStreamReader(getActivity().openFileInput(TWEETS_FILE_NAME)));
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

    public void loadTweets() {
        client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Gson gson = new GsonBuilder().create();
                Tweet[] tweets = gson.fromJson(response.toString(), Tweet[].class);

                if (maxId == null) {
                    tweetsList.clear();
                    tweetsList.addAll(Arrays.asList(tweets));
                    tweetsAdapter.notifyDataSetChanged();
                    persistToFile(true, tweetsList);
                } else {
                    int listSize = tweetsList.size();
                    tweetsList.addAll(Arrays.asList(tweets).subList(1, tweets.length - 1));
                    tweetsAdapter.notifyItemRangeInserted(listSize,20);
                    persistToFile(false, Arrays.asList(tweets));
                }
                maxId = tweets[tweets.length-1].getIdStr();

                swipeRefreshLayout.setRefreshing(false);
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null) {
                    Log.d("ON_FAILURE", errorResponse.toString());
                }
                swipeRefreshLayout.setRefreshing(false);
                hideProgressBar();
            }
        });
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

    private void renderConnectionErrorSnackBar(RecyclerView feedRecyclerView) {
        Snackbar
                .make(feedRecyclerView,
                        loadTweetsErrorString,
                        Snackbar.LENGTH_INDEFINITE)
                .setAction(retryString, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadTweets();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                })
                .setActionTextColor(Color.RED).show();
    }
}
