package com.try3x.uttam.Models.Response;

import com.try3x.uttam.Models.Result;

import java.util.List;

public class ResultListResponse {
    public boolean error;
    public String error_description;
    public List<Result> results;
}
