
package com.example.safepickup.Model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class FetchUserDetailRespond {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("authorized")
    @Expose
    private Boolean authorized;
    @SerializedName("user_inner_id")
    @Expose
    private String userInnerId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("tel_num")
    @Expose
    private String telNum;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getAuthorized() {
        return authorized;
    }

    public void setAuthorized(Boolean authorized) {
        this.authorized = authorized;
    }

    public String getUserInnerId() {
        return userInnerId;
    }

    public void setUserInnerId(String userInnerId) {
        this.userInnerId = userInnerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getTelNum() {
        return telNum;
    }

    public void setTelNum(String telNum) {
        this.telNum = telNum;
    }

}
