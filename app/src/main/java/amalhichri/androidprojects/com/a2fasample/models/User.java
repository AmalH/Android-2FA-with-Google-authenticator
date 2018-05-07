package amalhichri.androidprojects.com.a2fasample.models;

/**
 * Created by Amal on 14/11/2017.
 */

public class User {

    private String id,emailAddress,firstName,lastName,twoFactorAuthOn,phoneNumber,phoneCountryCode;  //fields to match with LinkedIn API
                                           // for facebook API we just read the json response object without switching it to a User object

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }



    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }



    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTwoFactorAuthOn() {
        return twoFactorAuthOn;
    }

    public void setTwoFactorAuthOn(String twoFactorAuthOn) {
        this.twoFactorAuthOn = twoFactorAuthOn;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPhoneCountryCode() {
        return phoneCountryCode;
    }

    public void setPhoneCountryCode(String phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode;
    }

    // just for test

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
