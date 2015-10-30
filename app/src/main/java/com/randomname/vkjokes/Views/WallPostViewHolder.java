package com.randomname.vkjokes.Views;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.randomname.vkjokes.R;

public class WallPostViewHolder extends RecyclerView.ViewHolder {

    public TextView dateTextView, likeCountTextView;
    public ImageButton likeButton, repostButton;

    public WallPostViewHolder(View itemView) {
        super(itemView);
        this.dateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
        this.likeCountTextView = (TextView) itemView.findViewById(R.id.likes_count_text_view);
        this.likeButton = (ImageButton) itemView.findViewById(R.id.like_image_button);
        this.repostButton = (ImageButton) itemView.findViewById(R.id.repost_image_button);
    }
}
