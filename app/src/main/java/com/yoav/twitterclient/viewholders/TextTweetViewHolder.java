package com.yoav.twitterclient.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

public class TextTweetViewHolder extends RecyclerView.ViewHolder {
    // We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    public TextTweetViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}