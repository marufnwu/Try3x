package com.try3x.uttam.Models.Response;

import com.try3x.uttam.Models.Apk;

public class AppUpdateResponse {
    public boolean error;
    public String error_description;
    public Apk apk;

    public AppUpdateResponse() {
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

    public Apk getApk() {
        return apk;
    }

    public void setApk(Apk apk) {
        this.apk = apk;
    }
}
