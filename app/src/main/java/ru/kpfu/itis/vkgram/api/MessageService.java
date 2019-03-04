package ru.kpfu.itis.vkgram.api;

import org.drinkless.td.libcore.telegram.TdApi.Message;

public interface MessageService {

    void dialogs(int offset, int count);

    void messages(int offset, int count, long chatId, boolean vk);

    void sendMessage(int peerId, String message);

    void sendMessage(Message message);
}
