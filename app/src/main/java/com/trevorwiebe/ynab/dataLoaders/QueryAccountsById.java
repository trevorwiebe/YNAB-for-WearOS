package com.trevorwiebe.ynab.dataLoaders;

import android.accounts.Account;
import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.ynab.db.AppDatabase;
import com.trevorwiebe.ynab.db.entities.AccountEntity;

public class QueryAccountsById extends AsyncTask<Context, Void, AccountEntity> {

    private String accountId;
    private OnAccountByIdReturned onAccountByIdReturned;

    public QueryAccountsById(String accountId, OnAccountByIdReturned onAccountByIdReturned){
        this.accountId = accountId;
        this.onAccountByIdReturned = onAccountByIdReturned;
    }

    public interface OnAccountByIdReturned{
        void onAccountByIdReturned(AccountEntity accountEntity);
    }

    @Override
    protected AccountEntity doInBackground(Context... contexts) {
        return AppDatabase.getAppDatabase(contexts[0]).accountDao().getAccount(accountId);
    }

    @Override
    protected void onPostExecute(AccountEntity accountEntity) {
        super.onPostExecute(accountEntity);
        onAccountByIdReturned.onAccountByIdReturned(accountEntity);
    }
}
