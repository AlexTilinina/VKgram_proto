package ru.kpfu.itis.vkgram.ui.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import ru.kpfu.itis.vkgram.R;
import ru.kpfu.itis.vkgram.model.VKChatMessage;

public class ChatMessageSentItemHolder extends ChatMessageItemHolder{

    @NonNull
    public static ChatMessageSentItemHolder create(@NonNull Context context) {
        View view = View.inflate(context, R.layout.item_chat_message_out, null);
        ChatMessageSentItemHolder holder = new ChatMessageSentItemHolder(view);
        return holder;
    }

    public ChatMessageSentItemHolder(View itemView) {
        super(itemView);
    }

    public void bind(Object message){
        super.bind(message);
    }
}
