package com.yoav.twitterclient.activities;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yoav.twitterclient.R;
import com.yoav.twitterclient.TwitterApplication;
import com.yoav.twitterclient.TwitterClient;
import com.yoav.twitterclient.adapters.TweetsAdapter;
import com.yoav.twitterclient.fragments.TweetsListFragment;
import com.yoav.twitterclient.models.CurrentUser;
import com.yoav.twitterclient.models.Tweet;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements TweetsListFragment.OnFragmentInteractionListener {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab_compose_tweet) FloatingActionButton composeFab;
    @BindView(R.id.image_view_user_profile_image) ImageView userProfileImage;

    @BindString(R.string.load_tweets_error) String loadTweetsErrorString;
    @BindString(R.string.retry) String retryString;
    @BindString(R.string.cant_compose) String cantComposeString;

    CurrentUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        Fragment activeFragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);

        if (activeFragment == null) {
            activeFragment = TweetsListFragment.newInstance("", "");

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, activeFragment, activeFragment.getClass().getSimpleName())
                    .commit();
        }

        setSupportActionBar(toolbar);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
