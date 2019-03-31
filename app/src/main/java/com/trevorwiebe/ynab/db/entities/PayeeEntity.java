package com.trevorwiebe.ynab.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Keep;

@Keep
@Entity(tableName = "payee")
public class PayeeEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryKey")
    private int primaryKey;

    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "transfer_account_id")
    private String transfer_account_id;

    @ColumnInfo(name = "deleted")
    private boolean deleted;

    public PayeeEntity(String id, String name, String transfer_account_id, boolean deleted){
        this.id = id;
        this.name = name;
        this.transfer_account_id = transfer_account_id;
        this.deleted = deleted;
    }

    @Ignore
    public PayeeEntity(){}

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
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

    public String getTransfer_account_id() {
        return transfer_account_id;
    }

    public void setTransfer_account_id(String transfer_account_id) {
        this.transfer_account_id = transfer_account_id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
