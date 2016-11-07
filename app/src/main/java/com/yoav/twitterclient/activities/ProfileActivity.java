package com.yoav.twitterclient.activities;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yoav.twitterclient.R;
import com.yoav.twitterclient.TwitterClient;
import com.yoav.twitterclient.adapters.TweetsAdapter;
import com.yoav.twitterclient.adapters.ViewPagerAdapter;
import com.yoav.twitterclient.fragments.BaseTweetListFragment;
import com.yoav.twitterclient.fragments.FollowFragment;
import com.yoav.twitterclient.fragments.ProfileTweetListFragment;
import com.yoav.twitterclient.models.Tweet;
import com.yoav.twitterclient.models.User;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.lang.reflect.Field;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.yoav.twitterclient.TwitterApplication.FAVORITES_KEY;
import static com.yoav.twitterclient.TwitterApplication.TIMELINE_KEY;
import static com.yoav.twitterclient.TwitterApplication.USER_ID_KEY;
import static com.yoav.twitterclient.TwitterApplication.USER_KEY;

public class ProfileActivity extends AppCompatActivity implements ProfileTweetListFragment.OnFragmentInteractionListener,
        TweetsAdapter.OnTweetChangedListener {
    private static final String FOLLOWING_ARG = "following";
    private static final String FOLLOWERS_ARG = "followers";
    @BindView(R.id.image_view_user_cover_photo) ImageView userCoverPhoto;
    @BindView(R.id.image_view_user_profile_photo) ImageView userProfilePhoto;
    @BindView(R.id.text_view_user_name) TextView userNameTextView;
    @BindView(R.id.text_view_user_screen_name) TextView userScreenNameTextView;
    @BindView(R.id.text_view_user_description) TextView descriptionTextView;
    @BindView(R.id.text_view_following_count) TextView followingCountTextView;
    @BindView(R.id.text_view_followers_count) TextView followersCountTextView;
    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.sliding_tabs) TabLayout tabLayout;

    @BindString(R.string.load_tweets_error) String loadTweetsErrorString;
    @BindString(R.string.retry) String retryString;

    TwitterClient client;
    User user;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        client = new TwitterClient(this);

        if (savedInstanceState != null) {
            user = Parcels.unwrap(savedInstanceState.getParcelable(USER_KEY));
            loadProfile();
        } else {
            userId = getIntent().getStringExtra(USER_ID_KEY);
            loadUser(userId);
        }
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
//        showProgressBar();
        return super.onCreateView(parent, name, context, attrs);
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(USER_KEY, Parcels.wrap(user));
        super.onSaveInstanceState(outState);
    }

    private void setupViewPager(ViewPager viewPager, User user) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ProfileTweetListFragment.newInstance(user, TIMELINE_KEY), "Timeline");
        adapter.addFragment(ProfileTweetListFragment.newInstance(user, FAVORITES_KEY), "Favorites");
        viewPager.setAdapter(adapter);
    }

    private void loadProfile() {
        Glide.with(this).load(user.getCoverPhotoUrl().replace("_normal", "")).centerCrop().into(userCoverPhoto);
        Glide.with(this).load(user.getProfilePhotoUrl().replace("_normal", "")).centerCrop()
                .bitmapTransform(new RoundedCornersTransformation(this, 3, 3))
                .into(userProfilePhoto);
        userNameTextView.setText(user.getName());
        String screenName = "@" + user.getScreenName();
        userScreenNameTextView.setText(screenName);
        if (user.getDescription() != null && !user.getDescription().equals("")) {
            descriptionTextView.setText(user.getDescription());
            descriptionTextView.setVisibility(View.VISIBLE);
        } else {
            descriptionTextView.setVisibility(View.GONE);
        }

        followersCountTextView.setText(String.valueOf(user.getFollowersCount()));
        followingCountTextView.setText(String.valueOf(user.getFollowingCount()));
    }

    private void loadUser(String userId) {
        client.getUser(userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new GsonBuilder().create();
                user = gson.fromJson(response.toString(), User.class);
                loadProfile();
                setupViewPager(viewPager, user);
                tabLayout.setupWithViewPager(viewPager);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

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
                    .make(findViewById(R.id.relative_layout_profile),
                            loadTweetsErrorString,
                            Snackbar.LENGTH_INDEFINITE)
                    .setAction(retryString, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadUser(userId);
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
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @OnClick(R.id.text_view_profile_followers_label)
    public void loadFollowersDialog() {
        android.app.FragmentManager fm = getFragmentManager();
        FollowFragment followFragment = FollowFragment.newInstance(userId, FOLLOWERS_ARG);
        followFragment.show(fm, "fragment_follow");
    }

    @OnClick(R.id.text_view_profile_following_label)
    public void loadFollowingDialog() {
        android.app.FragmentManager fm = getFragmentManager();
        FollowFragment followFragment = FollowFragment.newInstance(userId, FOLLOWING_ARG);
        followFragment.show(fm, "fragment_follow");
    }

    /**
     * This method customizes the icon of the submit button
     *
     * @param searchView
     */
    private void customizeSubmitButton(SearchView searchView) {
        // Set the submit button to be a custom button
        try {
            Field searchField = SearchView.class.getDeclaredField("mGoButton");
            searchField.setAccessible(true);
            ImageView submitButton = (ImageView) searchField.get(searchView);
            submitButton.setImageResource(R.drawable.ic_submit);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method customized the X button to load fresh articles with no queries
     *
     * @param searchView
     */
    private void customizeCloseButton(final SearchView searchView) {
        Field searchField = null;
        try {
            searchField = SearchView.class.getDeclaredField("mCloseButton");
            searchField.setAccessible(true);
            ImageView closeButton = (ImageView) searchField.get(searchView);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                queryParamsHashMap.remove(QUERY_KEY);
//                loadArticles(0);
                    searchView.setQuery("", false);
                    searchView.clearFocus();
                }
            });
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method sets a listener for when submiting a text query or changing text
     * in the search field //TODO: add suggestions if have time
     *
     * @param searchView
     */
    private void setSearchViewListener(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                queryParamsHashMap.put(QUERY_KEY, query);
//                loadArticles(0);
                // Must return true if we want to consume the event!
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (StringUtils.isEmpty(newText)) {
//                    queryParamsHashMap.remove(QUERY_KEY);
//                    loadArticles(0);
                }
                return true;
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

    private void postRetweeted(String statusId) {
        client.postRetweet(statusId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(getBaseContext(), "Retweeted Tweet!", Toast.LENGTH_SHORT).show();
                ViewPagerAdapter vpa = (ViewPagerAdapter) viewPager.getAdapter();
                BaseTweetListFragment tweetsListFragment = (BaseTweetListFragment) vpa.getItem(viewPager.getCurrentItem());
                Gson gson = new GsonBuilder().create();
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                tweetsListFragment.onRetweetSuccess(tweet.getIdStr());
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

    private void postUnFavorite(String statusId) {
        client.postunFavorite(statusId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(getBaseContext(), "Unfavorited Tweet!", Toast.LENGTH_SHORT).show();
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

    private void postFavorite(String statusId) {
        client.postFavorite(statusId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(getBaseContext(), "Favorited Tweet!", Toast.LENGTH_SHORT).show();
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
}
