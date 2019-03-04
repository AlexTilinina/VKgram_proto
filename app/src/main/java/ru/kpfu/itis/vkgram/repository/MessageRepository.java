package ru.kpfu.itis.vkgram.repository;

import android.support.annotation.NonNull;
import com.vk.sdk.api.VKError;
import org.drinkless.td.libcore.telegram.TdApi.*;
import ru.kpfu.itis.vkgram.model.VKChatMessageResponse;
import ru.kpfu.itis.vkgram.ui.chat.ChatPresenter;
import ru.kpfu.itis.vkgram.ui.messages.MessagesPresenter;

public interface MessageRepository {

    void dialogs(int offset, int limit, MessagesPresenter presenter, boolean isNext);

    void dialogs(@NonNull VKChatMessageResponse data);

    void dialogs(@NonNull Chat data);

    void messages(int offset, int limit, long chatId, boolean isNext, boolean vk, ChatPresenter presenter);

    void messages(@NonNull VKChatMessageResponse data);

    void messages(@NonNull Messages data);

    void sendMessage(int peerId, String message);

    void sendMessage(@NonNull Message message);

    void sendMessage();

    void sendMessageError(VKError error);

    //TODO add attachments
    void sendMessage(int peerId, String message, int... forwardedMessaged);
}
