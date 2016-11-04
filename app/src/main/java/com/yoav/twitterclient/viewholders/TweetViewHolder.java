package com.yoav.twitterclient.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yoav.twitterclient.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetViewHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.text_view_retweeted_status_label) TextView retweetedTextView;
    @BindView(R.id.image_view_profile_photo) ImageView profileImageView;
    @BindView(R.id.relative_layout_tweet_item) RelativeLayout tweetItemLayout;
    @BindView(R.id.text_view_user_name) TextView userNameTextView;
    @BindView(R.id.text_view_user_nickname) TextView userNicknameTextView;
    @BindView(R.id.text_view_when_published) TextView whenPublishedTextView;
    @BindView(R.id.textview_retweets_count) TextView retweetsCountTextView;
    @BindView(R.id.textview_favorites_count) TextView favoritesCountTextView;
    @BindView(R.id.text_view_tweet_body) TextView tweetBodyTextView;
    @BindView(R.id.respond_button) ImageView respondImageView;
    @BindView(R.id.image_view_tweet_embedded_image) ImageView embeddedImageView;

    // We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    public TweetViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public TextView getRetweetedTextView() {
        return retweetedTextView;
    }

    public ImageView getProfileImageView() {
        return profileImageView;
    }

    public TextView getUserNameTextView() {
        return userNameTextView;
    }

    public TextView getUserNicknameTextView() {
        return userNicknameTextView;
    }

    public TextView getWhenPublishedTextView() {
        return whenPublishedTextView;
    }

    public TextView getTweetBodyTextView() {
        return tweetBodyTextView;
    }

    public ImageView getEmbeddedImageView() {
        return embeddedImageView;
    }

    public TextView getRetweetsCountTextView() {
        return retweetsCountTextView;
    }

    public TextView getFavoritesCountTextView() {
        return favoritesCountTextView;
    }

    public ImageView getRespondImageView() {
        return respondImageView;
    }

    public RelativeLayout getTweetItemLayout() {
        return tweetItemLayout;
    }
}
