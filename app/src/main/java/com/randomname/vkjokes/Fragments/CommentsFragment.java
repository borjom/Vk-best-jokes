package com.randomname.vkjokes.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.randomname.vkjokes.Models.WallPostModel;
import com.randomname.vkjokes.R;

import butterknife.ButterKnife;

public class CommentsFragment extends Fragment {

    public final static String WALL_POST_MODEL_KEY = "wall_post_model_key";

    WallPostModel wallPostModel;

    public CommentsFragment() {
    }

    public static CommentsFragment getInstance(Bundle data) {
        CommentsFragment fragment = new CommentsFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wallPostModel = getArguments().getParcelable(WALL_POST_MODEL_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comments_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
