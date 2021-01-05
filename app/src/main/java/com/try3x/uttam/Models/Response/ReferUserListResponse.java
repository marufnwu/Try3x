package com.try3x.uttam.Models.Response;

import com.try3x.uttam.Models.ReferUser;

import java.util.List;

public class ReferUserListResponse {
    public boolean error;
    public String error_description;
    public List<ReferUser> users;
}
