package com.trevorwiebe.ynab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wear.widget.drawer.WearableActionDrawerView;
import android.support.wearable.activity.WearableActivity;
import android.view.View;

import com.trevorwiebe.ynab.adapters.SelectPayeeRvAdapter;
import com.trevorwiebe.ynab.dataLoaders.QueryPayees;
import com.trevorwiebe.ynab.db.entities.PayeeEntity;
import com.trevorwiebe.ynab.utils.ItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SelectPayeeActivity extends WearableActivity implements
        QueryPayees.OnQueriedPayeesReturned{

    private static final String TAG = "SelectPayeeActivity";

    private WearableActionDrawerView wearableActionDrawer;

    private SelectPayeeRvAdapter selectPayeeRvAdapter;
    private ArrayList<PayeeEntity> mPayeesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payee);

        RecyclerView selectPayeeRv = findViewById(R.id.select_payee_rv);
        selectPayeeRv.setLayoutManager(new LinearLayoutManager(this));
        selectPayeeRvAdapter = new SelectPayeeRvAdapter();
        selectPayeeRv.setAdapter(selectPayeeRvAdapter);
        selectPayeeRv.addOnItemTouchListener(new ItemClickListener(this, selectPayeeRv, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PayeeEntity payeeEntity = mPayeesList.get(position);
                String payeeId = payeeEntity.getId();
                Intent intent = new Intent();
                intent.putExtra("selectedPayeeId", payeeId);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        new QueryPayees(this).execute(this);
    }

    @Override
    public void onQueriedPayeesReturned(ArrayList<PayeeEntity> payeeEntities) {
        mPayeesList = payeeEntities;
        Collections.sort(mPayeesList, new Comparator<PayeeEntity>() {
            @Override
            public int compare(PayeeEntity o1, PayeeEntity o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        selectPayeeRvAdapter.swapData(mPayeesList);
    }
}
