package com.trevorwiebe.ynab.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.ynab.db.AppDatabase;
import com.trevorwiebe.ynab.db.entities.AccountEntity;

import java.util.ArrayList;
import java.util.List;

public class QueryAccounts extends AsyncTask<Context, Void, ArrayList<AccountEntity>> {

    private OnAccountsReturned onAccountsReturned;

    public QueryAccounts(OnAccountsReturned onAccountsReturned){
        this.onAccountsReturned = onAccountsReturned;
    }

    public interface OnAccountsReturned{
        void onAccountsReturned(ArrayList<AccountEntity> accountEntities);
    }

    @Override
    protected ArrayList<AccountEntity> doInBackground(Context... contexts) {
        List<AccountEntity> accountEntities = AppDatabase.getAppDatabase(contexts[0]).accountDao().getAccountList();
        return (ArrayList<AccountEntity>) accountEntities;
    }

    @Override
    protected void onPostExecute(ArrayList<AccountEntity> accountEntities) {
        super.onPostExecute(accountEntities);
        onAccountsReturned.onAccountsReturned(accountEntities);
    }
}
