package com.imageupload;

import com.google.gson.annotations.SerializedName;

/**
 * Created by md yousuf on 4/2/2017.
 */

public class ServerResponse {

    // variable name should be same as in the json response from php    @SerializedName("success")
    boolean success;
    @SerializedName("message")
    String message;

    String getMessage() {
        return message;
    }

    boolean getSuccess() {
        return success;
    }
}