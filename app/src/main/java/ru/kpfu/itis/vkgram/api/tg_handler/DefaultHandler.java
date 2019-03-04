package ru.kpfu.itis.vkgram.api.tg_handler;

import static ru.kpfu.itis.vkgram.utils.Constants.T_TAG;

import android.util.Log;
import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

public class DefaultHandler implements Client.ResultHandler {

    @Override
    public void onResult(TdApi.Object object) {
        Log.d(T_TAG, "onResult: " + object.toString());
    }
}
