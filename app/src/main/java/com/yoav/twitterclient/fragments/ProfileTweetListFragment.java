package com.yoav.twitterclient.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yoav.twitterclient.R;
import com.yoav.twitterclient.models.Tweet;
import com.yoav.twitterclient.models.User;
import com.yoav.twitterclient.utils.EndlessRecyclerViewScrollListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.yoav.twitterclient.TwitterApplication.FAVORITES_KEY;
import static com.yoav.twitterclient.TwitterApplication.TIMELINE_KEY;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileTweetListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileTweetListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileTweetListFragment extends BaseTweetListFragment {

    private User user;
    private String type;

    public ProfileTweetListFragment() {
        // Required empty public constructor
    }

    public static ProfileTweetListFragment newInstance(User user, String type) {
        ProfileTweetListFragment fragment = new ProfileTweetListFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(user));
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = Parcels.unwrap(getArguments().getParcelable("user"));
            type = getArguments().getString("type");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile_tweet_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (checkConnectivity()) {
            if (type.equals(TIMELINE_KEY)) {
                loadUserTimeline();
            } else if (type.equals(FAVORITES_KEY)) {
                loadUserFavorites();
            }
        } else {
            renderConnectionErrorSnackBar(feedRecyclerView);
        }


    }

    public void setFeedRecyclerView() {
        super.setFeedRecyclerView();

        feedRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                showProgressBar();
                if (checkConnectivity()) {
                    if (type.equals(TIMELINE_KEY)) {
                        loadUserTimeline();
                    } else if (type.equals(FAVORITES_KEY)) {
                        loadUserFavorites();
                    }
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
                            if (type.equals(TIMELINE_KEY)) {
                                loadUserTimeline();
                            } else if (type.equals(FAVORITES_KEY)) {
                                loadUserFavorites();
                            }
                        } else {
                            renderConnectionErrorSnackBar(feedRecyclerView);
                        }
                    }
                });
    }

    @Override
    public void reloadList() {
        if (type.equals(TIMELINE_KEY)) {
            loadUserTimeline();
        } else if (type.equals(FAVORITES_KEY)) {
            loadUserFavorites();
        }
    }

    private void loadUserTimeline() {
        client.getUserTimeline(user.getIdStr(), maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Gson gson = new GsonBuilder().create();
                Tweet[] tweets = gson.fromJson(response.toString(), Tweet[].class);

                if (maxId == null) {
                    tweetsList.clear();
                    tweetsList.addAll(Arrays.asList(tweets));
                    tweetsAdapter.notifyDataSetChanged();
                } else {
                    // Now we're adding tweets to a list so we need to just add and not clear
                    // and also to remove duplicate
                    int listSize = tweetsList.size();
                    ArrayList<Tweet> newList = new ArrayList<>(Arrays.asList(tweets));
                    // Remove duplicate tweet
                    newList.remove(0);
                    tweetsList.addAll(newList);
                    tweetsAdapter.notifyItemRangeInserted(listSize,20);
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
                checkConnectivity();
                swipeRefreshLayout.setRefreshing(false);
                hideProgressBar();
            }
        });
    }

    private void loadUserFavorites() {
        client.getUserFavorites(user.getIdStr(), maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Gson gson = new GsonBuilder().create();
                Tweet[] tweets = gson.fromJson(response.toString(), Tweet[].class);

                if (maxId == null) {
                    tweetsList.clear();
                    tweetsList.addAll(Arrays.asList(tweets));
                    tweetsAdapter.notifyDataSetChanged();
                } else {
                    int listSize = tweetsList.size();
                    tweetsList.addAll(Arrays.asList(tweets).subList(1, tweets.length - 1));
                    tweetsAdapter.notifyItemRangeInserted(listSize,20);
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
                checkConnectivity();
                swipeRefreshLayout.setRefreshing(false);
                hideProgressBar();
            }
        });
    }

    private void renderConnectionErrorSnackBar(RecyclerView feedRecyclerView) {
        Snackbar
                .make(feedRecyclerView,
                        loadTweetsErrorString,
                        Snackbar.LENGTH_INDEFINITE)
                .setAction(retryString, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (type.equals(TIMELINE_KEY)) {
                            loadUserTimeline();
                        } else if (type.equals(FAVORITES_KEY)) {
                            loadUserFavorites();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                })
                .setActionTextColor(Color.RED).show();
    }

}
