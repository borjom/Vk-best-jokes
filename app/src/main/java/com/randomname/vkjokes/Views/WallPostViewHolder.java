package com.randomname.vkjokes.Views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.randomname.vkjokes.R;

public class WallPostViewHolder extends RecyclerView.ViewHolder {

    public TextView dateTextView, commentCountTextView, likeCountTextView;

    public WallPostViewHolder(View itemView) {
        super(itemView);

        this.dateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
        this.commentCountTextView = (TextView) itemView.findViewById(R.id.comments_count_text_view);
        this.likeCountTextView = (TextView) itemView.findViewById(R.id.likes_count_text_view);
    }
}
