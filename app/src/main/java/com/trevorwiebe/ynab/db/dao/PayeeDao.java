package com.trevorwiebe.ynab.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.ynab.db.entities.PayeeEntity;

import java.util.List;

@Dao
public interface PayeeDao {

    @Insert
    void insertPayee(PayeeEntity payeeEntity);

    @Insert
    void insertPayeeList(List<PayeeEntity> payeeEntityList);

    @Query("SELECT * FROM payee WHERE id = :payId")
    PayeeEntity getPayeeById(String payId);

    @Query("SELECT * FROM payee")
    List<PayeeEntity> getPayeeList();

    @Update
    void updatePayee(PayeeEntity payeeEntity);

    @Delete
    void deletePayee(PayeeEntity payeeEntity);

}
