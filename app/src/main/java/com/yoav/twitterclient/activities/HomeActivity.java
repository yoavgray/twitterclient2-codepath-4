package com.yoav.twitterclient.activities;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yoav.twitterclient.R;
import com.yoav.twitterclient.TwitterApplication;
import com.yoav.twitterclient.TwitterClient;
import com.yoav.twitterclient.adapters.ViewPagerAdapter;
import com.yoav.twitterclient.fragments.TweetsListFragment;
import com.yoav.twitterclient.models.CurrentUser;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.lang.reflect.Field;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class HomeActivity extends AppCompatActivity implements TweetsListFragment.OnFragmentInteractionListener {
    private static final String USER_ID_KEY = "userId";
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.sliding_tabs) TabLayout tabLayout;
    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.fab_compose_tweet) FloatingActionButton composeFab;

    @BindString(R.string.load_tweets_error) String loadTweetsErrorString;
    @BindString(R.string.retry) String retryString;
    @BindString(R.string.cant_compose) String cantComposeString;

    TwitterClient client;
    CurrentUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        client = TwitterApplication.getRestClient();
        loadCurrentUserDetails();
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        setSupportActionBar(toolbar);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TweetsListFragment(), "Timeline");
        adapter.addFragment(new TweetsListFragment(), "Mentions");
        adapter.addFragment(new TweetsListFragment(), "Suggestions");
        viewPager.setAdapter(adapter);
    }

    /**
     * This method sets a listener for when submiting a text query or changing text
     * in the search field //TODO: add suggestions if have time
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

    /**
     * This method customizes the icon of the submit button
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

    private void loadCurrentUserDetails() {
        client.getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new GsonBuilder().create();
                currentUser = gson.fromJson(response.toString(), CurrentUser.class);
                getSupportActionBar().setDisplayUseLogoEnabled(true);
                toolbar.setTitle("@" + currentUser.getScreenName());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);

        customizeSubmitButton(searchView);
        customizeCloseButton(searchView);
        setSearchViewListener(searchView);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra(USER_ID_KEY, currentUser.getIdStr());
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
