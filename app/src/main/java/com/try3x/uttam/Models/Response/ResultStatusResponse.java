package com.try3x.uttam.Models.Response;

import com.try3x.uttam.Models.ResultStatus;

public class ResultStatusResponse {
    public boolean error;
    public String error_description;
    public ResultStatus resultStatus;

    public ResultStatusResponse() {
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

    public ResultStatus getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(ResultStatus resultStatus) {
        this.resultStatus = resultStatus;
    }
}
