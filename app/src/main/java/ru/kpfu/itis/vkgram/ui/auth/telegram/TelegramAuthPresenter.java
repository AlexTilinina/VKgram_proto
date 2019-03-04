package ru.kpfu.itis.vkgram.ui.auth.telegram;


import static ru.kpfu.itis.vkgram.utils.Constants.T_TAG;

import android.app.Activity;
import android.util.Log;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;
import org.drinkless.td.libcore.telegram.TdApi.Ok;
import ru.kpfu.itis.vkgram.api.ServiceFactory;
import ru.kpfu.itis.vkgram.api.tg_handler.DefaultHandler;
import ru.kpfu.itis.vkgram.ui.messages.MessagesActivity;

@InjectViewState
public class TelegramAuthPresenter extends MvpPresenter<TelegramAuthView> {

    private Client client = ServiceFactory.getTelegramService().getClient();

    public void getAuthCode(String phoneNumber){
        client.send(new TdApi.SetAuthenticationPhoneNumber(phoneNumber, false, true),
                new DefaultHandler());
        Log.d(T_TAG, "getAuthCode: well, we sent number");
        getViewState().changeViewSingUp();
    }

   public void sendAuthCode(String authCode, Activity activity){
        TdApi.CheckAuthenticationCode checkAuthenticationCode = new TdApi.CheckAuthenticationCode();
        checkAuthenticationCode.code = authCode;
        client.send(checkAuthenticationCode, object -> {
            if (object instanceof Ok){
                MessagesActivity.start(activity);
            }
            Log.d(T_TAG + " " + "SEND AUTH CODE", object.toString());
        });
   }

    public void sendAuthCode(String authCode, String firstName, String lastName, Activity activity){
        TdApi.CheckAuthenticationCode checkAuthenticationCode = new TdApi.CheckAuthenticationCode();
        checkAuthenticationCode.code = authCode;
        checkAuthenticationCode.firstName = firstName;
        checkAuthenticationCode.lastName = lastName;
        client.send(checkAuthenticationCode, object -> {
            if (object instanceof Ok){
                MessagesActivity.start(activity);
            }
            Log.d(T_TAG + " " + "SEND AUTH CODE", object.toString());
        });
    }
}
