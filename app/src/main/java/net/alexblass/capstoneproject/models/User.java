package net.alexblass.capstoneproject.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A model to store a user's data.
 */

    // TODO: Add list of friends
    // TODO: Add relationship user(s)

public class User implements Parcelable {
    private String mEmail;
    private String mName;
    private String mZipcode;
    private String mGender;
    private String mSexuality;
    private String mRelationshipStatus;
    private String mDescription;

    public User(String email, String name, String zipcode, String gender, String sexuality,
                String relationshipStatus, String description){
        this.mEmail = email;
        this.mName = name;
        this.mZipcode = zipcode;
        this.mGender = gender;
        this.mSexuality = sexuality;
        this.mRelationshipStatus = relationshipStatus;
        this.mDescription = description;
    }

    protected User(Parcel in) {
        this.mEmail = in.readString();
        this.mName = in.readString();
        this.mZipcode = in.readString();
        this.mGender = in.readString();
        this.mSexuality = in.readString();
        this.mRelationshipStatus = in.readString();
        this.mDescription = in.readString();
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getZipcode() {
        return mZipcode;
    }

    public void setZipcode(String zipcode) {
        this.mZipcode = zipcode;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String gender) {
        this.mGender = gender;
    }

    public String getSexuality() {
        return mSexuality;
    }

    public void setSexuality(String sexuality) {
        this.mSexuality = sexuality;
    }

    public String getRelationshipStatus() {
        return mRelationshipStatus;
    }

    public void setRelationshipStatus(String relationshipStatus) {
        this.mRelationshipStatus = relationshipStatus;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mEmail);
        parcel.writeString(mName);
        parcel.writeString(mZipcode);
        parcel.writeString(mGender);
        parcel.writeString(mSexuality);
        parcel.writeString(mRelationshipStatus);
        parcel.writeString(mDescription);
    }

    // Creator for Parcelable implementation
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
