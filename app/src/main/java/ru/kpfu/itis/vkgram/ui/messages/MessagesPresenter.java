package ru.kpfu.itis.vkgram.ui.messages;

import static ru.kpfu.itis.vkgram.utils.Constants.LIMIT;
import static ru.kpfu.itis.vkgram.utils.Constants.ZERO_OFFSET;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.vk.sdk.VKAccessToken;
import io.reactivex.Single;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.drinkless.td.libcore.telegram.TdApi.Chat;
import ru.kpfu.itis.vkgram.model.VKChatMessage;
import ru.kpfu.itis.vkgram.repository.RepositoryProvider;

@InjectViewState
public class MessagesPresenter extends MvpPresenter<MessagesView> {

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        loadMessages();
    }

    public void loadMessages() {
        RepositoryProvider.provideMessageRepository()
                .dialogs(ZERO_OFFSET, LIMIT, this, false);
    }

    public void loadMessages(Single<List<VKChatMessage>> data){
        data.doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .subscribe(messages -> getViewState().showItems(invertToObject(messages)),
                        getViewState()::handleError);
    }

    public void loadMessages(Single<Chat> data, boolean tg){
        data.subscribe(messages -> getViewState().showItems(invertToObject(messages)), getViewState()::handleError);
    }

    public void loadNextElements(int page) {
        //TODO почему-то работает через раз
       RepositoryProvider.provideMessageRepository()
                .dialogs(page * LIMIT, LIMIT, this, true);
    }

    public void loadNextElements(Single<List<VKChatMessage>> data){
        data.doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .doAfterTerminate(getViewState()::setNotLoading)
                .subscribe(messages -> getViewState().addMoreItems(invertToObject(messages)),
                        getViewState()::handleError);
    }

    public void loadNextElements(Single<Chat> data, boolean tg){
        data.subscribe(getViewState()::addMoreItems, getViewState()::handleError);
    }

    public void onItemClick(VKChatMessage message) {
        getViewState().showDetails(message);
    }

    public void onItemClick(Chat item){
        getViewState().showDetails(item);
    }

    private List<Object> invertToObject(List<VKChatMessage> items){
        return new ArrayList<>(items);
    }

    private List<Object> invertToObject(Chat items){
        return new ArrayList<>(Collections.singletonList(items));
    }
}
