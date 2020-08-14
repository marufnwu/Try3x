package com.try3x.uttam.Models;

public class UserLogin {
    public boolean error, accountExits;
    public String error_description;
    public User user;

    public UserLogin() {
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isAccountExits() {
        return accountExits;
    }

    public void setAccountExits(boolean accountExits) {
        this.accountExits = accountExits;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }
}
