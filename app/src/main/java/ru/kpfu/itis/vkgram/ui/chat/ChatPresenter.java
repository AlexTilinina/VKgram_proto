package ru.kpfu.itis.vkgram.ui.chat;

import static ru.kpfu.itis.vkgram.utils.Constants.LIMIT;
import static ru.kpfu.itis.vkgram.utils.Constants.ZERO_OFFSET;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.vk.sdk.api.VKError;
import io.reactivex.Single;
import java.util.List;
import org.drinkless.td.libcore.telegram.TdApi.Message;
import org.drinkless.td.libcore.telegram.TdApi.Messages;
import ru.kpfu.itis.vkgram.model.VKChatMessage;
import ru.kpfu.itis.vkgram.repository.RepositoryProvider;
import ru.kpfu.itis.vkgram.utils.Constants;

@InjectViewState
public class ChatPresenter extends MvpPresenter<ChatView>{

    private long chatId;

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().getChatId();
    }

    public void loadChatMessages(long id, boolean vk) {
        chatId = id;
        RepositoryProvider.provideMessageRepository()
                .messages(ZERO_OFFSET, LIMIT, id, false, vk, this);
    }

    public void loadMessages(Single<List<VKChatMessage>> data){
        data.subscribe(getViewState()::showItems, getViewState()::handleError);
    }

    public void loadMessages(Single<Messages> data, boolean tg){
        data.subscribe(getViewState()::showItems, getViewState()::handleError);
    }

    public void loadNextElements(int page, boolean vk) {
        RepositoryProvider.provideMessageRepository()
                .messages(page * LIMIT, LIMIT, chatId, true, vk, this);
    }

    public void loadNextElements(Single<List<VKChatMessage>> data){
        data.subscribe(getViewState()::addMoreItems, getViewState()::handleError);
    }

    public void loadNextElements(Single<Messages> data, boolean tg){
        data.subscribe(getViewState()::addMoreItems, getViewState()::handleError);
    }

    public void sendMessage(VKChatMessage message){
        int peerId = message.getChatId() == 0 ? message.user_id : message.getChatId() + Constants.CHAT_PREFIX;
        getViewState().setMessageSending();
        RepositoryProvider.provideMessageRepository()
                .sendMessage(peerId, message.body);
    }

    public void sendMessage(Message message){
        getViewState().setMessageSending();
        RepositoryProvider.provideMessageRepository().sendMessage(message);
    }

    public void sendMessage(){
        getViewState().setMessageSent();
    }

    public void error(VKError error){
        getViewState().error(error);
    }
}
