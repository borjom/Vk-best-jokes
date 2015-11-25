package com.randomname.vkjokes.Fragments;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.randomname.vkjokes.Adapters.PhotosAdapter;
import com.randomname.vkjokes.Interfaces.FragmentsCallbacks;
import com.randomname.vkjokes.MainActivity;
import com.randomname.vkjokes.R;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

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
        setHasOptionsMenu(true);
        wallPhotos = getArguments().getStringArrayList(PHOTOS_ARRAY_KEY);

        if (savedInstanceState == null) {
            position = getArguments().getInt(POSITION_KEY) + 1;
        } else {
            position = savedInstanceState.getInt(POSITION_KEY);
        }

        setNewTitle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fullscreen_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                savePhotoToDisc();
                return false;
            default:
                break;
        }

        return false;
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

    private void savePhotoToDisc() {
        String url = wallPhotos.get(viewPager.getCurrentItem() - 1);
        String title = System.currentTimeMillis() + ".jpg";

        File direct = new File(Environment.DIRECTORY_PICTURES
                + "/vk_jokes");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        DownloadManager mgr = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Vk jokes")
                .setDescription("Сохранение картинки")
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES + "/vk_jokes", title);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        mgr.enqueue(request);
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
