package com.yoav.twitterclient.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yoav.twitterclient.models.Tweet;
import com.yoav.twitterclient.utils.EndlessRecyclerViewScrollListener;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MentionsListFragment extends BaseTweetListFragment {
    public final static String MENTIONS_FILE_NAME = "mentionsFileName";

    public MentionsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showProgressBar();
        if (checkConnectivity()) {
            loadMentions();
        } else {
            renderConnectionErrorSnackBar(feedRecyclerView);
            loadMentionsFromFile();
        }
    }

    @Override
    public void setFeedRecyclerView() {
        super.setFeedRecyclerView();
        feedRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (checkConnectivity()) {
                    loadingTweetsRelativeLayout.setVisibility(View.VISIBLE);
                    loadMentions();
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
                            loadMentions();
                        } else {
                            renderConnectionErrorSnackBar(feedRecyclerView);
                        }
                    }
                });
    }

    @Override
    public void reloadList() {
        maxId = null;
        loadMentions();
    }

    public void persistToFile(final boolean isNew, final List<Tweet> newTweets) {
        // Persist to file another thread to not clog the main thread
        Thread otherThread = new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream outputStream;
                try {
                    outputStream = getContext().openFileOutput(MENTIONS_FILE_NAME, Context.MODE_PRIVATE);
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

    public void loadMentionsFromFile() {
        BufferedReader input;
        Gson gson = new GsonBuilder().create();
        try {
            input = new BufferedReader(
                    new InputStreamReader(getContext().openFileInput(MENTIONS_FILE_NAME)));
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

    public void loadMentions() {
        client.getMentionsTimeline(maxId, new JsonHttpResponseHandler() {
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
                    // Now we're adding tweets to a list so we need to just add and not clear
                    // and also to remove duplicate
                    int listSize = tweetsList.size();
                    ArrayList<Tweet> newList = new ArrayList<>(Arrays.asList(tweets));
                    // Remove duplicate tweet
                    newList.remove(0);
                    tweetsList.addAll(newList);
                    tweetsAdapter.notifyItemRangeInserted(listSize,20);
                    persistToFile(false, Arrays.asList(tweets));
                }
                maxId = tweets[tweets.length-1].getIdStr();

                swipeRefreshLayout.setRefreshing(false);
                hideProgressBar();
            }
        });
    }
//
//    public void clearTweetsFile() {
//        PrintWriter writer;
//        try {
//            writer = new PrintWriter(MENTIONS_FILE_NAME);
//            writer.print("");
//            writer.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

    private void renderConnectionErrorSnackBar(RecyclerView feedRecyclerView) {
        Snackbar
                .make(feedRecyclerView,
                        loadTweetsErrorString,
                        Snackbar.LENGTH_INDEFINITE)
                .setAction(retryString, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadMentions();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                })
                .setActionTextColor(Color.RED).show();
    }
}
