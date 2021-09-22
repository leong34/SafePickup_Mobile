
package com.example.safepickup.Model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Data {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("credential")
    @Expose
    private String credential;
    @SerializedName("organization_id")
    @Expose
    private String organizationId;
    @SerializedName("emptyFaceId")
    @Expose
    private Boolean emptyFaceId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Boolean getEmptyFaceId() {
        return emptyFaceId;
    }

    public void setEmptyFaceId(Boolean emptyFaceId) {
        this.emptyFaceId = emptyFaceId;
    }

}
