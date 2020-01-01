package com.trevorwiebe.ynab.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.trevorwiebe.ynab.db.dao.AccountDao;
import com.trevorwiebe.ynab.db.dao.CategoryDao;
import com.trevorwiebe.ynab.db.dao.PayeeDao;
import com.trevorwiebe.ynab.db.dao.PayeeLocationDao;
import com.trevorwiebe.ynab.db.entities.AccountEntity;
import com.trevorwiebe.ynab.db.entities.CategoryEntity;
import com.trevorwiebe.ynab.db.entities.PayeeEntity;
import com.trevorwiebe.ynab.db.entities.PayeeLocationEntity;

@Database(entities = {PayeeEntity.class, CategoryEntity.class, AccountEntity.class, PayeeLocationEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract PayeeDao payeeDao();
    public abstract CategoryDao categoryDao();
    public abstract AccountDao accountDao();
    public abstract PayeeLocationDao payeeLocationDao();

    public static AppDatabase getAppDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class,
                    "ynab_db")
                    .build();
        }
        return INSTANCE;
    }

}
