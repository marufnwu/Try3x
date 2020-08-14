package com.try3x.uttam.Models;

import java.util.List;

public class PackageList {
    public boolean error;
    public List<Packages> packages;

    public PackageList() {
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<Packages> getPackages() {
        return packages;
    }

    public void setPackages(List<Packages> packages) {
        this.packages = packages;
    }
}
