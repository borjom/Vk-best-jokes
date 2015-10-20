package com.randomname.vkjokes;

import android.app.Application;

import com.vk.sdk.VKSdk;

public class PublicsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(this);
    }
}
