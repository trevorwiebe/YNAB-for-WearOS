package com.trevorwiebe.ynab.objects;

public class Transaction {

    private String account_id;
    private String date;
    private long amount;
    private String payee_id;
    private String payee_name;
    private String category_id;

    public Transaction(String account_id, String date, long amount, String payee_id, String payee_name, String category_id) {
        this.account_id = account_id;
        this.date = date;
        this.amount = amount;
        this.payee_id = payee_id;
        this.payee_name = payee_name;
        this.category_id = category_id;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getPayee_id() {
        return payee_id;
    }

    public void setPayee_id(String payee_id) {
        this.payee_id = payee_id;
    }

    public String getPayee_name() {
        return payee_name;
    }

    public void setPayee_name(String payee_name) {
        this.payee_name = payee_name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }
}
