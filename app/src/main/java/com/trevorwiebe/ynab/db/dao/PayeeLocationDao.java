package com.trevorwiebe.ynab.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.trevorwiebe.ynab.db.entities.PayeeLocationEntity;

import java.util.List;

@Dao
public interface PayeeLocationDao {

    @Insert
    void insertPayeeLocationEntity(PayeeLocationEntity payeeLocationEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPayeeLocationEntities(List<PayeeLocationEntity> payeeLocationEntities);

    @Query("SELECT * FROM payeeLocation WHERE (latitude BETWEEN (:startLat + 0.005000) AND (:startLat - 0.005000)) AND (longitude BETWEEN (:startLong + 0.005000) AND (:startLong - 0.005000))")
    PayeeLocationEntity getPayeeWithLatAndLong(double startLat, double startLong);

    @Update
    void updatePayeeLocation(PayeeLocationEntity payeeLocationEntity);

    @Delete
    void deletePayeeLocation(PayeeLocationEntity payeeLocationEntity);
}
