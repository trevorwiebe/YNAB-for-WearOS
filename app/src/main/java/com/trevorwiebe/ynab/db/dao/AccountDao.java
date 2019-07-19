package com.trevorwiebe.ynab.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.ynab.db.entities.AccountEntity;

import java.util.List;

@Dao
public interface AccountDao {

    @Insert
    void insertAccount(AccountEntity accountEntity);

    @Insert
    void insertAccountList(List<AccountEntity> accountEntities);

    @Query("SELECT * FROM account")
    List<AccountEntity> getAccountList();

    @Query("SELECT * FROM account WHERE id = :id")
    AccountEntity getAccount(String id);

    @Update
    void updateAccount(AccountEntity accountEntity);

    @Delete
    void deleteAccount(AccountEntity accountEntity);
}
