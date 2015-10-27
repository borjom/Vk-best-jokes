package com.randomname.vkjokes.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.randomname.vkjokes.Fragments.CommentsFragment;
import com.randomname.vkjokes.R;
import com.randomname.vkjokes.Util.StringUtils;
import com.randomname.vkjokes.Views.CircleTransform;
import com.squareup.picasso.Picasso;
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

        String avatarUrl = user.photo_50;

        String userName = user.first_name + " " + user.last_name;

        customViewHolder.commentText.setText(Html.fromHtml(commentText));

        try {
            Picasso.with(mContext)
                    .load(avatarUrl)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.camera_c)
                    .into(customViewHolder.userAvatar);
        } catch (Exception e) {
            e.printStackTrace();
        }

        customViewHolder.userNameTextView.setText(userName);

        if (comment.date != 0) {
            customViewHolder.dateTextView.setText(StringUtils.getDateString(comment.date * 1000));
        } else {
            customViewHolder.dateTextView.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return (null != commentsArray ? commentsArray.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView commentText, dateTextView, userNameTextView;
        protected ImageView userAvatar;

        public CustomViewHolder(View view) {
            super(view);
            this.commentText = (TextView) view.findViewById(R.id.comment_text_view);
            this.dateTextView = (TextView) view.findViewById(R.id.date_text_view);
            this.userNameTextView = (TextView) view.findViewById(R.id.user_name_text_view);
            this.userAvatar = (ImageView) view.findViewById(R.id.avatar_image_view);

            commentText.setMovementMethod (LinkMovementMethod.getInstance());

            Typeface robotoLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
            Typeface robotoRegular = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Regular.ttf");
            dateTextView.setTypeface(robotoLight);
            commentText.setTypeface(robotoRegular);
            userNameTextView.setTypeface(robotoRegular);
        }
    }
}
