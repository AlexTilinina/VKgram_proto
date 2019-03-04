package ru.kpfu.itis.vkgram.ui.messages;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import org.drinkless.td.libcore.telegram.TdApi.Chat;
import org.drinkless.td.libcore.telegram.TdApi.Message;
import org.drinkless.td.libcore.telegram.TdApi.MessageAnimation;
import org.drinkless.td.libcore.telegram.TdApi.MessageAudio;
import org.drinkless.td.libcore.telegram.TdApi.MessageContent;
import org.drinkless.td.libcore.telegram.TdApi.MessageDocument;
import org.drinkless.td.libcore.telegram.TdApi.MessageExpiredPhoto;
import org.drinkless.td.libcore.telegram.TdApi.MessagePhoto;
import org.drinkless.td.libcore.telegram.TdApi.MessageText;
import ru.kpfu.itis.vkgram.R;
import ru.kpfu.itis.vkgram.utils.ImageLoadHelper;

public class MessagesTGItemHolder extends MessagesItemHolder<Chat> {

    public static MessagesTGItemHolder create(Context context) {
        View view = View.inflate(context, R.layout.item_message, null);
        return new MessagesTGItemHolder(view);
    }

    public MessagesTGItemHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(@NonNull Chat item) {
        setTitle(item);
        setTime(item);
        setLastMessage(item);
        setUnreadCount(item);
        setReadState(item);
        ivAppFrom.setImageResource(R.drawable.ic_tg);
        setPhoto(item);
    }

    private void setPhoto(Chat item) {
        if (item.photo != null){
            ImageLoadHelper.loadPicture(ivPhoto, item.photo.small.local.path);
        }
    }

    private void setLastMessage(Chat item) {
        Message lastMessage = item.lastMessage;
        if (lastMessage != null){
            MessageContent content = item.lastMessage.content;
            if (content instanceof MessageText){
                tvMessage.setText(((MessageText) content).text.text);
            } else {
                tvMessage.setText(content.getClass().getSimpleName());
            }
        }
    }

    private void setTitle(Chat item){
        tvName.setText(item.title);
    }

    private void setTime(Chat item){
        if (item.lastMessage != null){
            tvTime.setText(getTime(item.lastMessage.date));
        }
    }

    private void setUnreadCount(Chat item){
        if (item.unreadCount != 0) {
            tvUnread.setText(String.valueOf(item.unreadCount));
            tvUnread.setVisibility(View.VISIBLE);
        } else {
            tvUnread.setText("");
            tvUnread.setVisibility(View.GONE);
        }
    }

    private void setReadState(Chat item){
        if (item.lastMessage != null){
            if (item.unreadCount != 0){
                mRelativeLayout.setBackgroundResource(R.color.unreadMessage);
                tvMessage.setBackgroundResource(R.color.unreadMessage);
            } else {
              if (item.lastMessage.id != item.lastReadOutboxMessageId){
                  tvMessage.setBackgroundResource(R.color.unreadMessage);
              }
              else {
                  mRelativeLayout.setBackgroundResource(R.color.defaultBackgroundColor);
                  tvMessage.setBackgroundResource(R.color.defaultBackgroundColor);
              }
            }
        }
        else {
            mRelativeLayout.setBackgroundResource(R.color.defaultBackgroundColor);
            tvMessage.setBackgroundResource(R.color.defaultBackgroundColor);
        }
    }
}
