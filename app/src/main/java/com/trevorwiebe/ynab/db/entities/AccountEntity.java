package com.trevorwiebe.ynab.db.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.Keep;

@Keep
@Entity(tableName = "account")
public class AccountEntity {

    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "balance")
    private int balance;

    @ColumnInfo(name = "deleted")
    private int deleted;

    public AccountEntity(@NonNull String id, String name, int balance, int deleted) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.deleted = deleted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }
}
