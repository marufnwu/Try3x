package com.try3x.uttam.Models;

public class PayMethodInfo {
    public int id, pay_method_id;
    public String user_email, pay_method_name, pay_number, user_id;

    public PayMethodInfo() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPay_method_id() {
        return pay_method_id;
    }

    public void setPay_method_id(int pay_method_id) {
        this.pay_method_id = pay_method_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getPay_method_name() {
        return pay_method_name;
    }

    public void setPay_method_name(String pay_method_name) {
        this.pay_method_name = pay_method_name;
    }

    public String getPay_number() {
        return pay_number;
    }

    public void setPay_number(String pay_number) {
        this.pay_number = pay_number;
    }
}
