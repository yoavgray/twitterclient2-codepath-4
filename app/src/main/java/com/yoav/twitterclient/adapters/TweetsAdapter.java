package com.yoav.twitterclient.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.yoav.twitterclient.R;
import com.yoav.twitterclient.fragments.ComposeTweetFragment;
import com.yoav.twitterclient.models.ExtendedEntities;
import com.yoav.twitterclient.models.Media;
import com.yoav.twitterclient.models.Tweet;
import com.yoav.twitterclient.models.Url;
import com.yoav.twitterclient.models.User;
import com.yoav.twitterclient.viewholders.TweetViewHolder;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class TweetsAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final static int HOUR = 60;
    public final static int DAY = HOUR * 24;
    public final static int MONTH = DAY * 30;

    private final int VIDEO = 0, REGULAR = 1;

    // Store a member variable for the contacts
    private List<Tweet> tweets;
    private Context context;

    // Pass in the contact array into the constructor
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.tweets = tweets;
        this.context = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return context;
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    @Override
    public int getItemViewType(int position) {
        return REGULAR;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case REGULAR:
                View v1 = inflater.inflate(R.layout.regular_tweet_feed_item, viewGroup, false);
                viewHolder = new TweetViewHolder(v1);
                break;
            default:
                View v2 = inflater.inflate(R.layout.regular_tweet_feed_item, viewGroup, false);
                viewHolder = new TweetViewHolder(v2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case REGULAR:
                TweetViewHolder imageViewHolder = (TweetViewHolder) viewHolder;
                configureImageTweetViewHolder(imageViewHolder, position);
                break;
        }
    }

    private void configureImageTweetViewHolder(TweetViewHolder holder, int position) {
        final Tweet tweet = tweets.get(position);
        displayTweetEssentials(holder, tweet);

    }

    private void displayTweetEssentials(TweetViewHolder holder, final Tweet tweet) {
        final User user = tweet.getUser();

        Glide.with(getContext()).load(user.getProfilePhotoUrl().replace("_normal", ""))
                .bitmapTransform(new RoundedCornersTransformation(getContext(), 10, 10))
                .fitCenter()
                .into(holder.getProfileImageView());
        holder.getUserNameTextView().setText(user.getName());
        String nickname = "@" + user.getScreenName();
        holder.getUserNicknameTextView().setText(nickname);
        holder.getWhenPublishedTextView().setText(tweet.getRelativeTimeAgo(tweet.getCreatedAt()));
        String tweetBody = tweet.getText();

        //Try removing a Twitter URL if present, though this may be null even if there's a URL
        List<Url> urls = tweet.getEntities().getUrls();
        for (int i = 0; urls != null && i < urls.size(); i++) {
            String url = urls.get(i).getUrl();
            if (url.contains("/t.co") && tweetBody.contains(url)) {
                tweetBody = tweetBody.replace(url,urls.get(i).getExpandedUrl());
            }
        }
        holder.getEmbeddedImageView().setVisibility(View.GONE);
        ExtendedEntities extendedEntities = tweet.getExtendedEntities();
        for (int i = 0; extendedEntities != null && i < extendedEntities.getMedia().size(); i++) {
            Media thisMedia = extendedEntities.getMedia().get(i);
            if (i == 0) {
                String imageUrl = thisMedia.getMediaUrlHttps();
                Glide.with(getContext()).load(imageUrl).centerCrop()
                        .bitmapTransform(new RoundedCornersTransformation(getContext(), 10, 10))
                        .into(holder.getEmbeddedImageView());
                holder.getEmbeddedImageView().setVisibility(View.VISIBLE);
            }
            tweetBody = tweetBody.replace(thisMedia.getUrl(),thisMedia.getDisplayUrl());
        }

        holder.getTweetBodyTextView().setText(tweetBody);
        holder.getFavoritesCountTextView().setText(String.valueOf(tweet.getFavoriteCount()));
        holder.getRetweetsCountTextView().setText(String.valueOf(tweet.getRetweetCount()));

        holder.getRespondImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = ((Activity) context).getFragmentManager();
                ComposeTweetFragment composeTweetFragment = ComposeTweetFragment.newInstance(tweet.getUser().getScreenName());
                composeTweetFragment.show(fm, "fragment_compose");
            }
        });
    }


}
