package com.randomname.vkjokes;

import android.app.Application;
import android.os.SystemClock;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import java.util.concurrent.TimeUnit;

public class PublicsApplication extends Application {

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        SystemClock.sleep(TimeUnit.SECONDS.toMillis(2));
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
    }
}
