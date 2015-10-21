package com.randomname.vkjokes.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.Touch;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.randomname.vkjokes.R;
import com.randomname.vkjokes.Views.TouchImageView;
import com.squareup.picasso.Picasso;

import java.util.Stack;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FullscreenPhotoFragment extends Fragment {

    public final static String IMAGE_URL_KEY = "imageUrlKey";

    private String url = "";

    @Bind(R.id.full_screen_photo)
    TouchImageView imageView;

    public FullscreenPhotoFragment() {
    }

    public static FullscreenPhotoFragment getInstance(Bundle data) {
        FullscreenPhotoFragment fragment = new FullscreenPhotoFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        url = getArguments().getString(IMAGE_URL_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.full_screen_photo_fragment, container, false);
        ButterKnife.bind(this, view);

        Picasso.with(getActivity()).load(url).into(imageView);
        return view;
    }
}
