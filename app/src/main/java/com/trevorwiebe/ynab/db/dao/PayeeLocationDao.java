package com.trevorwiebe.ynab.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.ynab.db.entities.AccountEntity;
import com.trevorwiebe.ynab.db.entities.PayeeLocationEntity;

import java.util.List;

@Dao
public interface PayeeLocationDao {

    @Insert
    void insertPayeeLocationEntity(PayeeLocationEntity payeeLocationEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPayeeLocationEntities(List<PayeeLocationEntity> payeeLocationEntities);

    @Query("SELECT * FROM payeeLocation WHERE latitude = :latitude AND longitude = :longitude")
    PayeeLocationEntity getPayeeWithLatAndLong(String latitude, String longitude);

    @Update
    void updatePayeeLocation(PayeeLocationEntity payeeLocationEntity);

    @Delete
    void deletePayeeLocation(PayeeLocationEntity payeeLocationEntity);
}
