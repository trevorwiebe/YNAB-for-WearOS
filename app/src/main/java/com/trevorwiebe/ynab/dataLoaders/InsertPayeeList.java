package com.trevorwiebe.ynab.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.ynab.db.AppDatabase;
import com.trevorwiebe.ynab.db.entities.PayeeEntity;

import java.util.ArrayList;

public class InsertPayeeList extends AsyncTask<Context, Void, Void> {

    private ArrayList<PayeeEntity> payeeEntities;

    public InsertPayeeList(ArrayList<PayeeEntity> payeeEntities){
        this.payeeEntities = payeeEntities;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).payeeDao().insertPayeeList(payeeEntities);
        return null;
    }
}
