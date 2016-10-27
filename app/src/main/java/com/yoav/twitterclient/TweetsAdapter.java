package com.yoav.twitterclient;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.yoav.twitterclient.models.Tweet;
import com.yoav.twitterclient.models.User;
import com.yoav.twitterclient.viewholders.ImageTweetViewHolder;
import com.yoav.twitterclient.viewholders.RetweetViewHolder;
import com.yoav.twitterclient.viewholders.TextTweetViewHolder;

import java.util.List;


public class TweetsAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final static String NYTIMES_URL = "http://www.nytimes.com/";

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
            Picasso.with(getContext()).load(user.getProfileImageUrl()).
                    noFade().fit().into(holder.getProfileImageView());
            holder.getUserNameTextView().setText(user.getName());
            holder.getUserNicknameTextView().setText(user.getNickname());
            holder.getWhenPublishedTextView().setText("1h");
            holder.getWhenPublishedTextView().setText(tweet.getCreatedAt());
            holder.getTweetBodyTextView().setText(tweet.getText());
        }
    }
    private void configureTextTweetViewHolder(TextTweetViewHolder holder, int position) {
        final Tweet tweet = tweets.get(position);

        if (tweet != null) {

        }
    }

}
