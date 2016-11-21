package com.yellowsoft.worldtechhackathon.models;

/**
 * Created by subhankar on 11/21/2016.
 */
import com.google.gson.annotations.SerializedName;

public class Result {
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SerializedName("success")
    private Boolean success;
    @SerializedName("message")
    private String message;
}