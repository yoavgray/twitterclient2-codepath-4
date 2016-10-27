package com.yoav.twitterclient;

import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yoav.twitterclient.models.Tweet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class FeedActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.feed_recycler_view) RecyclerView feedRecyclerView;
    @BindView(R.id.compose_tweet_fab) FloatingActionButton composeFab;


    List<Tweet> tweetsList = new ArrayList<>();
    TweetsAdapter tweetsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        composeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setFeedRecyclerView();
        setRecyclerViewsListeners();

        TwitterClient client = TwitterApplication.getRestClient();
        client.getHomeTimeline(1, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Gson gson = new GsonBuilder().create();
                Tweet[] tweets = gson.fromJson(response.toString(), Tweet[].class);
                tweetsList.clear();
                tweetsList.addAll(Arrays.asList(tweets));
                tweetsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getBaseContext(), "Failed: " + errorResponse.toString(), Toast.LENGTH_LONG).show();
                Log.d("ON_FAILURE", errorResponse.toString());
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
//            RecyclerView.ItemDecoration itemDecoration = new
//                SpacesItemDecoration(10);
//        articlesRecyclerView.addItemDecoration(itemDecoration);

        feedRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {


            }
        });
    }

}
