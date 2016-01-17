package com.randomname.vkjokes.Interfaces;

import com.randomname.vkjokes.Models.WallPostModel;

import java.util.ArrayList;

public interface FragmentsCallbacks {
    public void onButtonClick(ArrayList<String> wallPhotos, int position);
    public void onCommentsClick(WallPostModel wallPostModel);
    public void showVkAlert();
    public void onPhotoFragmentPageSlide(float offset);
    public void onPhotoPageStop();
    public void onPhotoClick();
    public void onPageScroll(int offset);
    public void showSettings();
}
