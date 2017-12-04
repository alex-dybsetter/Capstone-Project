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
    private long mBirthday;
    private String mZipcode;
    private long mGenderCode;
    private String mSexuality;
    private String mRelationshipStatus;
    private String mDescription;
    private String mProfilePicUri;

    public User(String email, String name, long birthday){
        this.mEmail = email;
        this.mName = name;
        this.mBirthday = birthday;
        this.mProfilePicUri = "";
    }

    public User(String email, String name, long birthday, String zipcode, long genderCode,
                String sexuality, String relationshipStatus, String description, String profilePicUri){
        this.mEmail = email;
        this.mName = name;
        this.mBirthday = birthday;
        this.mZipcode = zipcode;
        this.mGenderCode = genderCode;
        this.mSexuality = sexuality;
        this.mRelationshipStatus = relationshipStatus;
        this.mDescription = description;
        this.mProfilePicUri = profilePicUri;
    }

    protected User(Parcel in) {
        this.mEmail = in.readString();
        this.mName = in.readString();
        this.mBirthday = in.readLong();
        this.mZipcode = in.readString();
        this.mGenderCode = in.readLong();
        this.mSexuality = in.readString();
        this.mRelationshipStatus = in.readString();
        this.mDescription = in.readString();
        this.mProfilePicUri = in.readString();
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

    public long getBirthday() { return mBirthday; }

    public void setBirthday(long birthday) { this.mBirthday = birthday; }

    public String getZipcode() {
        return mZipcode;
    }

    public void setZipcode(String zipcode) {
        this.mZipcode = zipcode;
    }

    public long getGenderCode() {
        return mGenderCode;
    }

    public void setGenderCode(int gender) {
        this.mGenderCode = gender;
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

    public String getProfilePicUri() {
        return mProfilePicUri;
    }

    public void setProfilePicUri(String pictureUri) {
        this.mProfilePicUri = pictureUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mEmail);
        parcel.writeString(mName);
        parcel.writeLong(mBirthday);
        parcel.writeString(mZipcode);
        parcel.writeLong(mGenderCode);
        parcel.writeString(mSexuality);
        parcel.writeString(mRelationshipStatus);
        parcel.writeString(mDescription);
        parcel.writeString(mProfilePicUri);
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
