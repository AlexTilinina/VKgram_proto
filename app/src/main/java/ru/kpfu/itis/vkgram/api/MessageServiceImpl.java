package ru.kpfu.itis.vkgram.api;

import android.util.Log;
import com.vk.sdk.api.*;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;
import org.drinkless.td.libcore.telegram.TdApi.*;
import org.json.JSONException;
import ru.kpfu.itis.vkgram.model.VKChatMessageResponse;
import ru.kpfu.itis.vkgram.repository.RepositoryProvider;
import ru.kpfu.itis.vkgram.utils.Constants;

public class MessageServiceImpl implements MessageService {

    private VKRequestListener vkDialogsRequestListener = new VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            super.onComplete(response);
            try {
                RepositoryProvider.provideMessageRepository()
                        .dialogs(new VKChatMessageResponse(response.json));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private VKRequestListener vkChatRequestListener = new VKRequestListener() {
        @Override
        public void onComplete(final VKResponse response) {
            super.onComplete(response);
            try {
                RepositoryProvider.provideMessageRepository()
                        .messages(new VKChatMessageResponse(response.json));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private VKRequestListener vkSendMessageRequestListener = new VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            super.onComplete(response);
            RepositoryProvider.provideMessageRepository().sendMessage();
        }

        @Override
        public void onError(VKError error) {
            super.onError(error);
            Log.d("TAK", "error: " + error.apiError.toString());
            RepositoryProvider.provideMessageRepository().sendMessageError(error);
        }
    };

    private Client client = ServiceFactory.getTelegramService().getClient();

    @Override
    public void dialogs(int offset, int count) {
        vkDialogs(offset, count);
        tgDialogs(offset, count);
    }

    private void vkDialogs(int offset, int count){
        VKParameters vkParameters = VKParameters.from(VKApiConst.OFFSET, offset, VKApiConst.COUNT, count);
        VKRequest vkRequest = VKApi.messages().getDialogs(vkParameters);
        vkRequest.executeWithListener(vkDialogsRequestListener);
    }

    private void tgDialogs(int offset, int count){
        TdApi.GetChats getChats = new TdApi.GetChats();
        getChats.offsetChatId = 0;
        //TODO поправить offset order, чтобы грузились иногда новые сообщения
        getChats.offsetOrder = 9223372036854775807L - offset;
        getChats.limit = count;
        client.send(getChats, o ->{
            if (o instanceof Chats){
                for (long id: ((Chats) o).chatIds){
                    client.send(new TdApi.GetChat(id), o1 ->{
                        if (o1 instanceof Chat) RepositoryProvider.provideMessageRepository().dialogs((Chat) o1);
                    });
                }
            }
        });
    }

    @Override
    public void messages(int offset, int count, long chatId, boolean vk) {
        if (vk){
            vkMessages(offset, count, chatId);
        }
        else {
            tgMessages(offset, count, chatId);
        }
    }

    private void vkMessages(int offset, int count, long chatId){
        VKParameters vkParameters = VKParameters.from(VKApiConst.OFFSET, offset,
                VKApiConst.COUNT, count, VKApiConst.USER_ID, chatId);
        VKRequest vkRequest = new VKRequest(Constants.MESSAGES_GET_HISTORY, vkParameters);
        vkRequest.executeWithListener(vkChatRequestListener);
    }

    private void tgMessages(int offset, int count, long chatId){
        TdApi.GetChatHistory getChatHistory = new GetChatHistory(chatId, 0, -offset, count, false);
        client.send(getChatHistory, o ->{
            if (o instanceof Messages){
                if (((Messages) o).messages != null){
                    RepositoryProvider.provideMessageRepository().messages((Messages) o);
                }
            }
        });
    }

    @Override
    public void sendMessage(int peerId, String message){
        VKParameters vkParameters = VKParameters.from(VKApiConst.PEER_ID, peerId, VKApiConst.MESSAGE, message);
        VKRequest vkRequest = new VKRequest(Constants.MESSAGES_SEND, vkParameters);
        vkRequest.executeWithListener(vkSendMessageRequestListener);
    }

    @Override
    public void sendMessage(Message message) {
        TdApi.SendMessage sendMessage = new SendMessage();
        sendMessage.chatId = message.chatId;
        sendMessage.replyToMessageId = 0;
        sendMessage.disableNotification = false;
        sendMessage.fromBackground = false;
        sendMessage.inputMessageContent = new InputMessageText(((MessageText)message.content).text, true, true);
        client.send(sendMessage, object -> {
            if (object instanceof Message){
                RepositoryProvider.provideMessageRepository().sendMessage();
            }
        });
    }
}
