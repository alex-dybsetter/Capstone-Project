package net.alexblass.capstoneproject.models;

/**
 * A model to store a user's data.
 */

    // TODO: Add list of friends
    // TODO: Add relationship user(s)

public class User {
    private String mName;
    private String mZipcode;
    private String mGender;
    private String mSexuality;
    private String mRelationshipStatus;
    private String mDescription;

    public User(String name, String zipcode, String gender, String sexuality,
                String relationshipStatus, String description){
        this.mName = name;
        this.mZipcode = zipcode;
        this.mGender = gender;
        this.mSexuality = sexuality;
        this.mRelationshipStatus = relationshipStatus;
        this.mDescription = description;
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
}
