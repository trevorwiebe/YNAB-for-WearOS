package com.trevorwiebe.ynab;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.trevorwiebe.ynab.connections.RefreshBudgetInfo;
import com.trevorwiebe.ynab.dataLoaders.QueryAccounts;
import com.trevorwiebe.ynab.dataLoaders.QueryCategoryById;
import com.trevorwiebe.ynab.dataLoaders.QueryPayeeById;
import com.trevorwiebe.ynab.db.entities.AccountEntity;
import com.trevorwiebe.ynab.db.entities.CategoryEntity;
import com.trevorwiebe.ynab.db.entities.PayeeEntity;
import com.trevorwiebe.ynab.utils.Utility;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.trevorwiebe.ynab.utils.Constants.BASE_URL;
import static com.trevorwiebe.ynab.utils.Constants.BUDGET_ID;
import static com.trevorwiebe.ynab.utils.Constants.PERSONAL_ACCESS_TOKEN;

public class MainActivity extends WearableActivity implements
        QueryPayeeById.OnPayeeByIdLoaded,
        QueryCategoryById.OnCategoryByIdReturned,
        QueryAccounts.OnAccountsReturned{

    private static final String TAG = "MainActivity";

    private static final int SELECT_PAYEE_CODE = 23;
    private static final int SELECT_CATEGORY_CODE = 24;
    private static final int LAST_KNOWLEDGE_OF_SERVER = 1148;

    private PayeeEntity mSelectedPayee;
    private CategoryEntity mSelectedCategory;
    private AccountEntity mSelectedAccount;

    private TextView mSelectedPayeeTv;
    private TextView mSelectedCategoryTv;
    private TextView mSelectedAccountTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String strUrl = BASE_URL + "/budgets/" + BUDGET_ID + "" + "?access_token=" + PERSONAL_ACCESS_TOKEN;
//        + "&last_knowledge_of_server=" + LAST_KNOWLEDGE_OF_SERVER;
        try{
            URL url = new URL(strUrl);
//            new RefreshBudgetInfo(this).execute(url);
        }catch (MalformedURLException e){
            Log.e(TAG, "onCreate: ", e);
        }




        final EditText transactionAmount = findViewById(R.id.transaction_amount);

        final Switch transSwitch = findViewById(R.id.trans_switch);
        transSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    transactionAmount.setTextColor(getResources().getColor(R.color.green));
                }else{
                    transactionAmount.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });

        mSelectedPayeeTv = findViewById(R.id.select_payee);
        mSelectedCategoryTv = findViewById(R.id.select_category);
        mSelectedAccountTv = findViewById(R.id.select_account);
        TextView date = findViewById(R.id.select_date);
        Button saveTransaction = findViewById(R.id.save_transaction_btn);

        mSelectedPayeeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectPayeeIntent = new Intent(MainActivity.this, SelectPayeeActivity.class);
                startActivityForResult(selectPayeeIntent, SELECT_PAYEE_CODE);
            }
        });

        mSelectedCategoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectCategoryIntent = new Intent(MainActivity.this, SelectCategoryActivity.class);
                startActivityForResult(selectCategoryIntent, SELECT_CATEGORY_CODE);
            }
        });

        long millisDate = System.currentTimeMillis();
        date.setText(Utility.convertMillisToDate(millisDate));
        date.setTypeface(null, Typeface.BOLD);

        saveTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ConfirmationActivity.class);
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
                intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Transaction Saved");
                startActivity(intent);
            }
        });

        // Enables Always-on
        setAmbientEnabled();

        new QueryAccounts(this).execute(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SELECT_PAYEE_CODE && resultCode == RESULT_OK){
            String payeeId = data.getStringExtra("selectedPayeeId");
            new QueryPayeeById(MainActivity.this, payeeId).execute(MainActivity.this);
        }else if(requestCode == SELECT_CATEGORY_CODE && resultCode == RESULT_OK){
            String categoryId = data.getStringExtra("selectedCategoryId");
            new QueryCategoryById(categoryId, MainActivity.this).execute(MainActivity.this);
        }
    }

    @Override
    public void onPayeeByIdLoaded(PayeeEntity payeeEntity) {
        mSelectedPayee = payeeEntity;
        if(payeeEntity != null) {
            String payeeName = payeeEntity.getName();
            mSelectedPayeeTv.setText(payeeName);
            mSelectedPayeeTv.setTypeface(null, Typeface.BOLD);
        }else{
            mSelectedPayeeTv.setText("Select Payee");
            mSelectedPayeeTv.setTypeface(null, Typeface.ITALIC);
        }
    }

    @Override
    public void onCategoryByIdReturned(CategoryEntity categoryEntity) {
        mSelectedCategory = categoryEntity;
        if(categoryEntity != null){
            String categoryName = categoryEntity.getName();
            mSelectedCategoryTv.setText(categoryName);
            mSelectedCategoryTv.setTypeface(null, Typeface.BOLD);
        }else{
            mSelectedCategoryTv.setText("Select Category");
            mSelectedCategoryTv.setTypeface(null, Typeface.ITALIC);
        }
    }

    @Override
    public void onAccountsReturned(ArrayList<AccountEntity> accountEntities) {
        if(accountEntities != null && accountEntities.size() == 1){
            mSelectedAccount = accountEntities.get(0);
            String name = mSelectedAccount.getName();

            mSelectedAccountTv.setText(name);
            mSelectedAccountTv.setTypeface(null, Typeface.BOLD);
        }
    }
}
