package ru.kpfu.itis.vkgram.ui.messages;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.*;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;
import ru.kpfu.itis.vkgram.R;
import ru.kpfu.itis.vkgram.model.VKChatMessage;
import ru.kpfu.itis.vkgram.utils.ImageLoadHelper;

public class MessagesVKItemHolder extends MessagesItemHolder<VKChatMessage> {

    @NonNull
    public static MessagesVKItemHolder create(@NonNull Context context) {
        View view = View.inflate(context, R.layout.item_message, null);
        return new MessagesVKItemHolder(view);
    }

    private MessagesVKItemHolder(View itemView){
        super(itemView);
    }

    @Override
    public void bind(@NonNull VKChatMessage item) {
        setTitle(item);
        setMessageText(item);
        setUnreadCount(item);
        setTime(item);
        setUserProperties(item);
        ivAppFrom.setImageResource(R.drawable.ic_ab_app);
        //TODO получение muted беседа или нет
        setReadState(item);
    }

    private void setTitle(VKChatMessage item){
        if (!item.title.isEmpty()) {
            tvName.setText(item.title);
        }
    }

    private void setMessageText(VKChatMessage item){
        String text = "";
        if (item.out){
            text += "You: ";
        }
        if (!item.body.isEmpty()){
            text += item.body;
            tvMessage.setText(text);
            tvMessage.setTextColor(context.getResources().getColor(R.color.defaultTextColor));
        } else {
            if (!item.attachments.isEmpty()) {
                //TODO перевод типа вложения
                text += item.attachments.get(0).getType();
                tvMessage.setText(text);
                tvMessage.setTextColor(context.getResources().getColor(R.color.vk_color));
            } else {
                if (!item.fwd_messages.isEmpty()){
                    text += item.fwd_messages.size() + " forwarded messages";
                    tvMessage.setText(text);
                    tvMessage.setTextColor(context.getResources().getColor(R.color.vk_color));
                }
            }
        }
    }

    private void setUnreadCount(VKChatMessage item){
        if (item.getUnreadCount() != 0) {
            tvUnread.setText(String.valueOf(item.getUnreadCount()));
            tvUnread.setVisibility(View.VISIBLE);
        } else {
            tvUnread.setText("");
            tvUnread.setVisibility(View.GONE);
        }
    }

    private void setUserProperties(VKChatMessage item){
        int userId = item.user_id;
        if (item.getChatId() == 0) {
            if (VKAccessToken.currentToken() != null){
                VKRequest request;
                if (userId > 0){
                    request = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS,
                            userId, VKApiConst.FIELDS, VKApiUser.FIELD_PHOTO_100 + "," + VKApiUser.FIELD_ONLINE +
                                    ", " + VKApiUser.FIELD_ONLINE_MOBILE));
                    request.executeWithListener(new VKRequestListener() {
                        @Override
                        public void onComplete(final VKResponse response) {
                            super.onComplete(response);
                            VKList<VKApiUser> vkApiUsers = new VKList<>(response.json, VKApiUser.class);
                            VKApiUser user = vkApiUsers.get(0);
                            ImageLoadHelper.loadPicture(ivPhoto, user.photo_100);
                            //TODO online(парс)
                            if (user.online || user.online_mobile){
                                ImageLoadHelper.loadPictureByDrawable(ivOnline, R.drawable.ic_online);
                                ImageLoadHelper.loadBackgroundByDrawable(ivOnline, R.drawable.ic_online_background);
                            }
                            if (item.title.isEmpty()){
                                tvName.setText(user.toString());
                                item.title = user.toString();
                            }
                        }
                    });
                } else {
                    request = VKApi.groups().getById(VKParameters.from(VKApiConst.GROUP_ID, Math.abs(userId)));
                    request.executeWithListener(new VKRequestListener(){
                        @Override
                        public void onComplete(final VKResponse response) {
                            super.onComplete(response);
                            VKList<VKApiCommunity> vkApiCommunities = new VKList<>(response.json,
                                    VKApiCommunity.class);
                            VKApiCommunity community = vkApiCommunities.get(0);
                            ImageLoadHelper.loadPicture(ivPhoto, community.photo_100);
                            if (item.title.isEmpty()){
                                tvName.setText(community.toString());
                                item.title = community.toString();
                            }
                            if (item.getChatId() != 0 && !item.out){
                                String text = community.name + ": " + tvMessage.getText();
                                tvMessage.setText(text);
                            }
                        }
                    });
                }
            }
        }
        else {
            if (VKAccessToken.currentToken() != null){
                VKRequest request = VKApi.users()
                        .get(VKParameters.from(VKApiConst.USER_IDS, userId));
                request.executeWithListener(new VKRequestListener() {
                    @Override
                    public void onComplete(final VKResponse response) {
                        super.onComplete(response);
                        VKList<VKApiUser> vkApiUsers = new VKList<>(response.json, VKApiUser.class);
                        VKApiUser user = vkApiUsers.get(0);
                        ImageLoadHelper.loadPicture(ivPhoto, item.getPhoto_100());
                        if (!item.out){
                            String text = user.first_name + ": " + tvMessage.getText();
                            tvMessage.setText(text);
                        }
                    }
                });
            }
        }
    }

    private void setTime(VKChatMessage item){
        tvTime.setText(getTime(item.date));
    }

    private void setReadState(VKChatMessage item){
        if (!item.read_state) {
            if (!item.out) {
                mRelativeLayout.setBackgroundResource(R.color.unreadMessage);
            }
            tvMessage.setBackgroundResource(R.color.unreadMessage);
        } else {
            mRelativeLayout.setBackgroundResource(R.color.defaultBackgroundColor);
            tvMessage.setBackgroundResource(R.color.defaultBackgroundColor);
        }
    }
}
