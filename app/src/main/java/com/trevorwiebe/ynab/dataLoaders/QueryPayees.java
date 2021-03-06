package com.trevorwiebe.ynab.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.trevorwiebe.ynab.db.AppDatabase;
import com.trevorwiebe.ynab.db.entities.PayeeEntity;

import java.util.ArrayList;
import java.util.List;

public class QueryPayees extends AsyncTask<Context, Void, ArrayList<PayeeEntity>> {

    public OnQueriedPayeesReturned onPayeesReturned;

    private static final String TAG = "QueryPayees";

    public QueryPayees(OnQueriedPayeesReturned onPayeesReturned){
        this.onPayeesReturned = onPayeesReturned;
    }

    public interface OnQueriedPayeesReturned{
        void onQueriedPayeesReturned(ArrayList<PayeeEntity> payeeEntities);
    }

    @Override
    protected ArrayList<PayeeEntity> doInBackground(Context... contexts) {
        List<PayeeEntity> payeeEntities = AppDatabase.getAppDatabase(contexts[0]).payeeDao().getPayeeList();
        Log.d(TAG, "doInBackground: " + payeeEntities.size());
        return (ArrayList<PayeeEntity>) payeeEntities;
    }

    @Override
    protected void onPostExecute(ArrayList<PayeeEntity> payeeEntities) {
        super.onPostExecute(payeeEntities);
        onPayeesReturned.onQueriedPayeesReturned(payeeEntities);
    }
}
