package com.try3x.uttam.Models.Response;

public class PayoutReqResponse {
    public boolean error,toMyCoin;
    public String error_description, dayForWait;

    public PayoutReqResponse() {
    }

    public String getDayForWait() {
        return dayForWait;
    }

    public void setDayForWait(String dayForWait) {
        this.dayForWait = dayForWait;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isToMyCoin() {
        return toMyCoin;
    }

    public void setToMyCoin(boolean toMyCoin) {
        this.toMyCoin = toMyCoin;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }
}
