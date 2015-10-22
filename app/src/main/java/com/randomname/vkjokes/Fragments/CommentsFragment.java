package com.randomname.vkjokes.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.randomname.vkjokes.Models.WallPostModel;
import com.randomname.vkjokes.R;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKCommentArray;
import com.vk.sdk.api.model.VKPostArray;

import org.json.JSONException;

import butterknife.ButterKnife;

public class CommentsFragment extends Fragment {

    public final static String WALL_POST_MODEL_KEY = "wall_post_model_key";

    private WallPostModel wallPostModel;
    private boolean loading = false;

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
        params.put("count", "10");

        final VKRequest request = new VKRequest("wall.getComments", params);

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                VKCommentArray comments = new VKCommentArray();

                try {
                    comments.parse(response.json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                loading = false;
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
}
