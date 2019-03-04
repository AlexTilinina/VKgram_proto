package ru.kpfu.itis.vkgram.model;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;

public class VKChatMessageResponse implements Parcelable{

    private VKChatMessageResponseData mVKChatMessageResponseData;

    public VKChatMessageResponseData getData() {
        return mVKChatMessageResponseData;
    }

    public void setVKChatMessageResponseData(final VKChatMessageResponseData VKChatMessageResponseData) {
        mVKChatMessageResponseData = VKChatMessageResponseData;
    }

    public VKChatMessageResponse(JSONObject from) throws JSONException {
        parse(from);
    }

    public VKChatMessageResponse parse(JSONObject from) throws JSONException {
        mVKChatMessageResponseData = new VKChatMessageResponseData(from.getJSONObject("response"));
        return this;
    }

    public VKChatMessageResponse(Parcel in) {
        mVKChatMessageResponseData = in.readParcelable(VKChatMessageResponse.class.getClassLoader());
    }

    public static final Creator<VKChatMessageResponse> CREATOR = new Creator<VKChatMessageResponse>() {
        @Override
        public VKChatMessageResponse createFromParcel(Parcel in) {
            return new VKChatMessageResponse(in);
        }

        @Override
        public VKChatMessageResponse[] newArray(int size) {
            return new VKChatMessageResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(mVKChatMessageResponseData, i);
    }
}
