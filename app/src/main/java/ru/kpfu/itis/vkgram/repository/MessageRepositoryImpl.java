package ru.kpfu.itis.vkgram.repository;

import android.support.annotation.NonNull;
import com.vk.sdk.api.VKError;
import io.reactivex.Single;
import java.util.ArrayList;
import java.util.List;
import org.drinkless.td.libcore.telegram.TdApi.Chat;
import org.drinkless.td.libcore.telegram.TdApi.Chats;
import org.drinkless.td.libcore.telegram.TdApi.Message;
import org.drinkless.td.libcore.telegram.TdApi.Messages;
import ru.kpfu.itis.vkgram.api.ServiceFactory;
import ru.kpfu.itis.vkgram.model.VKChatMessage;
import ru.kpfu.itis.vkgram.model.VKChatMessageResponse;
import ru.kpfu.itis.vkgram.model.VKChatMessageResponseData;
import ru.kpfu.itis.vkgram.ui.chat.ChatPresenter;
import ru.kpfu.itis.vkgram.ui.messages.MessagesPresenter;
import ru.kpfu.itis.vkgram.utils.RxUtils;

public class MessageRepositoryImpl implements MessageRepository {

    private MessagesPresenter presenter;
    private ChatPresenter chatPresenter;

    private boolean isNext;

    @Override
    public void dialogs(int offset, int count, MessagesPresenter presenter, boolean isNext) {
        this.presenter = presenter;
        this.isNext = isNext;
        ServiceFactory.getMessageService()
                .dialogs(offset, count);
    }

    @Override
    public void dialogs(@NonNull VKChatMessageResponse data) {
        Single<VKChatMessageResponse> single = Single.just(data);
        //TODO сделать кэш
        if (isNext){
            presenter.loadNextElements(single
                    .map(VKChatMessageResponse::getData)
                    .map(VKChatMessageResponseData::getItems)
                    .compose(RxUtils.asyncSingle()));
        }
        else {
            presenter.loadMessages(single
                    .map(VKChatMessageResponse::getData)
                    .map(VKChatMessageResponseData::getItems)
                    .compose(RxUtils.asyncSingle()));
        }
    }

    @Override
    public void dialogs(@NonNull Chat data) {
        Single<Chat> single = Single.just(data);
        presenter.loadNextElements(single.compose(RxUtils.asyncSingle()), true);
    }

    @Override
    public void messages(int offset, int limit, long chatId, boolean isNext, boolean vk, ChatPresenter presenter) {
        chatPresenter = presenter;
        this.isNext = isNext;
        ServiceFactory.getMessageService()
                .messages(offset, limit, chatId, vk);
    }

    @Override
    public void messages(@NonNull VKChatMessageResponse data) {
        Single<VKChatMessageResponse> single = Single.just(data);
        if (isNext){
            chatPresenter.loadNextElements(single
                    .map(VKChatMessageResponse::getData)
                    .map(VKChatMessageResponseData::getItems)
                    .compose(RxUtils.asyncSingle()));
        }
        else {
            chatPresenter.loadMessages(single
                    .map(VKChatMessageResponse::getData)
                    .map(VKChatMessageResponseData::getItems)
                    .compose(RxUtils.asyncSingle()));
        }
    }

    @Override
    public void messages(@NonNull Messages data) {
        Single<Messages> single = Single.just(data);
        if (isNext){
            chatPresenter.loadNextElements(single.compose(RxUtils.asyncSingle()), true);
        } else {
            chatPresenter.loadMessages(single.compose(RxUtils.asyncSingle()), true);
        }

    }

    @Override
    public void sendMessage(int peerId, String message) {
        ServiceFactory.getMessageService()
                .sendMessage(peerId, message);
    }

    @Override
    public void sendMessage(@NonNull Message message) {
        ServiceFactory.getMessageService().sendMessage(message);
    }

    @Override
    public void sendMessage() {
        chatPresenter.sendMessage();
    }

    @Override
    public void sendMessageError(VKError error) {
        chatPresenter.error(error);
    }

    @Override
    public void sendMessage(int peerId, String message, int... forwardedMessaged) {

    }
}
