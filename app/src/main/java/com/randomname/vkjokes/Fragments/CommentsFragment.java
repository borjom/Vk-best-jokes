package com.randomname.vkjokes.Fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.randomname.vkjokes.Adapters.PhotoCommentsAdapter;
import com.randomname.vkjokes.Models.WallPostModel;
import com.randomname.vkjokes.R;
import com.randomname.vkjokes.Util.StringUtils;
import com.randomname.vkjokes.Views.PreCachingLayoutManager;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiUsers;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKCommentArray;
import com.vk.sdk.api.model.VKPostArray;
import com.vk.sdk.api.model.VKUsersArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CommentsFragment extends Fragment {

    public final static String WALL_POST_MODEL_KEY = "wall_post_model_key";
    public final static String COMMENT_HASH_KEY = "wall_comment_hash_key";
    public final static String USER_HASH_KEY = "user_comment_hash_key";
    public final static String COMMENTS_ARRAY_KEY = "comments_array_key";
    public final static String OFFSET_KEY = "loading_offset_key";
    public final static String RECYCLER_STATE_KEY = "resycler_state_key";

    private WallPostModel wallPostModel;
    private boolean loading = false;
    private ArrayList<HashMap<String, Object>> vkCommentsArray;

    private int offset = 0;

    @Bind(R.id.comments_recycler_view)
    RecyclerView recyclerView;

    PhotoCommentsAdapter adapter;
    PreCachingLayoutManager preCachingLayoutManager;

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

        vkCommentsArray = new ArrayList<>();

        preCachingLayoutManager = new PreCachingLayoutManager(getActivity());
        recyclerView.setLayoutManager(preCachingLayoutManager);

        adapter = new PhotoCommentsAdapter(getActivity(), vkCommentsArray);

        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = preCachingLayoutManager.getChildCount();
                int totalItemCount = preCachingLayoutManager.getItemCount();
                int pastVisiblesItems = preCachingLayoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && offset >= 0) {
                    getComments();
                }
            }
        });

        if (savedInstanceState != null) {
            ArrayList<HashMap<String, Object>> restoredList =(ArrayList<HashMap<String, Object>>) savedInstanceState.getSerializable(COMMENTS_ARRAY_KEY);

            if (restoredList != null) {
                vkCommentsArray.addAll(restoredList);
            }

            Parcelable recyclerState = savedInstanceState.getParcelable(RECYCLER_STATE_KEY);
            preCachingLayoutManager.onRestoreInstanceState(recyclerState);
            offset = savedInstanceState.getInt(OFFSET_KEY);
        } else {
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            getComments();
                        }
                    },
                    300);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(COMMENTS_ARRAY_KEY, vkCommentsArray);

        Parcelable mListState = preCachingLayoutManager.onSaveInstanceState();
        outState.putParcelable(RECYCLER_STATE_KEY, mListState);

        outState.putInt(OFFSET_KEY, offset);

        super.onSaveInstanceState(outState);
    }

    private void getComments() {
        VKParameters params = new VKParameters();
        params.put("owner_id", wallPostModel.getFromId());
        params.put("post_id", wallPostModel.getId());
        params.put("need_likes", 0);
        params.put("preview_length", 0);
        params.put("offset", offset);
        params.put("count", "200");
        params.put("extended", "1");
        params.put("sort", "asc");

        final VKRequest request = new VKRequest("wall.getComments", params);

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                final VKCommentArray comments = new VKCommentArray();
                final VKUsersArray userFulls = new VKUsersArray();

                try {
                    JSONObject responseObject = response.json.getJSONObject("response");
                    comments.fill(responseObject.getJSONArray("items"), VKApiComment.class);
                    userFulls.fill(responseObject.getJSONArray("profiles"), VKApiUserFull.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                offset += 200;

                if (offset >= wallPostModel.getCommentsCount()) {
                    offset = -1;
                }

                final VKCommentArray finalComments = comments;
                final VKUsersArray finalUserFulls = userFulls;

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<HashMap<String, Object>> newComments = fixComments(finalComments, finalUserFulls);

                        vkCommentsArray.addAll(newComments);

                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                loading = false;
                            }
                        });
                    }
                };
                new Thread(runnable).start();
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
                loading = false;

                if (getActivity() == null) {
                    return;
                }

                Toast.makeText(getActivity(), "Произошла ошибка, новая попытка подключиться", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                loading = false;

                if (getActivity() == null) {
                    return;
                }

                Toast.makeText(getActivity(), "Произошла ошибка", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ArrayList<HashMap<String, Object>> fixComments(VKCommentArray comments,  VKUsersArray userFulls) {
        int size = comments.size();
        ArrayList<HashMap<String, Object>> output = new ArrayList<>();
        HashMap<String, Object> hm;

        for (int i = 0; i < size; i++) {
            VKApiComment comment = comments.get(i);
            VKApiUserFull user = null;
            VKAttachments attachments = comment.attachments;

            if (attachments.size() > 0) {
                continue;
            }

            for(VKApiUserFull u : userFulls){
                if (u.id == comment.from_id) {
                    user = u;
                    break;
                }
            }

            if (user == null) {
                user = new VKApiUserFull();
            }

            hm = new HashMap<>();
            hm.put(USER_HASH_KEY, user);
            hm.put(COMMENT_HASH_KEY, comment);

            if (!vkCommentsArray.contains(hm)) {
                vkCommentsArray.add(hm);
            }
        }

        return output;
    }
}
