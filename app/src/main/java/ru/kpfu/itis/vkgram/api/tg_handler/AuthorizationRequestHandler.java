package ru.kpfu.itis.vkgram.api.tg_handler;

import static ru.kpfu.itis.vkgram.utils.Constants.T_TAG;

import android.util.Log;
import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

public class AuthorizationRequestHandler implements Client.ResultHandler {
    @Override
    public void onResult(TdApi.Object object) {
        switch (object.getConstructor()) {
            case TdApi.Error.CONSTRUCTOR:
                Log.d(T_TAG,"Receive an error:" + object);
                break;
            case TdApi.Ok.CONSTRUCTOR:
                break;
            default:
                Log.d(T_TAG,"Receive wrong response from TDLib:" + object);
        }
    }
}