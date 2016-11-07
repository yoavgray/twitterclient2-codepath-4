package com.yoav.twitterclient.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yoav.twitterclient.R;
import com.yoav.twitterclient.TwitterApplication;
import com.yoav.twitterclient.TwitterClient;
import com.yoav.twitterclient.adapters.TweetsAdapter;
import com.yoav.twitterclient.models.Tweet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseTweetListFragment extends Fragment {
    @BindView(R.id.swipe_refresh_container) protected SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view_feed) protected RecyclerView feedRecyclerView;
    @BindView(R.id.relative_layout_loading_tweets) protected RelativeLayout loadingTweetsRelativeLayout;

    @BindString(R.string.load_tweets_error) protected String loadTweetsErrorString;
    @BindString(R.string.retry) protected String retryString;
    @BindString(R.string.cant_compose) protected String cantComposeString;

    protected List<Tweet> tweetsList = new ArrayList<>();
    protected TweetsAdapter tweetsAdapter;
    protected TwitterClient client;
    protected Unbinder unbinder;
    protected LinearLayoutManager linearLayoutManager;
    protected String maxId = null;

    private OnFragmentInteractionListener mListener;

    public BaseTweetListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setupSwipeRefreshLayout();
    }

    public void setFeedRecyclerView() {
        tweetsAdapter = new TweetsAdapter(getContext(), tweetsList);
        feedRecyclerView.setAdapter(tweetsAdapter);
        // Change number of columns when changing screen orientation
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        // Attach the layout manager to the recycler view
        feedRecyclerView.setLayoutManager(linearLayoutManager);
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
    }

    public void addNewTweetToList(Tweet tweet) {
        tweetsList.add(0, tweet);
        tweetsAdapter.notifyItemInserted(0);
        feedRecyclerView.smoothScrollToPosition(0);
        reloadList();
    }

    public void favoriteTweet(boolean isFavorited, String statusId) {
        for (int i = 0; i < tweetsList.size(); i++) {
            Tweet tweet = tweetsList.get(i);
            if (tweet.getIdStr().equals(statusId)) {
                tweet.setFavorited(isFavorited);
                tweet.setWasFavorited(isFavorited);
                tweetsAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    public void onRetweetSuccess(String statusId) {
        for (int i = 0; i < tweetsList.size(); i++) {
            Tweet tweet = tweetsList.get(i);
            if (tweet.getIdStr().equals(statusId)) {
                tweet.setWasRetweeted(true);
                tweetsAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    public abstract void reloadList();

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
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * This method checks connectivity and renders a Snackbar if there's a problem
     */
    public boolean checkConnectivity() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
        return isConnected && isOnline();
    }

    protected boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e)
        { e.printStackTrace(); }
        return false;
    }

    protected void showProgressBar() {
        loadingTweetsRelativeLayout.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar() {
        loadingTweetsRelativeLayout.setVisibility(View.GONE);
    }
}
