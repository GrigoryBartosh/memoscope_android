package ru.memoscope.android;

import android.app.Application;

import com.vk.sdk.VKSdk;

public class Memoscope extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(this);
    }
}
