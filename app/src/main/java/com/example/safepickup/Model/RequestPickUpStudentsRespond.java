
package com.example.safepickup.Model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class RequestPickUpStudentsRespond {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("authorized")
    @Expose
    private Boolean authorized;
    @SerializedName("rekog_message")
    @Expose
    private String rekogMessage;
    @SerializedName("face_id_verified")
    @Expose
    private Boolean faceIdVerified;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getAuthorized() {
        return authorized;
    }

    public void setAuthorized(Boolean authorized) {
        this.authorized = authorized;
    }

    public String getRekogMessage() {
        return rekogMessage;
    }

    public void setRekogMessage(String rekogMessage) {
        this.rekogMessage = rekogMessage;
    }

    public Boolean getFaceIdVerified() {
        return faceIdVerified;
    }

    public void setFaceIdVerified(Boolean faceIdVerified) {
        this.faceIdVerified = faceIdVerified;
    }

}
