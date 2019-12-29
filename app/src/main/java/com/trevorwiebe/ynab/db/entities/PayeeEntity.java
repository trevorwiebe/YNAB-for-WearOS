package com.trevorwiebe.ynab.db.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.Keep;

@Keep
@Entity(tableName = "payee")
public class PayeeEntity {

    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "transfer_account_id")
    private String transfer_account_id;

    @ColumnInfo(name = "deleted")
    private int deleted;

    public PayeeEntity(@NonNull String id, String name, String transfer_account_id, int deleted){
        this.id = id;
        this.name = name;
        this.transfer_account_id = transfer_account_id;
        this.deleted = deleted;
    }

    @Ignore
    public PayeeEntity(){}

    public @NonNull String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
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

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }
}
