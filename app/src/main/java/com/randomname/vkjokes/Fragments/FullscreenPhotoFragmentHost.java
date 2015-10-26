package com.randomname.vkjokes.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.randomname.vkjokes.Adapters.PhotosAdapter;
import com.randomname.vkjokes.Interfaces.FragmentsCallbacks;
import com.randomname.vkjokes.MainActivity;
import com.randomname.vkjokes.R;
import com.randomname.vkjokes.Views.TouchImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;
import butterknife.OnTouch;

public class FullscreenPhotoFragmentHost extends Fragment {
    public final static String PHOTOS_ARRAY_KEY = "photos_array_key";
    public final static String POSITION_KEY = "position_key";

    private FragmentsCallbacks publicListFragmentCallback;

    private ArrayList<String> wallPhotos;
    private int position;

    private PhotosAdapter adapter;

    private Bitmap b = null;

    @Bind(R.id.viewPager)
    ViewPager viewPager;

    @Bind(R.id.main_layout)
    RelativeLayout mainLayout;

    @Bind(R.id.dummy_background_image)
    ImageView dummyBackground;

    public FullscreenPhotoFragmentHost() {
    }

    public static FullscreenPhotoFragmentHost getInstance(Bundle data) {
        FullscreenPhotoFragmentHost fragment = new FullscreenPhotoFragmentHost();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity a;

        if (context instanceof Activity){
            a = (Activity) context;

            try {
                publicListFragmentCallback = (FragmentsCallbacks) a;
            } catch (ClassCastException e) {
                throw new ClassCastException(a.toString() + " must implement MainFragmentCallbacks");
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wallPhotos = getArguments().getStringArrayList(PHOTOS_ARRAY_KEY);

        if (savedInstanceState == null) {
            position = getArguments().getInt(POSITION_KEY) + 1;
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

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                viewPager.getParent().requestDisallowInterceptTouchEvent(true);
                if (position == 0) {
                    try {
                        publicListFragmentCallback.onPhotoFragmentPageSlide(positionOffset);
                        mainLayout.setAlpha(positionOffset);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        publicListFragmentCallback.onPhotoPageStop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onPageSelected(int pos) {
                if (pos == 0) {
                    return;
                }
                position = pos;
                setNewTitle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(POSITION_KEY, viewPager.getCurrentItem());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        b = loadBitmapFromView(getView());
        super.onPause();
    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(),
                v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getWidth(),
                v.getHeight());
        v.draw(c);
        return b;
    }

    @Override
    public void onDestroyView() {
        dummyBackground.setImageBitmap(b);
        b = null;
        super.onDestroyView();
    }

    private void setNewTitle() {
        if (wallPhotos.size() > 1) {
            try {
                MainActivity activity = (MainActivity) getActivity();
                activity.setNewToolbarTitle(position + " из " + wallPhotos.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
