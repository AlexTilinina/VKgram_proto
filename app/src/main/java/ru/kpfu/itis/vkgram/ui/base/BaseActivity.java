package ru.kpfu.itis.vkgram.ui.base;

import static ru.kpfu.itis.vkgram.utils.Constants.API_HASH;
import static ru.kpfu.itis.vkgram.utils.Constants.API_ID;
import static ru.kpfu.itis.vkgram.utils.Constants.LIMIT;
import static ru.kpfu.itis.vkgram.utils.Constants.T_TAG;
import static ru.kpfu.itis.vkgram.utils.Constants.ZERO_OFFSET;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.arellomobile.mvp.MvpAppCompatActivity;
import com.vk.sdk.*;
import com.vk.sdk.api.*;
import com.vk.sdk.api.VKRequest.*;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;
import java.io.File;
import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;
import org.drinkless.td.libcore.telegram.TdApi.*;
import org.json.JSONException;
import ru.kpfu.itis.vkgram.R;
import ru.kpfu.itis.vkgram.api.ServiceFactory;
import ru.kpfu.itis.vkgram.api.tg_handler.AuthorizationRequestHandler;
import ru.kpfu.itis.vkgram.model.VKChatMessage;
import ru.kpfu.itis.vkgram.model.VKChatMessageResponse;
import ru.kpfu.itis.vkgram.ui.auth.telegram.TelegramAuthActivity;
import ru.kpfu.itis.vkgram.ui.chat.ChatActivity;
import ru.kpfu.itis.vkgram.ui.messages.MessagesActivity;
import ru.kpfu.itis.vkgram.utils.Constants;

public abstract class BaseActivity extends MvpAppCompatActivity {

    protected DrawerLayout mDrawer;

    protected NavigationView mNavigationView;

    protected VKAccessTokenTracker vkAccessTokenTracker;

    protected VKApiUser currentVKUser;

    protected Client tgClient;

    protected User tgUser;

    private String[] scopeList = new String[]{
            VKScope.MESSAGES, VKScope.FRIENDS,
            VKScope.PHOTOS, VKScope.AUDIO,
            VKScope.VIDEO, VKScope.DOCS, VKScope.OFFLINE,
            VKScope.NOTIFY, VKScope.NOTIFICATIONS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        initVK();
        initTelegram();

        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        VKCallback<VKAccessToken> accessTokenVKCallback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // Пользователь успешно авторизовался
                getCurrentVKUserFromToken();
                Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(VKError error) {
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                Toast.makeText(BaseActivity.this, R.string.auth_error, Toast.LENGTH_SHORT).show();
            }
        };

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, accessTokenVKCallback)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setCurrentVKUser(VKApiUser user) {
        currentVKUser = user;
        mNavigationView.getMenu().findItem(R.id.menu_vk_profile).setTitle(currentVKUser.toString());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    protected void getCurrentVKUserFromToken() {
        if (isNetworkAvailable()) {
            if (VKAccessToken.currentToken() != null && currentVKUser == null) {
                //TODO юзера сохранять в кэш и брать оттуда
                VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS,
                        VKAccessToken.currentToken().userId));
                request.executeWithListener(new VKRequestListener() {
                    @Override
                    public void onComplete(final VKResponse response) {
                        super.onComplete(response);
                        VKList<VKApiUser> vkApiUserVKList = new VKList<>(response.json, VKApiUser.class);
                        setCurrentVKUser(vkApiUserVKList.get(0));
                    }
                });
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.net_error, Toast.LENGTH_LONG).show();
        }
    }

    protected void getCurrentVKUserChat() {
        if (isNetworkAvailable()) {
            VKParameters vkParameters = VKParameters.from(VKApiConst.OFFSET, ZERO_OFFSET,
                    VKApiConst.COUNT, LIMIT, VKApiConst.USER_ID, currentVKUser.getId());
            VKRequest request = new VKRequest(Constants.MESSAGES_GET_HISTORY, vkParameters);
            request.executeWithListener(new VKRequestListener() {
                @Override
                public void onComplete(final VKResponse response) {
                    super.onComplete(response);
                    try {
                        VKChatMessage vkChatMessage = new VKChatMessageResponse(response.json).getData().getItems()
                                .get(0);
                        ChatActivity.start(BaseActivity.this, vkChatMessage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    protected VKApiUser getCurrentVKUser() {
        return currentVKUser;
    }

    protected void supportActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        initNavigationDrawer(toolbar);
    }

    protected void setBackArrow(Toolbar toolbar) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void initVK() {
        initVKAccessTokenTracker();
        vkAccessTokenTracker.startTracking();
        getCurrentVKUserFromToken();
    }


    private void initTelegram() {
        tgClient = ServiceFactory.getTelegramService().getClient();
        tgClient.send(new GetMe(), object -> {
            if (object instanceof User){
                setCurrentTelegramUser((User) object);
            }
            else {
                tgClient.send(new SetTdlibParameters(initTelegramParameters()), new AuthorizationRequestHandler());
                tgClient.send(new TdApi.CheckDatabaseEncryptionKey(), new AuthorizationRequestHandler());
            }
        });
    }

    private TdlibParameters initTelegramParameters(){
        TdlibParameters parameters = new TdlibParameters();
        String path = Environment.getExternalStorageDirectory().getPath()
                + "/" +  getPackageName().split("\\.")[3] + "/tdlib";
        parameters.databaseDirectory = path;
        new File(parameters.databaseDirectory).mkdir();
        parameters.useMessageDatabase = true;
        parameters.useSecretChats = true;
        parameters.apiId = API_ID;
        parameters.apiHash = API_HASH;
        parameters.systemLanguageCode = "en";
        parameters.deviceModel = "Mobile";
        parameters.systemVersion = "Unknown";
        parameters.applicationVersion = "1.0";
        parameters.enableStorageOptimizer = true;
        //parameters.useTestDc = true;
        return parameters;
    }

    public void setCurrentTelegramUser(User user) {
        if (user != null){
            tgUser = user;
            String name = tgUser.firstName + " " + tgUser.lastName;
            mNavigationView.getMenu().findItem(R.id.menu_tg_profile).setTitle(name);
        }
    }

    private void initNavigationDrawer(Toolbar toolbar) {
        mNavigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            switch (id) {
                case R.id.menu_vk_profile:
                    if (VKAccessToken.currentToken() == null) {
                        VKSdk.login(this, scopeList);
                    } else {
                        //TODO сделать активити для редактирования аккаунта
                        showPopupMenu(toolbar, R.id.menu_vk_profile);
                    }
                    break;
                case R.id.menu_vk_saved_messages:
                    if (currentVKUser != null) {
                        getCurrentVKUserChat();
                    }
                    break;
                case R.id.menu_tg_profile:
                    tgClient.send(new TdApi.GetAuthorizationState(), object -> {
                        boolean waitingPhone = object instanceof TdApi.AuthorizationStateWaitPhoneNumber;
                        boolean waitingCode = object instanceof TdApi.AuthorizationStateWaitCode;
                        boolean stateClosed = object instanceof TdApi.AuthorizationStateClosed;
                        if (waitingPhone || waitingCode || stateClosed){
                            TelegramAuthActivity.start(this, waitingPhone || stateClosed);
                        }
                        else {
                            //TODO активити для выхода
                            TGLogOut();
                        }
                    });
                    break;
                case R.id.menu_tg_saved_messages:
                    //TODO сохраненки
                    break;
            }
            return true;
        });
        setActionBar(toolbar);
    }

    private void showPopupMenu(View v, int id) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.profile_drawer);
        popupMenu.setOnMenuItemClickListener(
                menuItem -> {
                    switch (id) {
                        case R.id.menu_vk_profile:
                            VKLogOut();
                            return true;
                    }
                    return false;
                }
        );
        popupMenu.show();
    }

    private void VKLogOut() {
        currentVKUser = null;
        mNavigationView.getMenu().findItem(R.id.menu_vk_profile).setTitle(R.string.login_vk);
        VKSdk.logout();
        Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);
        startActivity(intent);
    }

    private void TGLogOut() {
        mNavigationView.getMenu().findItem(R.id.menu_tg_profile).setTitle(R.string.login_telegram);
        tgClient.send(new TdApi.LogOut(), object -> Log.d(T_TAG, "onResult: YOU LOGGED OFF TELEGRAM"));
        Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);
        startActivity(intent);
    }

    private void setActionBar(Toolbar toolbar) {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        mDrawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void initVKAccessTokenTracker() {
        vkAccessTokenTracker = new VKAccessTokenTracker() {
            @Override
            public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
                if (newToken == null) {
                    // VKAccessToken is invalid
                }
            }
        };
    }
}
