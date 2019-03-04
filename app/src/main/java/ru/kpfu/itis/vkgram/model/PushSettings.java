package ru.kpfu.itis.vkgram.model;

import android.os.Parcel;
import org.json.JSONObject;

class PushSettings implements android.os.Parcelable{

    private int sound;

    private long disabled_until;

    public int getSound() {
        return sound;
    }

    public void setSound(int sound) {
        this.sound = sound;
    }

    public long getDisabledUntil() {
        return disabled_until;
    }

    public void setDisabledUntil(long disabled_until) {
        this.disabled_until = disabled_until;
    }

    public PushSettings(JSONObject from){
        parse(from);
    }

    public PushSettings parse(JSONObject from){
        sound = from.optInt("sound");
        disabled_until = from.optLong("disabled_until");
        return this;
    }

    public PushSettings(Parcel in) {
        sound = in.readInt();
        disabled_until = in.readLong();
    }

    public static final Creator<PushSettings> CREATOR = new Creator<PushSettings>() {
        @Override
        public PushSettings createFromParcel(Parcel in) {
            return new PushSettings(in);
        }

        @Override
        public PushSettings[] newArray(int size) {
            return new PushSettings[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(sound);
        parcel.writeLong(disabled_until);
    }
}
