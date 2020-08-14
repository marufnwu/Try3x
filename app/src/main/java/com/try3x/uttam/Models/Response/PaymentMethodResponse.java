package com.try3x.uttam.Models.Response;

import com.try3x.uttam.Models.PaymentMethod;

import java.util.List;

public class PaymentMethodResponse {
    public boolean error;
    public String error_description;
    public List<PaymentMethod> payMethodList;

    public PaymentMethodResponse() {
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

    public List<PaymentMethod> getPayMethodList() {
        return payMethodList;
    }

    public void setPayMethodList(List<PaymentMethod> payMethodList) {
        this.payMethodList = payMethodList;
    }
}
