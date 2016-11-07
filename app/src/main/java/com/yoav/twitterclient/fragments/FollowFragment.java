package com.yoav.twitterclient.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yoav.twitterclient.R;
import com.yoav.twitterclient.networking.TwitterClient;
import com.yoav.twitterclient.adapters.UsersAdapter;
import com.yoav.twitterclient.models.User;
import com.yoav.twitterclient.models.UsersList;
import com.yoav.twitterclient.utils.EndlessRecyclerViewScrollListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.yoav.twitterclient.TwitterApplication.USER_ID_KEY;

public class FollowFragment extends DialogFragment {
    private static final String TYPE_KEY = "type";
    private static final String FOLLOWERS_KEY = "followers";

    @BindView(R.id.recycler_view_follows) RecyclerView followRecyclerView;

    @BindString(R.string.load_users_error) protected String loadUsersErrorString;
    @BindString(R.string.retry) protected String retryString;

    List<User> users;
    UsersAdapter adapter;
    TwitterClient client;
    String nextCursor = "-1";
    String type = null;
    String userId = null;

    public FollowFragment() {
        // Required empty public constructor
    }

    public static FollowFragment newInstance(String userId, String type) {
        FollowFragment fragment = new FollowFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID_KEY, userId);
        args.putString(TYPE_KEY, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(USER_ID_KEY);
            type = getArguments().getString(TYPE_KEY);
        }
        users = new ArrayList<>();
        client = new TwitterClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow, container);
        ButterKnife.bind(this, view);

        if (type.equals(FOLLOWERS_KEY)) {
            getDialog().setTitle("Followers");
        } else {
            getDialog().setTitle("Following ");
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRecyclerView();
        if (checkConnectivity()) {
            if (type.equals(FOLLOWERS_KEY)) {
                loadFollowers();
            } else {
                loadFollowing();
            }
        }
    }

    private void loadFollowers() {
        client.getFollowersList(userId, nextCursor, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new GsonBuilder().create();
                UsersList usersList = gson.fromJson(response.toString(), UsersList.class);
                // If this is a fresh call, we want to clear the list
                if (nextCursor.equals("-1")) {
                    users.clear();
                }
                nextCursor = usersList.getNext_cursor_str();
                users.addAll(Arrays.asList(usersList.getUsers()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null) {
                    Log.d("ON_FAILURE", errorResponse.toString());
                }
                checkConnectivity();
            }
        });
    }

    private void loadFollowing() {
        client.getFollowingList(userId, nextCursor, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new GsonBuilder().create();
                UsersList usersList = gson.fromJson(response.toString(), UsersList.class);
                // If this is a fresh call, we want to clear the list
                if (nextCursor.equals("-1")) {
                    users.clear();
                }
                nextCursor = usersList.getNext_cursor_str();
                users.addAll(Arrays.asList(usersList.getUsers()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null) {
                    Log.d("ON_FAILURE", errorResponse.toString());
                }
                checkConnectivity();
            }
        });
    }

    public void setRecyclerView() {
        adapter = new UsersAdapter(getActivity(), users);
        followRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        // Attach the layout manager to the recycler view
        followRecyclerView.setLayoutManager(linearLayoutManager);

        followRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (checkConnectivity()) {
                    if (type.equals(FOLLOWERS_KEY)) {
                        loadFollowers();
                    } else {
                        loadFollowing();
                    }
                } else {
                    renderConnectionErrorSnackBar(followRecyclerView);
                }
            }
        });
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

    private void renderConnectionErrorSnackBar(RecyclerView feedRecyclerView) {
        Snackbar
                .make(followRecyclerView,
                        loadUsersErrorString,
                        Snackbar.LENGTH_INDEFINITE)
                .setAction(retryString, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadUsers();
                    }
                })
                .setActionTextColor(Color.RED).show();
    }

    private void loadUsers() {

    }
}
