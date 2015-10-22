package com.randomname.vkjokes.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.randomname.vkjokes.R;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKCommentArray;

public class PhotoCommentsAdapter extends RecyclerView.Adapter<PhotoCommentsAdapter.CustomViewHolder> {
    private Context mContext;
    private VKCommentArray commentsArray;

    public PhotoCommentsAdapter(Context context, VKCommentArray commentsArray) {
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
        VKApiComment comment = commentsArray.get(i);

        customViewHolder.commentText.setText(comment.text);
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
        }
    }
}
