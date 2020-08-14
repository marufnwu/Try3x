package com.try3x.uttam.Models.Response;

import com.try3x.uttam.Models.PayoutHistory;

import java.util.List;

public class PayoutHistoryResponse {
    private int currPage;

    private int totalItem;

    private String error_description;

    private int totalPage;

    private boolean isMore;

    private boolean error;

    private List<PayoutHistory> items;

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public int getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<PayoutHistory> getItems() {
        return items;
    }

    public void setItems(List<PayoutHistory> items) {
        this.items = items;
    }
}
