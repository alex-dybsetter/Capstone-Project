package net.alexblass.capstoneproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.GregorianCalendar;

/**
 * A Message class to store message data.
 */

public class Message implements Parcelable {
    private String mSender;
    private String mSentTo;
    private String mMessage;
    private String mDateTime;
    private boolean mReadFlag;

    public Message(String sender, String sentTo, String message, boolean isRead){
        this.mSender = sender;
        this.mSentTo = sentTo;
        this.mMessage = message;
        this.mDateTime = new GregorianCalendar().getTime().toString();
        this.mReadFlag = isRead;
    }

    protected Message(Parcel in) {
        this.mSender = in.readString();
        this.mSentTo = in.readString();
        this.mMessage = in.readString();
        this.mDateTime = in.readString();
        this.mReadFlag = in.readByte() != 0;
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

    public boolean isRead(){
        return mReadFlag;
    }

    public void setIsRead(boolean isRead){
        mReadFlag = isRead;
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
        parcel.writeByte((byte) (mReadFlag ? 1 : 0));
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
