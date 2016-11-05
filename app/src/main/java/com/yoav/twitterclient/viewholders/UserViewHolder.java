package com.yoav.twitterclient.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yoav.twitterclient.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.text_view_user_name) TextView userName;
    @BindView(R.id.text_view_user_screen_name) TextView userScreenName;
    @BindView(R.id.image_view_profile_photo) ImageView profileImageView;


    public UserViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public TextView getUserName() {
        return userName;
    }

    public TextView getUserScreenName() {
        return userScreenName;
    }

    public ImageView getProfileImageView() {
        return profileImageView;
    }
}
