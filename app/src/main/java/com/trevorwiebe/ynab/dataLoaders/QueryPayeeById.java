package com.trevorwiebe.ynab.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.ynab.db.AppDatabase;
import com.trevorwiebe.ynab.db.entities.PayeeEntity;

public class QueryPayeeById extends AsyncTask<Context, Void, PayeeEntity> {

    private String payeeId;
    private OnPayeeByIdLoaded onPayeeByIdLoaded;

    public QueryPayeeById(OnPayeeByIdLoaded onPayeeByIdLoaded, String payeeId){
        this.onPayeeByIdLoaded = onPayeeByIdLoaded;
        this.payeeId = payeeId;
    }

    public interface OnPayeeByIdLoaded{
        void onPayeeByIdLoaded(PayeeEntity payeeEntity);
    }

    @Override
    protected PayeeEntity doInBackground(Context... contexts) {
        return AppDatabase.getAppDatabase(contexts[0]).payeeDao().getPayeeById(payeeId);
    }

    @Override
    protected void onPostExecute(PayeeEntity payeeEntity) {
        super.onPostExecute(payeeEntity);
        onPayeeByIdLoaded.onPayeeByIdLoaded(payeeEntity);
    }
}
