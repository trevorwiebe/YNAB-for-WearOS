package com.trevorwiebe.ynab.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.ynab.db.AppDatabase;
import com.trevorwiebe.ynab.db.entities.PayeeLocationEntity;

public class QueryPayeeLocationWithLatAndLong extends AsyncTask<Context, Void, PayeeLocationEntity> {

    private double latitude;
    private double longitude;
    private OnPayeeLocationReturned onPayeeLocationReturned;

    public QueryPayeeLocationWithLatAndLong(double latitude, double longitude, OnPayeeLocationReturned onPayeeLocationReturned){
        this.latitude = latitude;
        this.longitude = longitude;
        this.onPayeeLocationReturned = onPayeeLocationReturned;
    }

    public interface OnPayeeLocationReturned{
        void onPayeeLocationReturned(PayeeLocationEntity payeeLocationEntity);
    }

    @Override
    protected PayeeLocationEntity doInBackground(Context... contexts) {
        return AppDatabase.getAppDatabase(contexts[0]).payeeLocationDao().getPayeeWithLatAndLong(latitude, longitude);
    }

    @Override
    protected void onPostExecute(PayeeLocationEntity payeeLocationEntity) {
        super.onPostExecute(payeeLocationEntity);
        onPayeeLocationReturned.onPayeeLocationReturned(payeeLocationEntity);
    }
}
