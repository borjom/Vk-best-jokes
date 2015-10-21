package com.randomname.vkjokes.Views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.randomname.vkjokes.R;

public class WallPostViewHolder extends RecyclerView.ViewHolder {

    public TextView dateTextView;

    public WallPostViewHolder(View itemView) {
        super(itemView);

        this.dateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
    }
}
