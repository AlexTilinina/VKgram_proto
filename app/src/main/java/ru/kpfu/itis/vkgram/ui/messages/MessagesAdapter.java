package ru.kpfu.itis.vkgram.ui.messages;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.drinkless.td.libcore.telegram.TdApi.Chat;
import org.drinkless.td.libcore.telegram.TdApi.Message;
import ru.kpfu.itis.vkgram.model.VKChatMessage;
import ru.kpfu.itis.vkgram.ui.base.BaseAdapter;

public class MessagesAdapter extends BaseAdapter<Object, MessagesItemHolder> {

    private static final int TG_MESSAGE = 0;
    private static final int VK_MESSAGE = 1;

    public MessagesAdapter(@NonNull List<Object> items) {
        super(items);
        sort(items);
    }

    @Override
    public int getItemViewType(int position) {
        Object item = getItem(position);
        if (item instanceof VKChatMessage) {
            return VK_MESSAGE;
        }
        else return TG_MESSAGE;
    }

    @NonNull
    @Override
    public MessagesItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case TG_MESSAGE:
                return MessagesTGItemHolder.create(parent.getContext());
            case VK_MESSAGE:
                return MessagesVKItemHolder.create(parent.getContext());
        }
        return MessagesVKItemHolder.create(parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesItemHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Object item = getItem(position);
        holder.bind(item);
    }

    @Override
    public void addAll(@NonNull List<?> values) {
        super.addAll(values);
        sort(getItems());
        refreshRecycler();
    }

    private void sort(@NonNull List<Object> items){
        Collections.sort(items, (o1, o2) -> {
            if (o1 instanceof VKChatMessage && o2 instanceof VKChatMessage){
                long m1 = ((VKChatMessage) o1).date;
                long m2 = ((VKChatMessage) o2).date;
                if (m1 < m2){
                    return 1;
                } else {
                    if (m1 > m2){
                        return -1;
                    }
                }
                return 0;
            }
            if (o1 instanceof VKChatMessage && o2 instanceof Chat){
                Message m2 = ((Chat) o2).lastMessage;
                long m1 = ((VKChatMessage) o1).date;
                if (m2 != null){
                    if (m1 < m2.date){
                        return 1;
                    }
                    else if (m1 > m2.date){
                        return -1;
                    }
                }
                return 0;
            }
            if (o1 instanceof Chat && o2 instanceof VKChatMessage){
                Message m1 = ((Chat) o1).lastMessage;
                long m2 = ((VKChatMessage) o2).date;
                if (m1 != null){
                    if (m1.date < m2){
                        return 1;
                    }
                    else if (m1.date > m2){
                        return -1;
                    }
                }
                return 0;
            }
            if (o1 instanceof Chat && o2 instanceof Chat){
                Message m1 = ((Chat) o1).lastMessage;
                Message m2 = ((Chat) o2).lastMessage;
                if (m1 != null && m2 != null){
                    if (m1.date < m2.date){
                        return 1;
                    }
                    else if (m1.date > m2.date){
                        return -1;
                    }
                }
            }
            return 0;
        });
    }
}
