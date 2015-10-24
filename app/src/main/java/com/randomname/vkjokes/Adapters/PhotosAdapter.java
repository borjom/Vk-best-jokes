package com.randomname.vkjokes.Adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.randomname.vkjokes.Fragments.FullscreenPhotoFragment;

import java.util.ArrayList;

public class PhotosAdapter extends FragmentPagerAdapter {
    private ArrayList<String> wallPhotos;

    public PhotosAdapter(FragmentManager fragmentManager, ArrayList<String> wallPhotos) {
        super(fragmentManager);
        this.wallPhotos = wallPhotos;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return wallPhotos.size() + 1;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        Bundle data = new Bundle();
        Fragment fragment;
        if (position == 0) {
            fragment = new Fragment();
        } else {
            data.putString(FullscreenPhotoFragment.IMAGE_URL_KEY, wallPhotos.get(position - 1));

            fragment = FullscreenPhotoFragment.getInstance(data);
        }

        return fragment;
    }

}
