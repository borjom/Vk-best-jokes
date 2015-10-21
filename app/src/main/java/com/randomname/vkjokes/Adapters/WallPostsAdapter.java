package com.randomname.vkjokes.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.randomname.vkjokes.Models.WallPostModel;
import com.randomname.vkjokes.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WallPostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<WallPostModel> wallPostModelArrayList;
    private Context mContext;

    public final static int MAIN_VIEW_HOLDER = 0;
    public final static int NO_TEXT_MAIN_VIEW_HOLDER = 1;
    public final static int MAIN_VIEW_HOLDER_MULTIPLE = 2;
    public final static int NO_TEXT_MAIN_VIEW_MULTIPLE = 3;
    public final static int NO_PHOTO_MAIN_HOLDER = 4;

    public WallPostsAdapter(Context context, ArrayList<WallPostModel> wallPostModelArrayList) {
        this.wallPostModelArrayList = wallPostModelArrayList;
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        WallPostModel model = wallPostModelArrayList.get(position);

        return model.getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder;
        View view;

        switch (i) {
            case MAIN_VIEW_HOLDER:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_wall_post, null);
                viewHolder = new MainViewHolder(view);
                break;
            case NO_TEXT_MAIN_VIEW_HOLDER:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.no_text_main_wall_post, null);
                viewHolder = new NoTextMainHolder(view);
                break;
            case MAIN_VIEW_HOLDER_MULTIPLE:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_wall_post_multiple_images, null);
                viewHolder = new MainViewHolderMultipleImages(view);
                break;
            case NO_TEXT_MAIN_VIEW_MULTIPLE:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.no_text_wall_post_multiply_images, null);
                viewHolder = new NoTextMainHolderMultipleImages(view);
                break;
            case NO_PHOTO_MAIN_HOLDER:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.no_photo_text, null);
                viewHolder = new NoPhotoMainHolder(view);
                break;
            default:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_wall_post, null);
                viewHolder = new MainViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        WallPostModel wallPost = wallPostModelArrayList.get(i);

        int type = wallPost.getType();

        switch (type) {
            case MAIN_VIEW_HOLDER:
                fillMainViewHolder(wallPost, viewHolder, i);
                break;
            case NO_TEXT_MAIN_VIEW_HOLDER:
                fillNoTextViewHolder(wallPost, viewHolder, i);
                break;
            case MAIN_VIEW_HOLDER_MULTIPLE:
                fillMainViewHolderMultiple(wallPost, viewHolder, i);
                break;
            case NO_TEXT_MAIN_VIEW_MULTIPLE:
                fillNoTextMainViewHolderMultiple(wallPost, viewHolder, i);
                break;
            case NO_PHOTO_MAIN_HOLDER:
                fillNoPhotoMainHolder(wallPost, viewHolder, i);
                break;
            default:
                fillMainViewHolder(wallPost, viewHolder, i);
        }
    }

    @Override
    public int getItemCount() {
        return (null != wallPostModelArrayList ? wallPostModelArrayList.size() : 0);
    }

    private void fillMainViewHolder(WallPostModel wallPost, RecyclerView.ViewHolder viewHolder, int i) {
        MainViewHolder customViewHolder = (MainViewHolder) viewHolder;

        customViewHolder.textView.setText(wallPost.getText());

        ArrayList<String> wallPhotos = wallPost.getPostPhotos();

        if (wallPhotos.size() > 0) {
            String url = wallPhotos.get(0);
            Picasso.with(mContext).load(url).into(customViewHolder.mainImage);
        } else {
            customViewHolder.mainImage.setImageResource(android.R.color.transparent);
        }
    }

    private void fillNoPhotoMainHolder(WallPostModel wallPost, RecyclerView.ViewHolder viewHolder, int i) {
        NoPhotoMainHolder customViewHolder = (NoPhotoMainHolder) viewHolder;

        customViewHolder.textView.setText(wallPost.getText());
    }

    private void fillMainViewHolderMultiple(WallPostModel wallPost, RecyclerView.ViewHolder viewHolder, int i) {
        MainViewHolderMultipleImages customViewHolder = (MainViewHolderMultipleImages) viewHolder;

        customViewHolder.textView.setText(wallPost.getText());

        ArrayList<String> wallPhotos = wallPost.getPostPhotos();

        if (wallPhotos.size() > 0) {

            customViewHolder.mainImage.setImageResource(android.R.color.transparent);
            customViewHolder.smallImage1.setImageResource(android.R.color.transparent);
            customViewHolder.smallImage2.setImageResource(android.R.color.transparent);
            customViewHolder.smallImage3.setImageResource(android.R.color.transparent);

            for (int y = 0; y < wallPhotos.size(); y++) {
                String url = wallPhotos.get(y);
                switch (y) {
                    case 0:
                        Picasso.with(mContext).load(url).into(customViewHolder.mainImage);
                        break;
                    case 1:
                        Picasso.with(mContext).load(url).into(customViewHolder.smallImage1);
                        break;
                    case 2:
                        Picasso.with(mContext).load(url).into(customViewHolder.smallImage2);
                        break;
                    case 3:
                        Picasso.with(mContext).load(url).into(customViewHolder.smallImage3);
                        break;
                    default:
                }
            }
        }
    }

    private void fillNoTextMainViewHolderMultiple(WallPostModel wallPost, RecyclerView.ViewHolder viewHolder, int i) {
        NoTextMainHolderMultipleImages customViewHolder = (NoTextMainHolderMultipleImages) viewHolder;

        ArrayList<String> wallPhotos = wallPost.getPostPhotos();

        if (wallPhotos.size() > 0) {

            customViewHolder.mainImage.setImageResource(android.R.color.transparent);
            customViewHolder.smallImage1.setImageResource(android.R.color.transparent);
            customViewHolder.smallImage2.setImageResource(android.R.color.transparent);
            customViewHolder.smallImage3.setImageResource(android.R.color.transparent);

            for (int y = 0; y < wallPhotos.size(); y++) {
                String url = wallPhotos.get(y);
                switch (y) {
                    case 0:
                        Picasso.with(mContext).load(url).into(customViewHolder.mainImage);
                        break;
                    case 1:
                        Picasso.with(mContext).load(url).into(customViewHolder.smallImage1);
                        break;
                    case 2:
                        Picasso.with(mContext).load(url).into(customViewHolder.smallImage2);
                        break;
                    case 3:
                        Picasso.with(mContext).load(url).into(customViewHolder.smallImage3);
                        break;
                    default:
                }
            }
        }
    }

    private void fillNoTextViewHolder(WallPostModel wallPost, RecyclerView.ViewHolder viewHolder, int i) {
        NoTextMainHolder customViewHolder = (NoTextMainHolder) viewHolder;
        ArrayList<String> wallPhotos = wallPost.getPostPhotos();

        if (wallPhotos.size() > 0) {
            String url = wallPhotos.get(0);
            Picasso.with(mContext).load(url).into(customViewHolder.mainImage);
        }
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView;
        protected ImageView mainImage;

        public MainViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.textView);
            this.mainImage = (ImageView) view.findViewById(R.id.main_image);
        }
    }

    public class NoPhotoMainHolder extends RecyclerView.ViewHolder {
        protected TextView textView;

        public NoPhotoMainHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.textView);
        }
    }

    public class NoTextMainHolder extends RecyclerView.ViewHolder {
        protected ImageView mainImage;

        public NoTextMainHolder(View view) {
            super(view);
            this.mainImage = (ImageView) view.findViewById(R.id.main_image);
        }
    }

    public class MainViewHolderMultipleImages extends RecyclerView.ViewHolder {
        protected TextView textView;
        protected ImageView mainImage, smallImage1, smallImage2, smallImage3;

        public MainViewHolderMultipleImages(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.textView);
            this.mainImage = (ImageView) view.findViewById(R.id.main_image);
            this.smallImage1 = (ImageView) view.findViewById(R.id.small_image_1);
            this.smallImage2 = (ImageView) view.findViewById(R.id.small_image_2);
            this.smallImage3 = (ImageView) view.findViewById(R.id.small_image_3);
        }
    }

    public class NoTextMainHolderMultipleImages extends RecyclerView.ViewHolder {
        protected ImageView mainImage, smallImage1, smallImage2, smallImage3;

        public NoTextMainHolderMultipleImages(View view) {
            super(view);
            this.mainImage = (ImageView) view.findViewById(R.id.main_image);
            this.smallImage1 = (ImageView) view.findViewById(R.id.small_image_1);
            this.smallImage2 = (ImageView) view.findViewById(R.id.small_image_2);
            this.smallImage3 = (ImageView) view.findViewById(R.id.small_image_3);
        }
    }
}
