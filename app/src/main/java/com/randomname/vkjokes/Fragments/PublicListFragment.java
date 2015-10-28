package com.randomname.vkjokes.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.randomname.vkjokes.Adapters.WallPostsAdapter;
import com.randomname.vkjokes.Interfaces.FragmentsCallbacks;
import com.randomname.vkjokes.Models.WallPostModel;
import com.randomname.vkjokes.R;
import com.randomname.vkjokes.Util.Constants;
import com.randomname.vkjokes.Util.Misc;
import com.randomname.vkjokes.Util.StringUtils;
import com.randomname.vkjokes.Views.PreCachingLayoutManager;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKCommentArray;
import com.vk.sdk.api.model.VKList;
import com.vk.sdk.api.model.VKPostArray;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublicListFragment extends Fragment {

    final String WALL_POSTS_KEY = "wallPostsKey";
    final String RECYCLER_STATE_KEY = "recyclerStateKey";
    final String REQUEST_OFFSET_KEY = "requestOffsetKey";
    final String CURRENT_PUBLIC_KEY = "currentPublic";

    private FragmentsCallbacks publicListFragmentCallback;
    private WallPostsAdapter adapter;
    private ArrayList<WallPostModel> wallPostModelArrayList;
    private PreCachingLayoutManager preCachingLayoutManager;

    private int offset = 0;
    private boolean loading = false;
    private String currentPublic = "mdk";

    VKRequest request;

    @Bind(R.id.wall_posts_recycler_view)
    RecyclerView wallPostsRecyclerView;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    public PublicListFragment() {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.public_list_fragment, container, false);
        ButterKnife.bind(this, view);

        String[] publicUrls = getResources().getStringArray(R.array.public_url);
        currentPublic = publicUrls[0];

        wallPostModelArrayList = new ArrayList<>();

        SharedPreferences prefs = getActivity().getSharedPreferences(
                Constants.SHARED_PREFERENCES.PREF_NAME, Context.MODE_PRIVATE);

        currentPublic = prefs.getString(Constants.SHARED_PREFERENCES.CURRENT_PUBLIC, "mdk");

        TypedValue tv = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
            refreshLayout.setProgressViewOffset(true, actionBarHeight / 2, actionBarHeight + 50);
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWallPosts(0, false, false);
            }
        });

        preCachingLayoutManager = new PreCachingLayoutManager(getActivity());

        wallPostsRecyclerView.setLayoutManager(preCachingLayoutManager);
        adapter = new WallPostsAdapter(getActivity(), wallPostModelArrayList);
        wallPostsRecyclerView.setAdapter(adapter);

        wallPostsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                publicListFragmentCallback.onPageScroll(dy);
                int visibleItemCount = preCachingLayoutManager.getChildCount();
                int totalItemCount = preCachingLayoutManager.getItemCount();
                int pastVisiblesItems = preCachingLayoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    getWallPosts();
                }
            }
        });

        if (savedInstanceState == null) {
            getWallPosts();
        } else {
            ArrayList<WallPostModel> restoredList = savedInstanceState.getParcelableArrayList(WALL_POSTS_KEY);

            if (restoredList != null) {
                wallPostModelArrayList.addAll(restoredList);
            }

            Parcelable recyclerState = savedInstanceState.getParcelable(RECYCLER_STATE_KEY);
            preCachingLayoutManager.onRestoreInstanceState(recyclerState);
            offset = savedInstanceState.getInt(REQUEST_OFFSET_KEY);
            currentPublic = savedInstanceState.getString(CURRENT_PUBLIC_KEY);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(WALL_POSTS_KEY, wallPostModelArrayList);

        Parcelable mListState = preCachingLayoutManager.onSaveInstanceState();
        outState.putParcelable(RECYCLER_STATE_KEY, mListState);

        outState.putInt(REQUEST_OFFSET_KEY, offset);
        outState.putString(CURRENT_PUBLIC_KEY, currentPublic);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences prefs = getActivity().getSharedPreferences(
                Constants.SHARED_PREFERENCES.PREF_NAME, Context.MODE_PRIVATE);

        prefs.edit().putString(Constants.SHARED_PREFERENCES.CURRENT_PUBLIC, currentPublic).apply();
    }

    private void getWallPosts() {
        startDownloadingPosts(offset, true, true);
    }

    private void getWallPosts(int startOffset, boolean toIncrement, boolean appendFromBottom) {
        startDownloadingPosts(startOffset, toIncrement, appendFromBottom);
    }

    private void startDownloadingPosts(final int startOffset, final boolean toIncrement, final boolean appendFromBottom) {
        if (loading) {
            return;
        }
        loading = true;
        VKParameters params = new VKParameters();
        params.put("domain", currentPublic);
        params.put("count", "50");
        params.put("filter", "owner");
        params.put("offset", startOffset);

        if (request != null) {
            request.cancel();
        }

        request = new VKRequest("wall.get", params);
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                VKPostArray posts = new VKPostArray();

                try {
                    posts.parse(response.json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startConverting(posts, appendFromBottom);

                if (toIncrement) {
                    offset += posts.size();
                }
                loading = false;
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);

                if (getActivity() == null) {
                    return;
                }

                Toast.makeText(getActivity(), "Произошла ошибка, новая попытка подключиться", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                loading = false;
                Log.e("mag", error.toString());
                if (getActivity() == null) {
                    return;
                }

                Toast.makeText(getActivity(), "Произошла ошибка", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void changePublic(String newPublic) {
        int oldSize = wallPostModelArrayList.size();
        wallPostModelArrayList.clear();
        adapter.notifyItemRangeRemoved(0, oldSize);
        offset = 0;
        currentPublic = newPublic;

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        getWallPosts();
                    }
                },
                300);
    }

    public void startConverting(final VKPostArray posts, final boolean appendFromBottom) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                convertVKPostToWallPost(posts, appendFromBottom);
            }
        };
        new Thread(runnable).start();
    }

    private void convertVKPostToWallPost(VKPostArray vkPosts, final boolean appendFromBottom) {
        int size = vkPosts.size();
        final int origSize = wallPostModelArrayList.size();
        ArrayList<WallPostModel> newArray = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            VKApiPost vkApiPost = vkPosts.get(i);
            WallPostModel wallPostModel = new WallPostModel();

            boolean toBreak = setWallModelAttachments(vkApiPost, wallPostModel);

            if (toBreak) {
                continue;
            }

            String endText = vkApiPost.text;
            endText = StringUtils.replaceURLwithAnchor(endText);
            endText = StringUtils.replaceVkLinks(endText);

            wallPostModel.setText(endText);
            wallPostModel.setId(vkApiPost.getId());
            wallPostModel.setCommentsCount(vkApiPost.comments_count);
            wallPostModel.setLikeCount(vkApiPost.likes_count);
            wallPostModel.setAlreadyLiked(vkApiPost.user_likes);
            wallPostModel.setCanPost(vkApiPost.can_post_comment);
            wallPostModel.setFromId(vkApiPost.from_id);

            if (vkApiPost.date > 0) {
                long millisecond = vkApiPost.date * 1000;
                String dateString = StringUtils.getDateString(millisecond);

                wallPostModel.setDate(dateString);
            } else {
                wallPostModel.setDate("");
            }

            boolean noText = vkApiPost.text.isEmpty();
            boolean multipleImage = wallPostModel.getPostPhotos().size() > 1;
            boolean noPhotos = wallPostModel.getPostPhotos().size() == 0;

            if (noText && multipleImage) {
                wallPostModel.setType(WallPostsAdapter.NO_TEXT_MAIN_VIEW_MULTIPLE);
            } else if(noText && !multipleImage) {
                wallPostModel.setType(WallPostsAdapter.NO_TEXT_MAIN_VIEW_HOLDER);
            } else if (!noText && multipleImage) {
                wallPostModel.setType(WallPostsAdapter.MAIN_VIEW_HOLDER_MULTIPLE);
            } else if (!noText && noPhotos) {
                wallPostModel.setType(WallPostsAdapter.NO_PHOTO_MAIN_HOLDER);
            } else {
                wallPostModel.setType(WallPostsAdapter.MAIN_VIEW_HOLDER);
            }

            if (!wallPostModelArrayList.contains(wallPostModel)) {
                newArray.add(wallPostModel);
            }
        }

        if (appendFromBottom) {
            wallPostModelArrayList.addAll(newArray);
        } else {
            wallPostModelArrayList.addAll(0, newArray);
        }

        Collections.sort(wallPostModelArrayList, new Misc.WallPostModelComparator());

        wallPostsRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();

                if (origSize == 0) {
                    //wallPostsRecyclerView.scrollToPosition(0);
                }

                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }
            }
        });

    }

    private boolean setWallModelAttachments(VKApiPost vkApiPost, WallPostModel wallPostModel) {
        VKAttachments attachments = vkApiPost.attachments;
        ArrayList<String> wallPostPhotos = new ArrayList<>();
        wallPostModel.setPostPhotos(wallPostPhotos);
        if (vkApiPost.copy_history.size() > 0) {
            return true;
        }

        for (int i = 0; i < attachments.size(); i++) {
            VKAttachments.VKApiAttachment attachment = attachments.get(i);
            if (attachment.getType().equals(VKApiConst.PHOTO)) {
                String photo = getWallPhoto(attachment);

                if (!photo.isEmpty()) {
                    wallPostPhotos.add(photo);
                }
            } else {
                return true;
            }
        }
        wallPostModel.setPostPhotos(wallPostPhotos);
        return false;
    }

    private String getWallPhoto(VKAttachments.VKApiAttachment attachment) {
        VKApiPhoto vkApiPhoto = (VKApiPhoto) attachment;
        String url = "";

        url = vkApiPhoto.photo_604;

        if (url.isEmpty()) {
            url = vkApiPhoto.photo_807;
        }

        if (url.isEmpty()) {
            url = vkApiPhoto.photo_1280;
        }

        if (url.isEmpty()) {
            url = vkApiPhoto.photo_2560;
        }

        return url;
    }
}
