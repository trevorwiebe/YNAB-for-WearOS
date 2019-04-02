package com.trevorwiebe.ynab;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;

import com.trevorwiebe.ynab.adapters.SelectPayeeRvAdapter;
import com.trevorwiebe.ynab.connections.FetchPayees;
import com.trevorwiebe.ynab.dataLoaders.InsertPayeeList;
import com.trevorwiebe.ynab.dataLoaders.QueryPayees;
import com.trevorwiebe.ynab.db.entities.PayeeEntity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.trevorwiebe.ynab.utils.Constants.BASE_URL;
import static com.trevorwiebe.ynab.utils.Constants.BUDGET_ID;
import static com.trevorwiebe.ynab.utils.Constants.PERSONAL_ACCESS_TOKEN;

public class SelectPayeeActivity extends WearableActivity implements
        FetchPayees.OnPayeesReturned,
        QueryPayees.OnQueriedPayeesReturned{

    private static final String TAG = "SelectPayeeActivity";

    private SelectPayeeRvAdapter selectPayeeRvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payee);

        String strUrl = BASE_URL + "/budgets/" + BUDGET_ID + "/payees" + "?access_token=" + PERSONAL_ACCESS_TOKEN;

        try{
            URL url = new URL(strUrl);
            new FetchPayees(SelectPayeeActivity.this).execute(url);
        }catch (MalformedURLException e){
            Log.e(TAG, "onCreate: ", e);
        }


        RecyclerView selectPayeeRv = findViewById(R.id.select_payee_rv);
        selectPayeeRv.setLayoutManager(new LinearLayoutManager(this));
        selectPayeeRvAdapter = new SelectPayeeRvAdapter();
        selectPayeeRv.setAdapter(selectPayeeRvAdapter);


//        new QueryPayees(this).execute(this);


    }

    @Override
    public void onPayeesReturned(ArrayList<PayeeEntity> payeeEntities) {
        Log.d(TAG, "onPayeesReturned: " + payeeEntities);
//        new InsertPayeeList(payeeEntities).execute(SelectPayeeActivity.this);
    }

    @Override
    public void onQueriedPayeesReturned(ArrayList<PayeeEntity> payeeEntities) {
        Log.d(TAG, "onQueriedPayeesReturned: " + payeeEntities.toString());
        selectPayeeRvAdapter.swapData(payeeEntities);
    }
}
