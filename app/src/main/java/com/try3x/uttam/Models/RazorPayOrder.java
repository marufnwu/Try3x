package com.try3x.uttam.Models;

import java.util.List;

public class RazorPayOrder {
    public String id;
    public String entity;
    public int amount;
    public int amount_paid;
    public int amount_due;
    public String currency;
    public Object receipt;
    public Object offer_id;
    public String status;
    public int attempts;
    public List<Object> notes;
    public int created_at;
}
