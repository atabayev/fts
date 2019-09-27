package kz.ftsystem.yel.fts.backend.connection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayResponse {

    @SerializedName("response")
    @Expose
    private String response;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("reasonCode")
    @Expose
    private String result;

    @SerializedName("md")
    @Expose
    private String md;

    @SerializedName("paReq")
    @Expose
    private String paReq;

    @SerializedName("acsUrl")
    @Expose
    private String  acsUrl;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMd() {
        return md;
    }

    public void setMd(String md) {
        this.md = md;
    }

    public String getPaReq() {
        return paReq;
    }

    public void setPaReq(String paReq) {
        this.paReq = paReq;
    }

    public String getAcsUrl() {
        return acsUrl;
    }

    public void setAcsUrl(String acsUrl) {
        this.acsUrl = acsUrl;
    }
}


