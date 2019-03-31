package com.trevorwiebe.ynab;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class MainActivity extends WearableActivity {

    private static final String TAG = "MainActivity";

    boolean mAmountEditedByUser = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final EditText transactionAmount = findViewById(R.id.transaction_amount);
        RelativeLayout selectPayee = findViewById(R.id.select_payee);
        selectPayee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectPayeeIntent = new Intent(MainActivity.this, SelectPayeeActivity.class);
                startActivity(selectPayeeIntent);
            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }

}
