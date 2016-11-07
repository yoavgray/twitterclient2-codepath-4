package com.yoav.twitterclient.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.yoav.twitterclient.R;
import com.yoav.twitterclient.TwitterApplication;
import com.yoav.twitterclient.activities.ProfileActivity;
import com.yoav.twitterclient.fragments.ComposeTweetFragment;
import com.yoav.twitterclient.fragments.TweetDetailsFragment;
import com.yoav.twitterclient.models.CurrentUser;
import com.yoav.twitterclient.models.ExtendedEntities;
import com.yoav.twitterclient.models.Media;
import com.yoav.twitterclient.models.Tweet;
import com.yoav.twitterclient.models.Url;
import com.yoav.twitterclient.models.User;
import com.yoav.twitterclient.utils.PatternEditableBuilder;
import com.yoav.twitterclient.viewholders.TweetViewHolder;

import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.regex.Pattern;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class TweetsAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final static int HOUR = 60;
    public final static int DAY = HOUR * 24;
    public final static int MONTH = DAY * 30;

    private final int VIDEO = 0, REGULAR = 1;

    // Store a member variable for the contacts
    private CurrentUser currentUser;
    private List<Tweet> tweets;
    private Context context;
    private OnTweetChangedListener listener;

    // Pass in the contact array into the constructor
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.tweets = tweets;
        this.context = context;
    }

    public void setCurrentUser(CurrentUser currentUser) {
        this.currentUser = currentUser;
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
                TweetViewHolder tweetViewHolder = (TweetViewHolder) viewHolder;
                configureTweetViewHolder(tweetViewHolder, position);
                break;
        }
    }



    private void configureTweetViewHolder(TweetViewHolder holder, int position) {
        if (context instanceof OnTweetChangedListener) {
            listener = (OnTweetChangedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTweetChangedListener");
        }

        Tweet tweet = tweets.get(position);
        displayTweetEssentials(holder, tweet);
    }

    private void displayTweetEssentials(TweetViewHolder holder, Tweet tweet) {
        String retweeter;
        if (tweet.getRetweeted_status() != null) {
            retweeter = tweet.getUser().getName();
            tweet = tweet.getRetweeted_status();
            holder.getRetweetedTextView().setVisibility(View.VISIBLE);
            String toShow = retweeter + " Retweeted";
            holder.getRetweetedTextView().setText(toShow);
        } else {
            holder.getRetweetedTextView().setVisibility(View.GONE);
        }

        final Tweet finalTweet = tweet;
        final User user = finalTweet.getUser();

        Glide.with(getContext()).load(user.getProfilePhotoUrl().replace("_normal", ""))
                .bitmapTransform(new RoundedCornersTransformation(getContext(), 10, 10))
                .fitCenter()
                .into(holder.getProfileImageView());
        holder.getProfileImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra(TwitterApplication.USER_ID_KEY, user.getIdStr());
                getContext().startActivity(intent);
            }
        });

        holder.getUserNameTextView().setText(user.getName());
        String nickname = "@" + user.getScreenName();
        holder.getUserNicknameTextView().setText(nickname);
        holder.getWhenPublishedTextView().setText(finalTweet.getRelativeTimeAgo(finalTweet.getCreatedAt()));

        String tweetBody = finalTweet.getText();

        //Try removing a Twitter URL if present, though this may be null even if there's a URL
        List<Url> urls = finalTweet.getEntities().getUrls();
        for (int i = 0; urls != null && i < urls.size(); i++) {
            String url = urls.get(i).getUrl();
            if (url.contains("/t.co") && tweetBody.contains(url)) {
                tweetBody = tweetBody.replace(url,urls.get(i).getExpandedUrl());
            }
        }
        holder.getEmbeddedImageView().setVisibility(View.GONE);
        ExtendedEntities extendedEntities = finalTweet.getExtendedEntities();
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
        // Set spannable styles and behavior
        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), R.color.colorAccent,
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Intent i = new Intent(getContext(), ProfileActivity.class);
                                i.putExtra(TwitterApplication.SCREEN_NAME_KEY, text.substring(1));
                                getContext().startActivity(i);
                            }
                        }).
                addPattern(Pattern.compile("\\#(\\w+)"), R.color.colorAccent,
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Toast.makeText(getContext(), "Clicked hashtag: " + text,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).into(holder.getTweetBodyTextView());


        // Check if we got a notification from parent activity that a favorite/retweet event just happened;
        holder.getFavoritesCountTextView().setText(String.valueOf(finalTweet.getFavoriteCount()));
        if (finalTweet.getWasRetweeted()) {
            String newCount = "" + (finalTweet.getRetweetCount() + 1);
            holder.getRetweetsCountTextView().setText(newCount);
            finalTweet.setWasRetweeted(false);
        } else {
            holder.getRetweetsCountTextView().setText(String.valueOf(finalTweet.getRetweetCount()));
        }

        // Take care of the favorite button
        if (BooleanUtils.isTrue(tweet.getFavorited())) {
            holder.getFavoriteButton().setLiked(true);
            if (finalTweet.getWasFavorited()) {
                // Letting the UI know that the number of favorites should be incremented because user favorited
                String newCount = "" + (finalTweet.getFavoriteCount() + 1);
                holder.getFavoritesCountTextView().setText(newCount);
                finalTweet.setWasFavorited(false);
            }
        } else {
            if (finalTweet.getWasUnfavorited()) {
                // Letting the UI know that the number of favorites should be incremented because user favorited
                String newCount = "" + (finalTweet.getFavoriteCount() - 1);
                holder.getFavoritesCountTextView().setText(newCount);
                finalTweet.setWasUnfavorited(false);
            }
            holder.getFavoriteButton().setLiked(false);
        }
        holder.getFavoriteButton().setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                ((OnTweetChangedListener) context).onTweetFavorited(true, finalTweet.getIdStr());
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                ((OnTweetChangedListener) context).onTweetFavorited(false, finalTweet.getIdStr());
            }
        });

        holder.getRespondImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = ((Activity) context).getFragmentManager();
                ComposeTweetFragment composeTweetFragment = ComposeTweetFragment
                        .newInstance(finalTweet.getUser().getScreenName(), finalTweet.getIdStr());
                composeTweetFragment.show(fm, "fragment_compose");
            }
        });
        holder.getRetweetImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnTweetChangedListener) context).onTweetRetweeted(finalTweet.getIdStr());
            }
        });

        holder.getTweetItemLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = ((Activity) context).getFragmentManager();
                TweetDetailsFragment tweetDetailsFragment = TweetDetailsFragment.newInstance(finalTweet);
                tweetDetailsFragment.show(fm, "fragment_details");
            }
        });

        holder.getTweetItemLayout().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (currentUser != null && currentUser.getScreenName().equals(user.getScreenName())) {
                    new AlertDialog.Builder(context, R.style.AlertDialogStyle)
                            .setTitle("Delete Tweet")
                            .setMessage("Are you sure you want to delete this Tweet?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ((OnTweetChangedListener) context).onTweetDeleted(finalTweet.getIdStr());
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                return true;
            }
        });
    }

    public interface OnTweetChangedListener {
        public void onTweetFavorited(boolean isFavorited, String statusId);
        public void onTweetRetweeted(String statusId);
        public void onTweetDeleted(String statusId);
    }


}
