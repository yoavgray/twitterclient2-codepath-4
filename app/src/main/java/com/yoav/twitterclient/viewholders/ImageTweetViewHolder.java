package com.yoav.twitterclient.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yoav.twitterclient.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ImageTweetViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.image_view_profile_photo) ImageView profileImageView;
    @BindView(R.id.text_view_user_name) TextView userNameTextView;
    @BindView(R.id.text_view_user_nickname) TextView userNicknameTextView;
    @BindView(R.id.text_view_when_published) TextView whenPublishedTextView;
    @BindView(R.id.text_view_tweet_body) TextView tweetBodyTextView;

    // We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    public ImageTweetViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
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


}