package ru.kpfu.itis.vkgram.ui.chat;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ViewGroup;
import java.util.List;
import org.drinkless.td.libcore.telegram.TdApi.Message;
import ru.kpfu.itis.vkgram.model.VKChatMessage;
import ru.kpfu.itis.vkgram.ui.base.BaseAdapter;

public class ChatMessageAdapter extends BaseAdapter<Object, ChatMessageItemHolder> {

    private final int TYPE_SENT = 1;
    private final int TYPE_RECEIVED = 0;

    public ChatMessageAdapter(@NonNull List<Object> items) {
        super(items);
    }

    @Override
    public int getItemViewType(int position) {
        Object item = getItem(position);
        if (item instanceof VKChatMessage){
            if (((VKChatMessage)item).out){
                return TYPE_SENT;
            } else return TYPE_RECEIVED;
        } else {
            if (item instanceof Message){
                if (((Message) item).isOutgoing){
                    return TYPE_SENT;
                }
            }
            return TYPE_RECEIVED;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageItemHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Object item = getItem(position);
        holder.bind(item);
    }

    @NonNull
    @Override
    public ChatMessageItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_RECEIVED:
                return ChatMessageReceivedItemHolder.create(parent.getContext());
            case TYPE_SENT:
                return ChatMessageSentItemHolder.create(parent.getContext());
            default:
                return ChatMessageSentItemHolder.create(parent.getContext());
        }
    }
}
