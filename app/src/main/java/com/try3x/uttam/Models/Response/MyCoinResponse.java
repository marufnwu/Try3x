package com.try3x.uttam.Models.Response;

public class MyCoinResponse {
    public boolean error;
    public String error_description;
    public float coin;

    public MyCoinResponse() {
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }

    public float getCoin() {
        return coin;
    }

    public void setCoin(float coin) {
        this.coin = coin;
    }
}
