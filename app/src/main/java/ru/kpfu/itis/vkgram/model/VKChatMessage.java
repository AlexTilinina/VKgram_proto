package ru.kpfu.itis.vkgram.model;

import android.os.Parcel;
import com.vk.sdk.api.model.Identifiable;
import com.vk.sdk.api.model.VKApiMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class VKChatMessage extends VKApiMessage implements Identifiable, android.os.Parcelable {

    private int chat_id;

    private int user_count;

    private int admin_id;

    private int unread_count = 0;

    private PushSettings push_settings;

    private String photo_50 = "http://vk.com/images/camera_c.gif";

    private String photo_100 = "http://vk.com/images/camera_b.gif";

    private String photo_200 = "http://vk.com/images/camera_a.gif";

    public int getAdminId() {
        return admin_id;
    }

    public void setAdminId(int admin_id) {
        this.admin_id = admin_id;
    }

    public int getChatId() {
        return chat_id;
    }

    public void setChatId(int chat_id) {
        this.chat_id = chat_id;
    }

    public int getUserCount() {
        return user_count;
    }

    public void setUserCount(int user_count) {
        this.user_count = user_count;
    }

    public PushSettings getPushSettings() {
        return push_settings;
    }

    public void setPushSettings(PushSettings push_settings) {
        this.push_settings = push_settings;
    }

    public String getPhoto_50() {
        return photo_50;
    }

    public void setPhoto_50(String photo_50) {
        this.photo_50 = photo_50;
    }

    public String getPhoto_100() {
        return photo_100;
    }

    public void setPhoto_100(String photo_100) {
        this.photo_100 = photo_100;
    }

    public String getPhoto_200() {
        return photo_200;
    }

    public void setPhoto_200(String photo_200) {
        this.photo_200 = photo_200;
    }

    public int getUnreadCount() {
        return unread_count;
    }

    public void setUnreadCount(final int unread_count) {
        this.unread_count = unread_count;
    }

    public VKChatMessage(JSONObject from) throws JSONException {
        parse(from);
    }

    public VKChatMessage parse(JSONObject source) throws JSONException {
        super.parse(source);
        chat_id = source.optInt("chat_id");
        admin_id = source.optInt("admin_id");
        user_count = source.optInt("user_count");
        photo_50 = source.optString("photo_50", photo_50);
        photo_100 = source.optString("photo_100", photo_100);
        photo_200 = source.optString("photo_200", photo_200);
        JSONObject pushSettings = source.optJSONObject("push_settings");
        if (pushSettings != null){
            push_settings = new PushSettings(pushSettings);
        }
        return this;
    }

    public VKChatMessage(Parcel in) {
        super(in);
        chat_id = in.readInt();
        user_count = in.readInt();
        admin_id = in.readInt();
        unread_count = in.readInt();
        push_settings = in.readParcelable(PushSettings.class.getClassLoader());
        photo_50 = in.readString();
        photo_100 = in.readString();
        photo_200 = in.readString();
    }

    public VKChatMessage() {
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.chat_id);
        dest.writeInt(this.user_count);
        dest.writeInt(this.admin_id);
        dest.writeInt(this.unread_count);
        dest.writeParcelable(this.push_settings, flags);
        dest.writeString(this.photo_50);
        dest.writeString(this.photo_100);
        dest.writeString(this.photo_200);
    }

    public static Creator<VKChatMessage> CREATOR = new Creator<VKChatMessage>() {

        public VKChatMessage createFromParcel(Parcel source) {
            return new VKChatMessage(source);
        }

        public VKChatMessage[] newArray(int size) {
            return new VKChatMessage[size];
        }
    };
}
