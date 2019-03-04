package ru.kpfu.itis.vkgram.ui.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.drinkless.td.libcore.telegram.TdApi.Message;
import org.drinkless.td.libcore.telegram.TdApi.MessageContent;
import org.drinkless.td.libcore.telegram.TdApi.MessageText;
import ru.kpfu.itis.vkgram.R;
import ru.kpfu.itis.vkgram.model.VKChatMessage;

public class ChatMessageItemHolder extends RecyclerView.ViewHolder {

    protected TextView tvMessage;
    protected TextView tvTime;
    protected RelativeLayout rlMessage;
    //TODO attachments

    protected Context context;

    public ChatMessageItemHolder(View itemView) {
        super(itemView);
        tvMessage = itemView.findViewById(R.id.tv_message);
        tvTime = itemView.findViewById(R.id.tv_time);
        rlMessage = itemView.findViewById(R.id.rl_message);
        this.context = itemView.getContext();
    }

    public void bind(Object message){
        setMessageText(message);
        setTime(message);
        setReadState(message);
    }

    public void setSending(){
        tvTime.setText(context.getText(R.string.sending));
    }

    public void setCurrentTime(){
        GregorianCalendar calendar = new GregorianCalendar();
        tvTime.setText(getTime(calendar.getTimeInMillis()/1000));
    }

    private void setMessageText(Object item){
        String text = "";
        if (item instanceof VKChatMessage){
            text += ((VKChatMessage)item).body;
        } else {
            if (item instanceof Message){
                if (((Message) item).content instanceof MessageText){
                    text += ((MessageText) ((Message) item).content).text.text;
                } else {
                    text += ((Message) item).content.getClass().getSimpleName();
                }
            }
        }
        tvMessage.setText(text);

        /*if (!item.attachments.isEmpty()) {
            //TODO ССЫлка на вложения
            text += item.attachments.get(0).getType();
            tvMessage.setText(text);
            tvMessage.setTextColor(context.getResources().getColor(R.color.vk_color));
        } else {
            if (!item.fwd_messages.isEmpty()){
                text += item.fwd_messages.size() + " forwarded messages";
                tvMessage.setText(text);
                tvMessage.setTextColor(context.getResources().getColor(R.color.vk_color));
            }
        }*/
    }

    private void setReadState(Object item){
        //TODO выделение непрочитанных сообщений
        if (item instanceof VKChatMessage){
            if (!((VKChatMessage)item).read_state){
                rlMessage.setBackgroundResource(R.color.unreadMessage);
            }
            else {
                rlMessage.setBackgroundResource(0);
            }
        } else {
            if (item instanceof Message){
                if (((Message) item).containsUnreadMention){
                    rlMessage.setBackgroundResource(R.color.unreadMessage);
                }
                else {
                    rlMessage.setBackgroundResource(0);
                }
            }

        }

    }

    private void setTime(Object item){
        if (item instanceof VKChatMessage){
            if (((VKChatMessage)item).date != 0){
                tvTime.setText(getTime(((VKChatMessage)item).date));
            }
            else {
                tvTime.setText(context.getText(R.string.sending));
            }
        }
        else {
            if (item instanceof Message){
                if (((Message) item).date != 0){
                    tvTime.setText(getTime(((Message) item).date));
                }
                else {
                    tvTime.setText(context.getText(R.string.sending));
                }
            }
        }

    }

    private String getTime(long dateInMillis){
        Calendar date = new GregorianCalendar();
        date.setTimeInMillis(dateInMillis * 1000);
        Calendar today = new GregorianCalendar();
        String messageDate = "";

        int minutes = date.get(Calendar.MINUTE);
        String time = date.get(Calendar.HOUR_OF_DAY) + ":" + (minutes < 10 ?  "0" + minutes : minutes);

        if (date.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
                && date.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && date.get(Calendar.YEAR) == today.get(Calendar.YEAR)){
            messageDate = time;
        }
        else {
            messageDate = date.get(Calendar.DAY_OF_MONTH) + " " + getMonth(date.get(Calendar.MONTH));
            if (date.get(Calendar.YEAR) != today.get(Calendar.YEAR)){
                messageDate += " " + date.get(Calendar.YEAR);
            }
            messageDate += ", " + time;
        }
        return messageDate;
    }

    private String getMonth(int monthNum){
        String[] months = context.getResources().getStringArray(R.array.months);
        return months[monthNum].substring(0,3);
    }
}
