
package com.example.safepickup.Model;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class FetchNoticesListRespond {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("authorized")
    @Expose
    private Boolean authorized;
    @SerializedName("notices")
    @Expose
    private List<Notice> notices = null;

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

    public List<Notice> getNotices() {
        return notices;
    }

    public void setNotices(List<Notice> notices) {
        this.notices = notices;
    }

}
