package com.try3x.uttam.Models;

public class User {
    public boolean acc_status;
    public int id,gender;
    public String name,email,phone,country,acc_created_at,photo_url,u_id,refer_by, referCode;
    public String fcm_token;

    public User() {
    }

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    public boolean isAcc_status() {
        return acc_status;
    }

    public void setAcc_status(boolean acc_status) {
        this.acc_status = acc_status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAcc_created_at() {
        return acc_created_at;
    }

    public void setAcc_created_at(String acc_created_at) {
        this.acc_created_at = acc_created_at;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getRefer_by() {
        return refer_by;
    }

    public void setRefer_by(String refer_by) {
        this.refer_by = refer_by;
    }

    public String getReferCode() {
        return referCode;
    }

    public void setReferCode(String referCode) {
        this.referCode = referCode;
    }
}
