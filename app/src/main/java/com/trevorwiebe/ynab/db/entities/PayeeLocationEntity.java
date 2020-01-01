package com.trevorwiebe.ynab.db.entities;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Keep
@Entity(tableName = "payeeLocation")
public class PayeeLocationEntity {

    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "payee_id")
    private String payee_id;

    @ColumnInfo(name = "latitude")
    private String latitude;

    @ColumnInfo(name = "longitude")
    private String longitude;

    @ColumnInfo(name = "deletedInt")
    private int deletedInt;

    public PayeeLocationEntity(@NonNull String id, String payee_id, String latitude, String longitude, int deletedInt) {
        this.id = id;
        this.payee_id = payee_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.deletedInt = deletedInt;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getPayee_id() {
        return payee_id;
    }

    public void setPayee_id(String payee_id) {
        this.payee_id = payee_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getDeletedInt() {
        return deletedInt;
    }

    public void setDeletedInt(int deletedInt) {
        this.deletedInt = deletedInt;
    }
}
