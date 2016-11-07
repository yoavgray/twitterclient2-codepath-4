package com.yoav.twitterclient.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.yoav.twitterclient.R;
import com.yoav.twitterclient.models.User;
import com.yoav.twitterclient.viewholders.UserViewHolder;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class UsersAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Store a member variable for the contacts
    private List<User> users;
    private Context context;

    // Pass in the contact array into the constructor
    public UsersAdapter(Context context, List<User> users) {
        this.users = users;
        this.context = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return context;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View view = inflater.inflate(R.layout.follow_list_item, viewGroup, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        UserViewHolder userViewHolder = (UserViewHolder) holder;
        configureUserViewHolder(userViewHolder, position);
    }

    private void configureUserViewHolder(UserViewHolder holder, int position) {
        User user = users.get(position);
        Glide.with(getContext()).load(user.getProfilePhotoUrl().replace("_normal", ""))
                .bitmapTransform(new RoundedCornersTransformation(getContext(), 10, 10))
                .fitCenter()
                .into(holder.getProfileImageView());
        holder.getUserName().setText(user.getName());
        holder.getUserScreenName().setText(user.getScreenName());
    }
}
