package com.try3x.uttam.Models.Response;

public class BajiInfoResponse {
    public boolean error;
    public String error_description;
    public int todayBaji, todayWin, totalBaji, totalWin;

    public BajiInfoResponse() {
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

    public int getTodayBaji() {
        return todayBaji;
    }

    public void setTodayBaji(int todayBaji) {
        this.todayBaji = todayBaji;
    }

    public int getTodayWin() {
        return todayWin;
    }

    public void setTodayWin(int todayWin) {
        this.todayWin = todayWin;
    }

    public int getTotalBaji() {
        return totalBaji;
    }

    public void setTotalBaji(int totalBaji) {
        this.totalBaji = totalBaji;
    }

    public int getTotalWin() {
        return totalWin;
    }

    public void setTotalWin(int totalWin) {
        this.totalWin = totalWin;
    }
}
