package com.yoav.twitterclient.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yoav.twitterclient.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ComposeTweetFragment extends DialogFragment {
    public static final String PREFS_NAME = "PrefsFile";
    private static final String DRAFT_KEY = "draft";
    private static final int TWEET_CHAR_SIZE = 140;
    SharedPreferences sharedPreferences;

    @BindView(R.id.edit_text_tweet) EditText tweetEditText;
    @BindView(R.id.button_send_tweet) Button sendTweetButton;
    @BindView(R.id.text_view_character_counter) TextView counterTextView;

    String draft = "";

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
        sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, 0);
        if (savedInstanceState != null) {
            draft = savedInstanceState.getString(DRAFT_KEY);
        } else {
            draft = sharedPreferences.getString(DRAFT_KEY, "");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(DRAFT_KEY, tweetEditText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.button_send_tweet)
    public void sendTweet() {
        if (selectionListener != null) {
            selectionListener.onTweetComposed(tweetEditText.getText().toString());
            tweetEditText.setText("");
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
        tweetEditText.setText(draft);
        tweetEditText.setSelection(draft.length());
        counterTextView.setText(String.valueOf(TWEET_CHAR_SIZE - draft.length()));


        tweetEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                counterTextView.setText(String.valueOf(TWEET_CHAR_SIZE - s.length()));
                if (s.length() == TWEET_CHAR_SIZE) {
                    Toast.makeText(getActivity(), getString(R.string.no_more_tweet), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
        sharedPreferences.edit().putString(DRAFT_KEY,tweetEditText.getText().toString()).apply();
        selectionListener = null;
    }

    public interface TweetComposedListener {
        void onTweetComposed(String tweet);
    }


}
