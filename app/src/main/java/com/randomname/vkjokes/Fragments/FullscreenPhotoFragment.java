package com.randomname.vkjokes.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.randomname.vkjokes.R;
import com.randomname.vkjokes.Views.TouchImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FullscreenPhotoFragment extends Fragment {
    public final static String PHOTOS_ARRAY_KEY = "photos_array_key";
    public final static String POSITION_KEY = "position_key";

    private ArrayList<String> wallPhotos;
    private int position;

    @Bind(R.id.full_screen_image_view)
    TouchImageView imageView;

    public FullscreenPhotoFragment() {
    }

    public static final FullscreenPhotoFragment getInstance(Bundle data) {
        FullscreenPhotoFragment fragment = new FullscreenPhotoFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wallPhotos = getArguments().getStringArrayList(PHOTOS_ARRAY_KEY);
        position = getArguments().getInt(POSITION_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.full_screen_photo_fragment, container, false);
        ButterKnife.bind(this, view);

        Picasso.with(getActivity()).load(wallPhotos.get(position)).into(imageView);
        return view;
    }
}
