package com.try3x.uttam.Models.Response;

import com.try3x.uttam.Models.CoinPackage;

import java.util.List;

public class CoinPackageResponse {
    public boolean error;
    public String error_description;
    public List<CoinPackage> packs;

    public CoinPackageResponse() {
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

    public List<CoinPackage> getPacks() {
        return packs;
    }

    public void setPacks(List<CoinPackage> packs) {
        this.packs = packs;
    }
}
