package com.trevorwiebe.ynab;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;

import com.trevorwiebe.ynab.connections.FetchPayees;
import com.trevorwiebe.ynab.dataLoaders.InsertPayeeList;
import com.trevorwiebe.ynab.db.entities.PayeeEntity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.trevorwiebe.ynab.utils.Constants.BASE_URL;
import static com.trevorwiebe.ynab.utils.Constants.BUDGET_ID;
import static com.trevorwiebe.ynab.utils.Constants.PERSONAL_ACCESS_TOKEN;

public class SelectPayeeActivity extends WearableActivity implements FetchPayees.OnPayeesReturned{

    private static final String TAG = "SelectPayeeActivity";

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
    }

    @Override
    public void onPayeesReturned(ArrayList<PayeeEntity> payeeEntities) {
        new InsertPayeeList(payeeEntities).execute(SelectPayeeActivity.this);
    }
}
