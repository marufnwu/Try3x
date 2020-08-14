package com.try3x.uttam.Models.Response;

import com.try3x.uttam.Models.PayMethodInfo;

public class SinglePayMethodResponse {
    public boolean error;
    public String error_description;
    public PayMethodInfo info;

    public SinglePayMethodResponse() {
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

    public PayMethodInfo getInfo() {
        return info;
    }

    public void setInfo(PayMethodInfo info) {
        this.info = info;
    }
}
