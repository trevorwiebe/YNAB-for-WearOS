package com.trevorwiebe.ynab;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

public class SelectPayeeActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payee);
    }
}
