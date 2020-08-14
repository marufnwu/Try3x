package com.try3x.uttam.Models.Response;

import com.try3x.uttam.Models.PaymentInfo;

import java.util.List;

public class PaymentInfoResponse {
    public boolean error;
    public String error_description;
    public List<PaymentInfo> payment_info;

    public PaymentInfoResponse() {
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

    public List<PaymentInfo> getPayment_info() {
        return payment_info;
    }

    public void setPayment_info(List<PaymentInfo> payment_info) {
        this.payment_info = payment_info;
    }
}
