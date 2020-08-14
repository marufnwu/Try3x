package com.try3x.uttam.Models.Paytm;

import com.google.gson.annotations.SerializedName;

public class Checksum {
    @SerializedName("CHECKSUMHASH")
    private String checksumHash;

    @SerializedName("ORDER_ID")
    private String orderId;



    public Checksum(String checksumHash, String orderId) {
        this.checksumHash = checksumHash;
        this.orderId = orderId;

    }

    public String getChecksumHash() {
        return checksumHash;
    }

    public String getOrderId() {
        return orderId;
    }

}
