package com.randomname.vkjokes.Fragments;

import android.os.Bundle;
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

    private WallPostModel wallPostModel;
    private boolean loading = false;
    private ArrayList<HashMap<String, Object>> vkCommentsArray;

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

        getComments();
        return view;
    }

    private void getComments() {
        VKParameters params = new VKParameters();
        params.put("owner_id", wallPostModel.getFromId());
        params.put("post_id", wallPostModel.getId());
        params.put("need_likes", 0);
        params.put("preview_length", 0);
        params.put("offset", 0);
        params.put("count", "200");
        params.put("extended", "1");

        final VKRequest request = new VKRequest("wall.getComments", params);

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                VKCommentArray comments = new VKCommentArray();
                VKUsersArray userFulls = new VKUsersArray();

                try {
                    JSONObject responseObject = response.json.getJSONObject("response");
                    comments.fill(responseObject.getJSONArray("items"), VKApiComment.class);
                    userFulls.fill(responseObject.getJSONArray("profiles"), VKApiUserFull.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loading = false;

                if (comments.size() == 200) {
                    getComments();
                }

                ArrayList<HashMap<String, Object>> newComments = fixComments(comments, userFulls);

                vkCommentsArray.addAll(newComments);
                adapter.notifyDataSetChanged();
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
