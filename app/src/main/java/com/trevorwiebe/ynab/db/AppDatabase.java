package com.trevorwiebe.ynab.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.trevorwiebe.ynab.db.dao.PayeeDao;
import com.trevorwiebe.ynab.db.entities.PayeeEntity;

@Database(entities = {PayeeEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract PayeeDao payeeDao();

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
