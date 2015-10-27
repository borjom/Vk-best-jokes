package com.randomname.vkjokes.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.randomname.vkjokes.Fragments.CommentsFragment;
import com.randomname.vkjokes.R;
import com.randomname.vkjokes.Util.StringUtils;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKCommentArray;

import java.util.ArrayList;
import java.util.HashMap;

public class PhotoCommentsAdapter extends RecyclerView.Adapter<PhotoCommentsAdapter.CustomViewHolder> {
    private Context mContext;
    private ArrayList<HashMap<String, Object>> commentsArray;

    public PhotoCommentsAdapter(Context context, ArrayList<HashMap<String, Object>> commentsArray) {
        this.mContext = context;
        this.commentsArray = commentsArray;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_row, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        HashMap<String, Object> hm = commentsArray.get(i);
        VKApiComment comment =(VKApiComment) hm.get(CommentsFragment.COMMENT_HASH_KEY);
        VKApiUserFull user = (VKApiUserFull) hm.get(CommentsFragment.USER_HASH_KEY);

        String commentText = comment.text;
        commentText = commentText.replace(" ", "&nbsp;");
        commentText = StringUtils.replaceURLwithAnchor(commentText);
        commentText = StringUtils.replaceVkLinks(commentText);

        customViewHolder.commentText.setText(Html.fromHtml(commentText));
    }

    @Override
    public int getItemCount() {
        return (null != commentsArray ? commentsArray.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView commentText;

        public CustomViewHolder(View view) {
            super(view);
            this.commentText = (TextView) view.findViewById(R.id.comment_text_view);

            commentText.setMovementMethod (LinkMovementMethod.getInstance());
        }
    }
}
