package com.trevorwiebe.ynab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;

import com.trevorwiebe.ynab.adapters.SelectAccountRvAdapter;
import com.trevorwiebe.ynab.dataLoaders.QueryAccounts;
import com.trevorwiebe.ynab.db.entities.AccountEntity;
import com.trevorwiebe.ynab.utils.ItemClickListener;

import java.util.ArrayList;


public class SelectAccountActivity extends WearableActivity implements QueryAccounts.OnAccountsReturned {

    private static final String TAG = "SelectAccountActivity";

    private SelectAccountRvAdapter mSelectAccountRvAdapter;
    private ArrayList<AccountEntity> mAccountEntities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_account);

        RecyclerView selectAccountRv = findViewById(R.id.select_account_rv);
        selectAccountRv.setLayoutManager(new LinearLayoutManager(this));
        mSelectAccountRvAdapter = new SelectAccountRvAdapter(null);
        selectAccountRv.setAdapter(mSelectAccountRvAdapter);

        selectAccountRv.addOnItemTouchListener(new ItemClickListener(this, selectAccountRv, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AccountEntity selectedAccountEntity = mAccountEntities.get(position);
                String accountId = selectedAccountEntity.getId();
                Log.d(TAG, "onItemClick: " + accountId);
                Intent intent = new Intent();
                intent.putExtra("selectedAccountId", accountId);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        new QueryAccounts(this).execute(this);
    }

    @Override
    public void onAccountsReturned(ArrayList<AccountEntity> accountEntities) {
        mAccountEntities = accountEntities;
        mSelectAccountRvAdapter.swapData(accountEntities);
    }
}
