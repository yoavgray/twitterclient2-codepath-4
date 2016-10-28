package com.yoav.twitterclient.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.yoav.twitterclient.R;
import com.yoav.twitterclient.models.Tweet;
import com.yoav.twitterclient.models.User;
import com.yoav.twitterclient.viewholders.ImageTweetViewHolder;
import com.yoav.twitterclient.viewholders.RetweetViewHolder;
import com.yoav.twitterclient.viewholders.TextTweetViewHolder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class TweetsAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final static int HOUR = 60;
    public final static int DAY = HOUR * 24;
    public final static int MONTH = DAY * 30;

    private final int TEXT = 0, IMAGE = 1, RETWEET = 2;

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
        // Decide what kind of TYPE should i return
        return IMAGE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case RETWEET:
                View v1 = inflater.inflate(R.layout.retweet_feed_item, viewGroup, false);
                viewHolder = new RetweetViewHolder(v1);
                break;
            case IMAGE:
                View v2 = inflater.inflate(R.layout.image_tweet_feed_item, viewGroup, false);
                viewHolder = new ImageTweetViewHolder(v2);
                break;
            case TEXT:
                View v3 = inflater.inflate(R.layout.text_tweet_feed_item, viewGroup, false);
                viewHolder = new TextTweetViewHolder(v3);
                break;
            default:
                View v4 = inflater.inflate(R.layout.text_tweet_feed_item, viewGroup, false);
                viewHolder = new TextTweetViewHolder(v4);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case IMAGE:
                ImageTweetViewHolder imageViewHolder = (ImageTweetViewHolder) viewHolder;
                configureImageTweetViewHolder(imageViewHolder, position);
                break;
            case TEXT:
                TextTweetViewHolder textViewHolder = (TextTweetViewHolder) viewHolder;
                configureTextTweetViewHolder(textViewHolder, position);
                break;
        }
    }

    private void configureImageTweetViewHolder(ImageTweetViewHolder holder, int position) {
        final Tweet tweet = tweets.get(position);
        final User user = tweet.getUser();

        if (tweet != null) {
            Glide.with(getContext()).load(user.getProfileImageUrl()).into(holder.getProfileImageView());
            holder.getUserNameTextView().setText(user.getName());
            holder.getUserNicknameTextView().setText(user.getNickname());
            holder.getWhenPublishedTextView().setText(getPublishTimeOffset(tweet));
            holder.getTweetBodyTextView().setText(tweet.getText());
        }
    }

    private String getPublishTimeOffset(Tweet tweet) {
        DateFormat originalFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = originalFormat.parse(tweet.getCreatedAt());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date now = new Date();
        long timeOffsetInMinutes = (now.getTime() - date.getTime()) / 1000 / 60;
        if (timeOffsetInMinutes > MONTH) {
            return "" + (timeOffsetInMinutes % MONTH) + "M";
        } else if (timeOffsetInMinutes > DAY) {
            return "" + (timeOffsetInMinutes % DAY) + "d";
        } else if (timeOffsetInMinutes > HOUR) {
            return "" + (timeOffsetInMinutes % HOUR) + "h";
        } else {
            return "" + timeOffsetInMinutes + "m";
        }
    }

    private void configureTextTweetViewHolder(TextTweetViewHolder holder, int position) {
        final Tweet tweet = tweets.get(position);

        if (tweet != null) {

        }
    }

}
