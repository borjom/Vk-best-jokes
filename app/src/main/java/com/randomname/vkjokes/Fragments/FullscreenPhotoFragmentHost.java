package com.randomname.vkjokes.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import com.randomname.vkjokes.Adapters.PhotosAdapter;
import com.randomname.vkjokes.R;
import com.randomname.vkjokes.Views.TouchImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnPageChange;

public class FullscreenPhotoFragmentHost extends Fragment {
    public final static String PHOTOS_ARRAY_KEY = "photos_array_key";
    public final static String POSITION_KEY = "position_key";

    private ArrayList<String> wallPhotos;
    private int position;

    private PhotosAdapter adapter;

    @Bind(R.id.viewPager)
    ViewPager viewPager;

    public FullscreenPhotoFragmentHost() {
    }

    public static FullscreenPhotoFragmentHost getInstance(Bundle data) {
        FullscreenPhotoFragmentHost fragment = new FullscreenPhotoFragmentHost();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wallPhotos = getArguments().getStringArrayList(PHOTOS_ARRAY_KEY);

        if (savedInstanceState == null) {
            position = getArguments().getInt(POSITION_KEY);
        } else {
            position = savedInstanceState.getInt(POSITION_KEY);
        }

        setNewTitle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.full_screen_photo_fragment_host, container, false);
        ButterKnife.bind(this, view);

        adapter = new PhotosAdapter(getChildFragmentManager(), wallPhotos);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(POSITION_KEY, viewPager.getCurrentItem());

        super.onSaveInstanceState(outState);
    }

    @OnPageChange(R.id.viewPager)
    public void onPageChanged() {
        position = viewPager.getCurrentItem();
        setNewTitle();
    }

    private void setNewTitle() {
        if (wallPhotos.size() > 1) {
            try {
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.getSupportActionBar().setTitle((position + 1) + " из " + wallPhotos.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
