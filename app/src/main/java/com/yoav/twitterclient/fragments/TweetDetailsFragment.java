package com.yoav.twitterclient.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yoav.twitterclient.R;
import com.yoav.twitterclient.models.ExtendedEntities;
import com.yoav.twitterclient.models.Media;
import com.yoav.twitterclient.models.Tweet;
import com.yoav.twitterclient.models.Url;
import com.yoav.twitterclient.models.User;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetailsFragment extends DialogFragment {
    private static final String TWEET_KEY = "tweet";

    @BindView(R.id.image_view_profile_photo) ImageView profileImageView;
    @BindView(R.id.text_view_user_name) TextView userNameTextView;
    @BindView(R.id.text_view_user_nickname) TextView userNicknameTextView;
    @BindView(R.id.text_view_when_published) TextView whenPublishedTextView;
    @BindView(R.id.text_view_tweet_body) TextView tweetBodyTextView;
    @BindView(R.id.image_view_tweet_embedded_image) ImageView embeddedImageView;
    @BindView(R.id.textview_favorites_count) TextView favoritesCountTextView;
    @BindView(R.id.textview_retweets_count) TextView retweetsCountTextView;

    Tweet tweet;

    public TweetDetailsFragment() {
        // Required empty public constructor
    }

    public static TweetDetailsFragment newInstance(Tweet tweet) {
        TweetDetailsFragment fragment = new TweetDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(TWEET_KEY, Parcels.wrap(tweet));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tweet = Parcels.unwrap(getArguments().getParcelable(TWEET_KEY));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet_details, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // edit views
        final User user = tweet.getUser();

        Glide.with(getActivity()).load(user.getProfilePhotoUrl().replace("_normal", ""))
                .bitmapTransform(new RoundedCornersTransformation(getActivity(), 10, 10))
                .fitCenter()
                .into(profileImageView);
        userNameTextView.setText(user.getName());
        String nickname = "@" + user.getScreenName();
        userNicknameTextView.setText(nickname);
        whenPublishedTextView.setText(tweet.getRelativeTimeAgo(tweet.getCreatedAt()));
        String tweetBody = tweet.getText();

        //Try removing a Twitter URL if present, though this may be null even if there's a URL
        List<Url> urls = tweet.getEntities().getUrls();
        for (int i = 0; urls != null && i < urls.size(); i++) {
            String url = urls.get(i).getUrl();
            if (url.contains("/t.co") && tweetBody.contains(url)) {
                tweetBody = tweetBody.replace(url,urls.get(i).getExpandedUrl());
            }
        }
        ExtendedEntities extendedEntities = tweet.getExtendedEntities();
        for (int i = 0; extendedEntities != null && i < extendedEntities.getMedia().size(); i++) {
            Media thisMedia = extendedEntities.getMedia().get(i);
            if (i == 0) {
                String imageUrl = thisMedia.getMediaUrlHttps();
                Glide.with(getActivity()).load(imageUrl).centerCrop()
                        .bitmapTransform(new RoundedCornersTransformation(getActivity(), 10, 10))
                        .into(embeddedImageView);
                embeddedImageView.setVisibility(View.VISIBLE);
            }
            tweetBody = tweetBody.replace(thisMedia.getUrl(),thisMedia.getDisplayUrl());
        }

        tweetBodyTextView.setText(tweetBody);
        retweetsCountTextView.setText(String.valueOf(tweet.getRetweetCount()));
        favoritesCountTextView.setText(String.valueOf(tweet.getFavoriteCount()));
    }

    @OnClick(R.id.respond_button)
    public void respondToTweet() {
        FragmentManager fm = getFragmentManager();
        ComposeTweetFragment composeTweetFragment = ComposeTweetFragment.newInstance(tweet.getUser().getScreenName());
        composeTweetFragment.show(fm, "fragment_compose");
    }
}
