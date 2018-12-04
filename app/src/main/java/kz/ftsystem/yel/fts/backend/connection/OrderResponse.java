package kz.ftsystem.yel.fts.backend.connection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderResponse {
    @SerializedName("response")
    @Expose
    private String response;
    @SerializedName("orderId")
    @Expose
    private String orderId;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("pagesCount")
    @Expose
    private String pagesCount;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("dateEnd")
    @Expose
    private String dateEnd;
    @SerializedName("urgency")
    @Expose
    private String urgency;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPagesCount() {
        return pagesCount;
    }

    public void setPages_count(String pages_count) {
        this.pagesCount = pages_count;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDate_end(String date_end) {
        this.dateEnd = date_end;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }
}
