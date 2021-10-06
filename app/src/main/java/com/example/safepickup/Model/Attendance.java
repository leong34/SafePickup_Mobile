package com.example.safepickup.Model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Attendance {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("check_in_time")
    @Expose
    private String checkInTime;
    @SerializedName("check_out_time")
    @Expose
    private String checkOutTime;
    @SerializedName("guardian_id")
    @Expose
    private String guardianId;
    @SerializedName("pick_up_by")
    @Expose
    private String pickUpBy;
    @SerializedName("pick_up_internal_id")
    @Expose
    private String pickUpInternalId;
    @SerializedName("request_time")
    @Expose
    private String requestTime;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getGuardianId() {
        return guardianId;
    }

    public void setGuardianId(String guardianId) {
        this.guardianId = guardianId;
    }

    public String getPickUpBy() {
        return pickUpBy;
    }

    public void setPickUpBy(String pickUpBy) {
        this.pickUpBy = pickUpBy;
    }

    public String getPickUpInternalId() {
        return pickUpInternalId;
    }

    public void setPickUpInternalId(String pickUpInternalId) {
        this.pickUpInternalId = pickUpInternalId;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

}