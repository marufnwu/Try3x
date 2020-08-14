package com.try3x.uttam.Models.Response;

import com.try3x.uttam.Models.PayMethodInfo;
import com.try3x.uttam.Models.PaymentInfo;

import java.util.List;

public class UserPayMethodListResponse {
    public boolean error;
    public String error_description;
    public List<PayMethodInfo> methodList;

    public UserPayMethodListResponse() {
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

    public List<PayMethodInfo> getMethodList() {
        return methodList;
    }

    public void setMethodList(List<PayMethodInfo> methodList) {
        this.methodList = methodList;
    }
}
