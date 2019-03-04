package ru.kpfu.itis.vkgram.ui.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.vk.sdk.api.*;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;
import de.hdodenhof.circleimageview.CircleImageView;
import org.drinkless.td.libcore.telegram.TdApi;
import org.drinkless.td.libcore.telegram.TdApi.Message;
import org.drinkless.td.libcore.telegram.TdApi.User;
import ru.kpfu.itis.vkgram.R;
import ru.kpfu.itis.vkgram.api.ServiceFactory;
import ru.kpfu.itis.vkgram.model.VKChatMessage;
import ru.kpfu.itis.vkgram.utils.ImageLoadHelper;

public class ChatMessageReceivedItemHolder extends ChatMessageItemHolder {

    private CircleImageView ivPhoto;
    private ImageView ivOnline;
    private TextView tvName;

    @NonNull
    public static ChatMessageReceivedItemHolder create(@NonNull Context context) {
        View view = View.inflate(context, R.layout.item_chat_message_in, null);
        ChatMessageReceivedItemHolder holder = new ChatMessageReceivedItemHolder(view);
        return holder;
    }

    public ChatMessageReceivedItemHolder(View itemView) {
        super(itemView);
        ivPhoto = itemView.findViewById(R.id.iv_photo);
        ivOnline = itemView.findViewById(R.id.iv_online);
        tvName = itemView.findViewById(R.id.tv_name);
    }

    public void bind(Object message){
        super.bind(message);
        if (message instanceof VKChatMessage){
            setUserProperties((VKChatMessage) message);
        } else {
            if (message instanceof Message){
                setUserProperties((Message) message);
            }
        }
    }

    private void setUserProperties(VKChatMessage item){
        int userId = item.user_id;
        VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS,
                userId, VKApiConst.FIELDS, VKApiUser.FIELD_PHOTO_100 + "," + VKApiUser.FIELD_ONLINE +
                        ", " + VKApiUser.FIELD_ONLINE_MOBILE));
        request.executeWithListener(new VKRequestListener() {
            @Override
            public void onComplete(final VKResponse response) {
                super.onComplete(response);
                VKList<VKApiUser> vkApiUsers = new VKList<>(response.json, VKApiUser.class);
                VKApiUser user = vkApiUsers.get(0);
                ImageLoadHelper.loadPicture(ivPhoto, user.photo_100);
                if (user.online || user.online_mobile){
                    ImageLoadHelper.loadPictureByDrawable(ivOnline, R.drawable.ic_online);
                    ImageLoadHelper.loadBackgroundByDrawable(ivOnline, R.drawable.ic_online_background);
                }
                tvName.setText(user.toString());
            }
        });
    }

    private void setUserProperties(Message item){
        int userId = item.senderUserId;
        TdApi.GetUser getUser = new TdApi.GetUser(userId);
        ServiceFactory.getTelegramService().getClient().send(getUser, object -> {
            if (object instanceof User){
                User user = (User) object;
                String name = user.firstName + " " + user.lastName;
                tvName.setText(name);
            }
        });
    }
}
