package com.yoav.twitterclient.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yoav.twitterclient.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ComposeTweetFragment extends DialogFragment {
    @BindView(R.id.edit_text_tweet) EditText tweetEditText;
    @BindView(R.id.button_send_tweet) Button sendTweetButton;
    @BindView(R.id.text_view_character_counter) TextView counterTextView;

    int counter = 140;

    private TweetComposedListener selectionListener;

    public ComposeTweetFragment() {
        // Required empty public constructor
    }

    public static ComposeTweetFragment newInstance() {
        ComposeTweetFragment fragment = new ComposeTweetFragment();
//        Bundle args = new Bundle();
//        args.putString(BEGIN_DATE, beginDate);
//        args.putString(END_DATE, endDate);
//        args.putInt(SORT_ID, sortId);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick(R.id.button_send_tweet)
    public void sendTweet() {
        if (selectionListener != null) {
            selectionListener.onTweetComposed(tweetEditText.getText().toString());
        }
        dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose_tweet, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        beginDate    = getArguments().getString(BEGIN_DATE);
//        endDate      = getArguments().getString(END_DATE);
//        sortId       = getArguments().getInt(SORT_ID);

//        beginDateTextView.setText(getNicerDateFormat(beginDate));
//        endDateTextView.setText(getNicerDateFormat(endDate));
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof TweetComposedListener) {
            selectionListener = (TweetComposedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        selectionListener = null;
    }

    public interface TweetComposedListener {
        void onTweetComposed(String tweet);
    }


}
