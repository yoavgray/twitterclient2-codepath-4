package com.yoav.twitterclient.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yoav.twitterclient.R;
import com.yoav.twitterclient.TwitterApplication;
import com.yoav.twitterclient.TwitterClient;
import com.yoav.twitterclient.adapters.TweetsAdapter;
import com.yoav.twitterclient.models.Tweet;
import com.yoav.twitterclient.utils.EndlessRecyclerViewScrollListener;
import com.yoav.twitterclient.utils.ItemClickSupport;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.yoav.twitterclient.activities.FeedActivity.TWEETS_FILE_NAME;

public class TweetsListFragment extends Fragment {
    @BindView(R.id.swipe_refresh_container) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view_feed) RecyclerView feedRecyclerView;
    @BindView(R.id.relative_layout_loading_tweets) RelativeLayout loadingTweetsRelativeLayout;

    @BindString(R.string.load_tweets_error) String loadTweetsErrorString;
    @BindString(R.string.retry) String retryString;
    @BindString(R.string.cant_compose) String cantComposeString;

    List<Tweet> tweetsList = new ArrayList<>();
    TweetsAdapter tweetsAdapter;
    TwitterClient client;
    Unbinder unbinder;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TweetsListFragment() {
        // Required empty public constructor
    }


    public static TweetsListFragment newInstance(String param1, String param2) {
        TweetsListFragment fragment = new TweetsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        client = TwitterApplication.getRestClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweet_list, parent, false);
        // Set up view injection with ButterKnife
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setFeedRecyclerView();
        setRecyclerViewsListeners();
        setupSwipeRefreshLayout();
        if (checkConnectivity()) {
            loadTweets(1);
        } else {
            loadTweetsFromFile();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private void setRecyclerViewsListeners() {
        // Open the clicked article with a Chrome Custom Tab
        ItemClickSupport.addTo(feedRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                                                                            @Override
                                                                            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                FragmentManager fm = getFragmentManager();
                TweetDetailsFragment tweetDetailsFragment = TweetDetailsFragment.newInstance(tweetsList.get(position));
                /*tweetDetailsFragment.show(fm, "fragment_details");*/}
                                                                        }
        );
    }

    private void setFeedRecyclerView() {
        tweetsAdapter = new TweetsAdapter(getContext(), tweetsList);
        feedRecyclerView.setAdapter(tweetsAdapter);
        // Change number of columns when changing screen orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
                    outputStream = getContext().openFileOutput(TWEETS_FILE_NAME, Context.MODE_PRIVATE);
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
                    new InputStreamReader(getContext().openFileInput(TWEETS_FILE_NAME)));
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

    public void loadTweets(final int page) {
        client.getHomeTimeline(page, new JsonHttpResponseHandler() {
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
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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
