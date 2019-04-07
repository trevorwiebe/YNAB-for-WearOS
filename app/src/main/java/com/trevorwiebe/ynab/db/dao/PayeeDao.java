package com.trevorwiebe.ynab.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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
