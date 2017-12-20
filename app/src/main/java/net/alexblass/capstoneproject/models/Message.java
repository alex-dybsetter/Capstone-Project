package net.alexblass.capstoneproject.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A Message class to store message data.
 */

public class Message implements Parcelable {
    private String mSender;
    private String mSentTo;
    private String mMessage;
    private String mDateTime;

    public Message(String sender, String sentTo, String message, String dateTime){
        this.mSender = sender;
        this.mSentTo = sentTo;
        this.mMessage = message;
        this.mDateTime = dateTime;
    }

    protected Message(Parcel in) {
        this.mSender = in.readString();
        this.mSentTo = in.readString();
        this.mMessage = in.readString();
        this.mDateTime = in.readString();
    }

    public String getSender() {
        return mSender;
    }

    public String getSentTo() {
        return mSentTo;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getDateTime() {
        return mDateTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mSender);
        parcel.writeString(mSentTo);
        parcel.writeString(mMessage);
        parcel.writeString(mDateTime);
    }

    // Creator for Parcelable implementation
    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
