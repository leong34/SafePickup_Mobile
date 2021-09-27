
package com.example.safepickup.Model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class FetchOrganizationAddressRespond {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("authorized")
    @Expose
    private Boolean authorized;
    @SerializedName("full_address")
    @Expose
    private String fullAddress;

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

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

}
