package com.randomname.vkjokes.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.randomname.vkjokes.R;

import butterknife.ButterKnife;

public class CommentsFragment extends Fragment {

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comments_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
