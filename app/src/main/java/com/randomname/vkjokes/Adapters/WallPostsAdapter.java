package com.randomname.vkjokes.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.randomname.vkjokes.Fragments.PublicListFragment;
import com.randomname.vkjokes.MainActivity;
import com.randomname.vkjokes.Models.WallPostModel;
import com.randomname.vkjokes.R;
import com.randomname.vkjokes.Views.WallPostViewHolder;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class WallPostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<WallPostModel> wallPostModelArrayList;
    private Context mContext;
    private PublicListFragment.PublicListFragmentCallback callbacks;

    public final static int MAIN_VIEW_HOLDER = 0;
    public final static int NO_TEXT_MAIN_VIEW_HOLDER = 1;
    public final static int MAIN_VIEW_HOLDER_MULTIPLE = 2;
    public final static int NO_TEXT_MAIN_VIEW_MULTIPLE = 3;
    public final static int NO_PHOTO_MAIN_HOLDER = 4;

    public WallPostsAdapter(Context context, ArrayList<WallPostModel> wallPostModelArrayList) {
        this.wallPostModelArrayList = wallPostModelArrayList;
        this.mContext = context;

        if (context instanceof Activity){
            Activity a = (Activity) context;

            try {
                callbacks = (PublicListFragment.PublicListFragmentCallback) a;
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
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

        WallPostViewHolder holder = (WallPostViewHolder) viewHolder;
        holder.dateTextView.setText(wallPost.getDate());

        switch (type) {
            case MAIN_VIEW_HOLDER:
                fillMainViewHolder(wallPost, holder, i);
                break;
            case NO_TEXT_MAIN_VIEW_HOLDER:
                fillNoTextViewHolder(wallPost, holder, i);
                break;
            case MAIN_VIEW_HOLDER_MULTIPLE:
                fillMainViewHolderMultiple(wallPost, holder, i);
                break;
            case NO_TEXT_MAIN_VIEW_MULTIPLE:
                fillNoTextMainViewHolderMultiple(wallPost, holder, i);
                break;
            case NO_PHOTO_MAIN_HOLDER:
                fillNoPhotoMainHolder(wallPost, holder, i);
                break;
            default:
                fillMainViewHolder(wallPost, holder, i);
        }
    }

    @Override
    public int getItemCount() {
        return (null != wallPostModelArrayList ? wallPostModelArrayList.size() : 0);
    }

    private void onImageClickAction(ArrayList<String> wallPhotos, int position) {
        if (callbacks != null) {
            callbacks.onButtonClick(wallPhotos, position);
        }
    }

    private void setOnImageClick(View view, final ArrayList<String> wallPhotos, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClickAction(wallPhotos, position);
            }
        });
    }

    private void fillMainViewHolder(WallPostModel wallPost, RecyclerView.ViewHolder viewHolder, int i) {
        MainViewHolder customViewHolder = (MainViewHolder) viewHolder;

        customViewHolder.textView.setText(wallPost.getText());

        final ArrayList<String> wallPhotos = wallPost.getPostPhotos();

        if (wallPhotos.size() > 0) {
            String url = wallPhotos.get(0);
            Picasso.with(mContext).load(url).into(customViewHolder.mainImage);
            setOnImageClick(customViewHolder.mainImage, wallPhotos, 0);
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

            customViewHolder.smallImage2.setVisibility(View.GONE);
            customViewHolder.smallImage3Wrapper.setVisibility(View.GONE);
            customViewHolder.mainImage.setImageResource(android.R.color.transparent);
            customViewHolder.smallImage1.setImageResource(android.R.color.transparent);
            customViewHolder.smallImage2.setImageResource(android.R.color.transparent);
            customViewHolder.smallImage3.setImageResource(android.R.color.transparent);

            for (int y = 0; y < wallPhotos.size(); y++) {
                String url = wallPhotos.get(y);
                switch (y) {
                    case 0:
                        Picasso.with(mContext).load(url).into(customViewHolder.mainImage);
                        setOnImageClick(customViewHolder.mainImage, wallPhotos, 0);
                        break;
                    case 1:
                        Picasso.with(mContext).load(url).into(customViewHolder.smallImage1);
                        setOnImageClick(customViewHolder.smallImage1, wallPhotos, 1);
                        break;
                    case 2:
                        customViewHolder.smallImage2.setVisibility(View.VISIBLE);
                        Picasso.with(mContext).load(url).into(customViewHolder.smallImage2);
                        setOnImageClick(customViewHolder.smallImage2, wallPhotos, 2);
                        break;
                    case 3:
                        customViewHolder.smallImage3Wrapper.setVisibility(View.VISIBLE);
                        Picasso.with(mContext).load(url).into(customViewHolder.smallImage3);
                        setOnImageClick(customViewHolder.smallImage3, wallPhotos, 3);
                        customViewHolder.morePhoto.setText("+" + (wallPhotos.size() - 4));
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

            customViewHolder.smallImage2.setVisibility(View.GONE);
            customViewHolder.smallImage3Wrapper.setVisibility(View.GONE);
            customViewHolder.mainImage.setImageResource(android.R.color.transparent);
            customViewHolder.smallImage1.setImageResource(android.R.color.transparent);
            customViewHolder.smallImage2.setImageResource(android.R.color.transparent);
            customViewHolder.smallImage3.setImageResource(android.R.color.transparent);

            for (int y = 0; y < wallPhotos.size(); y++) {
                String url = wallPhotos.get(y);
                switch (y) {
                    case 0:
                        Picasso.with(mContext).load(url).into(customViewHolder.mainImage);
                        setOnImageClick(customViewHolder.mainImage, wallPhotos, 0);
                        break;
                    case 1:
                        Picasso.with(mContext).load(url).into(customViewHolder.smallImage1);
                        setOnImageClick(customViewHolder.smallImage1, wallPhotos, 1);
                        break;
                    case 2:
                        customViewHolder.smallImage2.setVisibility(View.VISIBLE);
                        Picasso.with(mContext).load(url).into(customViewHolder.smallImage2);
                        setOnImageClick(customViewHolder.smallImage2, wallPhotos, 2);
                        break;
                    case 3:
                        customViewHolder.smallImage3Wrapper.setVisibility(View.VISIBLE);
                        Picasso.with(mContext).load(url).into(customViewHolder.smallImage3);
                        setOnImageClick(customViewHolder.smallImage3, wallPhotos, 3);
                        customViewHolder.morePhoto.setText("+" + (wallPhotos.size() - 4));
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
            setOnImageClick(customViewHolder.mainImage, wallPhotos, 0);
        }
    }

    public class MainViewHolder extends WallPostViewHolder {
        protected TextView textView;
        protected ImageView mainImage;

        public MainViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.textView);
            this.mainImage = (ImageView) view.findViewById(R.id.main_image);
        }
    }

    public class NoPhotoMainHolder extends WallPostViewHolder {
        protected TextView textView;

        public NoPhotoMainHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.textView);
        }
    }

    public class NoTextMainHolder extends WallPostViewHolder {
        protected ImageView mainImage;

        public NoTextMainHolder(View view) {
            super(view);
            this.mainImage = (ImageView) view.findViewById(R.id.main_image);
        }
    }

    public class MainViewHolderMultipleImages extends WallPostViewHolder {
        protected TextView textView, morePhoto;
        protected ImageView mainImage, smallImage1, smallImage2, smallImage3;
        protected RelativeLayout smallImage3Wrapper;

        public MainViewHolderMultipleImages(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.textView);
            this.morePhoto = (TextView) view.findViewById(R.id.more_photo_text_view);
            this.mainImage = (ImageView) view.findViewById(R.id.main_image);
            this.smallImage1 = (ImageView) view.findViewById(R.id.small_image_1);
            this.smallImage2 = (ImageView) view.findViewById(R.id.small_image_2);
            this.smallImage3 = (ImageView) view.findViewById(R.id.small_image_3);
            this.smallImage3Wrapper = (RelativeLayout) view.findViewById(R.id.small_image_3_layout);
        }
    }

    public class NoTextMainHolderMultipleImages extends WallPostViewHolder {
        protected TextView morePhoto;
        protected ImageView mainImage, smallImage1, smallImage2, smallImage3;
        protected RelativeLayout smallImage3Wrapper;

        public NoTextMainHolderMultipleImages(View view) {
            super(view);
            this.mainImage = (ImageView) view.findViewById(R.id.main_image);
            this.morePhoto = (TextView) view.findViewById(R.id.more_photo_text_view);
            this.smallImage1 = (ImageView) view.findViewById(R.id.small_image_1);
            this.smallImage2 = (ImageView) view.findViewById(R.id.small_image_2);
            this.smallImage3 = (ImageView) view.findViewById(R.id.small_image_3);
            this.smallImage3Wrapper = (RelativeLayout) view.findViewById(R.id.small_image_3_layout);
        }
    }
}
