package com.try3x.uttam.Models;

public class Packages {
    public int id;
    public String name;
    public float price;
    public String btn;
    public int baji;
    public boolean active;

    public int getBaji() {
        return baji;
    }

    public void setBaji(int baji) {
        this.baji = baji;
    }

    public String getBtn() {
        return btn;
    }

    public void setBtn(String btn) {
        this.btn = btn;
    }

    public Packages() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
