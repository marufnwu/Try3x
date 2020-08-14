package com.try3x.uttam.Models;

public class Commission {
    public String date, time, coinByUserName, coin_by, comment;
    public int trans_type;
    public float amount;

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Commission() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCoinByUserName() {
        return coinByUserName;
    }

    public void setCoinByUserName(String coinByUserName) {
        this.coinByUserName = coinByUserName;
    }

    public String getCoin_by() {
        return coin_by;
    }

    public void setCoin_by(String coin_by) {
        this.coin_by = coin_by;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getTrans_type() {
        return trans_type;
    }

    public void setTrans_type(int trans_type) {
        this.trans_type = trans_type;
    }
}
