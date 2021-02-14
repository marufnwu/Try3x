package com.try3x.uttam.Models.Response;

import com.try3x.uttam.Models.Packages;

import java.util.ArrayList;
import java.util.List;

public class BajiServerBody {
    public String email, u_id, sha1, quesId;
    public int noOfBaji, quesResult;
    public String token;
   public List<Packages> packageList = new ArrayList<>();

    public BajiServerBody() {
    }

    public String getQuesId() {
        return quesId;
    }

    public void setQuesId(String quesId) {
        this.quesId = quesId;
    }

    public int getQuesResult() {
        return quesResult;
    }

    public void setQuesResult(int quesResult) {
        this.quesResult = quesResult;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Packages> getPackageList() {
        return packageList;
    }

    public void setPackageList(List<Packages> packageList) {
        this.packageList = packageList;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public int getNoOfBaji() {
        return noOfBaji;
    }

    public void setNoOfBaji(int noOfBaji) {
        this.noOfBaji = noOfBaji;
    }
}
