package com.try3x.uttam.Models.Response;

import com.try3x.uttam.Models.Commission;

import java.util.List;

public class MyCommissionsResponse {
    public boolean error;
    public String error_description;
   public List<Commission> items;
    public int totalPage;

    public boolean isMore;


    public int currPage;

    public int totalItem;

    public MyCommissionsResponse() {
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

    public List<Commission> getItems() {
        return items;
    }

    public void setItems(List<Commission> items) {
        this.items = items;
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
}
