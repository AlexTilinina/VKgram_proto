package ru.kpfu.itis.vkgram.ui.messages;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.Calendar;
import java.util.GregorianCalendar;
import ru.kpfu.itis.vkgram.R;

public abstract class MessagesItemHolder<T> extends RecyclerView.ViewHolder {

    protected TextView tvName;
    protected TextView tvTime;
    protected TextView tvMessage;
    protected TextView tvUnread;
    protected CircleImageView ivPhoto;
    protected ImageView ivOnline;
    protected ImageView ivAppFrom;
    protected ImageView ivMuted;
    protected RelativeLayout mRelativeLayout;

    protected Context context;

    public MessagesItemHolder(View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tv_name);
        tvTime = itemView.findViewById(R.id.tv_time);
        tvMessage = itemView.findViewById(R.id.tv_message);
        tvUnread = itemView.findViewById(R.id.tv_unread);
        ivPhoto = itemView.findViewById(R.id.iv_photo);
        ivOnline = itemView.findViewById(R.id.iv_online);
        ivAppFrom = itemView.findViewById(R.id.iv_app_from);
        ivMuted = itemView.findViewById(R.id.iv_muted);
        mRelativeLayout = itemView.findViewById(R.id.relative_layout);
        context = itemView.getContext();
    }

    public abstract void bind(@NonNull T item);

    protected String getTime(long unixTime){
        Calendar date = new GregorianCalendar();
        date.setTimeInMillis(unixTime * 1000);
        Calendar today = new GregorianCalendar();
        String time = "";
        if (date.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
                && date.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && date.get(Calendar.YEAR) == today.get(Calendar.YEAR)){
            int minutes = date.get(Calendar.MINUTE);
            time = date.get(Calendar.HOUR_OF_DAY) + ":" + (minutes < 10 ?  "0" + minutes : minutes);
        }
        else {
            time = date.get(Calendar.DAY_OF_MONTH) + " " + getMonth(date.get(Calendar.MONTH));
            if (date.get(Calendar.YEAR) != today.get(Calendar.YEAR)){
                time += " " + date.get(Calendar.YEAR);
            }
        }
        return time;
    }

    private String getMonth(int monthNum){
        String[] months = context.getResources().getStringArray(R.array.months);
        return months[monthNum].substring(0,3);
    }
}
