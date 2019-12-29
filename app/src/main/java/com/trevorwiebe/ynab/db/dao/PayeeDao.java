package com.trevorwiebe.ynab.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.ynab.db.entities.PayeeEntity;

import java.util.List;

@Dao
public interface PayeeDao {

    @Insert
    void insertPayee(PayeeEntity payeeEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPayeeList(List<PayeeEntity> payeeEntityList);

    @Query("SELECT * FROM payee WHERE id = :payId")
    PayeeEntity getPayeeById(String payId);

    @Query("SELECT * FROM payee WHERE deleted = 0")
    List<PayeeEntity> getPayeeList();

    @Update
    void updatePayee(PayeeEntity payeeEntity);

    @Delete
    void deletePayee(PayeeEntity payeeEntity);

}
