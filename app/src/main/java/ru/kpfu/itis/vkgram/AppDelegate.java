package ru.kpfu.itis.vkgram;

import android.app.Application;
import com.vk.sdk.VKSdk;

public class AppDelegate extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(this);
    }
}
