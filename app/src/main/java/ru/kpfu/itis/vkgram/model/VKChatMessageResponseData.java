package ru.kpfu.itis.vkgram.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.vk.sdk.api.model.VKList;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VKChatMessageResponseData implements Parcelable{

    private int count;

    private int unread_dialogs;

    private VKList<VKChatMessage> items;

    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    public int getUnread_dialogs() {
        return unread_dialogs;
    }

    public void setUnread_dialogs(final int unread_dialogs) {
        this.unread_dialogs = unread_dialogs;
    }

    public VKList<VKChatMessage> getItems() {
        return items;
    }

    public void setItems(VKList<VKChatMessage> items) {
        this.items = items;
    }

    public VKChatMessageResponseData(JSONObject from) throws JSONException {
        parse(from);
    }

    public VKChatMessageResponseData parse(JSONObject from) throws JSONException {
        count = from.optInt("count");
        unread_dialogs = from.optInt("unread_dialogs");
        items = parseMessagesFromJson(from);
        return this;
    }

    private VKList<VKChatMessage> parseMessagesFromJson(JSONObject from) throws JSONException {
        JSONArray jsonArray = from.optJSONArray("items");
        ArrayList<VKChatMessage> chatList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                chatList.add(new VKChatMessage(jsonArray.getJSONObject(i).getJSONObject("message")));
                try {
                    chatList.get(i).setUnreadCount(jsonArray.getJSONObject(i).getInt("unread"));
                } catch (JSONException ignored){}
            } catch (JSONException e){
                chatList.add(new VKChatMessage(jsonArray.getJSONObject(i)));
            }
        }
        return new VKList<>(chatList);
    }

    public VKChatMessageResponseData(Parcel in) {
        count = in.readInt();
        unread_dialogs = in.readInt();
        items = in.readParcelable(VKList.class.getClassLoader());
    }

    public static final Creator<VKChatMessageResponseData> CREATOR = new Creator<VKChatMessageResponseData>() {
        @Override
        public VKChatMessageResponseData createFromParcel(Parcel in) {
            return new VKChatMessageResponseData(in);
        }

        @Override
        public VKChatMessageResponseData[] newArray(int size) {
            return new VKChatMessageResponseData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(count);
        parcel.writeInt(unread_dialogs);
        parcel.writeParcelable(items, i);
    }
}
