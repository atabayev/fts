package kz.ftsystem.yel.fts.backend.connection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PKResponse {

    @SerializedName("response")
    @Expose
    private String response;
    @SerializedName("id")
    @Expose
    private String key;


    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String response) {
        this.key = key;
    }
}

