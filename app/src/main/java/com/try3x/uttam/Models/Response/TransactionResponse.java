package com.try3x.uttam.Models.Response;

import com.try3x.uttam.Models.Transaction;

import java.util.List;

public class TransactionResponse {
    public boolean error;
    public String error_description;
    public boolean isMore;
    public List<Transaction> transactions;
    public String currPage;
    public String totalItem;
    public int totalPage;
}
