package com.randomname.vkjokes.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.randomname.vkjokes.Adapters.WallPostsAdapter;
import com.randomname.vkjokes.Models.WallPostModel;
import com.randomname.vkjokes.R;
import com.randomname.vkjokes.Views.PreCachingLayoutManager;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKPostArray;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublicListFragment extends Fragment {

    private PublicListFragmentCallback publicListFragmentCallback;
    private WallPostsAdapter adapter;
    private ArrayList<WallPostModel> wallPostModelArrayList;
    private PreCachingLayoutManager preCachingLayoutManager;

    private int offset = 0;
    private boolean loading = false;

    @Bind(R.id.wall_posts_recycler_view)
    RecyclerView wallPostsRecyclerView;

    public PublicListFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity a;

        if (context instanceof Activity){
            a = (Activity) context;

            try {
                publicListFragmentCallback = (PublicListFragmentCallback) a;
            } catch (ClassCastException e) {
                throw new ClassCastException(a.toString() + " must implement MainFragmentCallbacks");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.public_list_fragment, container, false);
        ButterKnife.bind(this, view);

        wallPostModelArrayList = new ArrayList<>();
        getWallPosts();

        preCachingLayoutManager = new PreCachingLayoutManager(getActivity());

        wallPostsRecyclerView.setLayoutManager(preCachingLayoutManager);
        adapter = new WallPostsAdapter(getActivity(), wallPostModelArrayList);
        wallPostsRecyclerView.setAdapter(adapter);

        wallPostsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = preCachingLayoutManager.getChildCount();
                int totalItemCount = preCachingLayoutManager.getItemCount();
                int pastVisiblesItems = preCachingLayoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    if (!loading) {
                        loading = true;
                        getWallPosts();
                    }
                }
            }
        });

        return view;
    }

    private void getWallPosts() {
        VKParameters params = new VKParameters();
        params.put("domain", "mdk");
        params.put("count", "10");
        params.put("offset", offset);

        final VKRequest request = new VKRequest("wall.get", params);

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

                convertVKPostToWallPost(posts);

                offset += 10;
                loading = false;
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
                loading = false;
                Toast.makeText(getActivity(), "Произошла ошибка, новая попытка подключиться", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                loading = false;
                Toast.makeText(getActivity(), "Произошла ошибка", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void convertVKPostToWallPost(VKPostArray vkPosts) {
        int size = vkPosts.size();

        for (int i = 0; i < size; i++) {
            VKApiPost vkApiPost = vkPosts.get(i);
            WallPostModel wallPostModel = new WallPostModel();

            ArrayList<String> wallPhotos = getWallPhotos(vkApiPost);
            wallPostModel.setPostPhotos(wallPhotos);

            wallPostModel.setText(vkApiPost.text);
            wallPostModel.setId(vkApiPost.getId());
            wallPostModel.setCommentsCount(vkApiPost.comments_count);
            wallPostModel.setLikeCount(vkApiPost.likes_count);

            if (vkApiPost.date > 0) {
                long millisecond = vkApiPost.date * 1000;
                String dateString= DateFormat.format("dd MMMM kk:mm", new Date(millisecond)).toString();

                wallPostModel.setDate(dateString);
            } else {
                wallPostModel.setDate("");
            }

            boolean noText = vkApiPost.text.isEmpty();
            boolean multipleImage = wallPhotos.size() > 1;

            if (noText && multipleImage) {
                wallPostModel.setType(WallPostsAdapter.NO_TEXT_MAIN_VIEW_MULTIPLE);
            } else if(noText && !multipleImage) {
                wallPostModel.setType(WallPostsAdapter.NO_TEXT_MAIN_VIEW_HOLDER);
            } else if (!noText && multipleImage) {
                wallPostModel.setType(WallPostsAdapter.MAIN_VIEW_HOLDER_MULTIPLE);
            } else if (!noText && wallPhotos.size() == 0) {
                wallPostModel.setType(WallPostsAdapter.NO_PHOTO_MAIN_HOLDER);
            } else {
                wallPostModel.setType(WallPostsAdapter.MAIN_VIEW_HOLDER);
            }

            wallPostModelArrayList.add(wallPostModel);
        }

        adapter.notifyDataSetChanged();
    }

    private ArrayList<String> getWallPhotos(VKApiPost vkApiPost) {
        ArrayList<String> output = new ArrayList<>();

        VKAttachments attachments = vkApiPost.attachments;

        if (attachments.isEmpty()) {
            return output;
        }

        for (int i = 0; i < attachments.size(); i++) {
            VKAttachments.VKApiAttachment attachment = attachments.get(i);

            if (!attachment.getType().equals(VKApiConst.PHOTO)) {
                break;
            }

            VKApiPhoto vkApiPhoto = (VKApiPhoto)attachment;
            String url = "";

            url = vkApiPhoto.photo_2560;

            if (url.isEmpty()) {
                url = vkApiPhoto.photo_1280;
            }

            if (url.isEmpty()) {
                url = vkApiPhoto.photo_807;
            }

            if (url.isEmpty()) {
                url = vkApiPhoto.photo_604;
            }

            if (!url.isEmpty()) {
                output.add(url);
            }
        }

        return output;
    }

    public interface PublicListFragmentCallback {
        public void onButtonClick(ArrayList<String> wallPhotos, int position);
    }
}
