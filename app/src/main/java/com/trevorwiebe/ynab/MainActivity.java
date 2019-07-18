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
import com.trevorwiebe.ynab.dataLoaders.QueryAccountsById;
import com.trevorwiebe.ynab.dataLoaders.QueryCategoryById;
import com.trevorwiebe.ynab.dataLoaders.QueryPayeeById;
import com.trevorwiebe.ynab.db.entities.AccountEntity;
import com.trevorwiebe.ynab.db.entities.CategoryEntity;
import com.trevorwiebe.ynab.db.entities.PayeeEntity;
import com.trevorwiebe.ynab.utils.Utility;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.trevorwiebe.ynab.utils.Constants.BASE_URL;
import static com.trevorwiebe.ynab.utils.Constants.BUDGET_ID;
import static com.trevorwiebe.ynab.utils.Constants.PERSONAL_ACCESS_TOKEN;

public class MainActivity extends WearableActivity implements
        QueryPayeeById.OnPayeeByIdLoaded,
        QueryCategoryById.OnCategoryByIdReturned,
        QueryAccounts.OnAccountsReturned,
        QueryAccountsById.OnAccountByIdReturned{

    private static final String TAG = "MainActivity";

    private static final int SELECT_PAYEE_CODE = 23;
    private static final int SELECT_CATEGORY_CODE = 24;
    private static final int SELECT_ACCOUNT_CODE = 473;
    private static final int LAST_KNOWLEDGE_OF_SERVER = 1148;

    private boolean mInOrOut = false;

    private PayeeEntity mSelectedPayee;
    private CategoryEntity mSelectedCategory;
    private AccountEntity mSelectedAccount;

    private Button mOutInBtn;
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

        mOutInBtn = findViewById(R.id.in_out_btn);
        mSelectedPayeeTv = findViewById(R.id.select_payee);
        mSelectedCategoryTv = findViewById(R.id.select_category);
        mSelectedAccountTv = findViewById(R.id.select_account);
        TextView date = findViewById(R.id.select_date);
        Button saveTransaction = findViewById(R.id.save_transaction_btn);

        mOutInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mInOrOut){
                    mOutInBtn.setBackgroundColor(getResources().getColor(R.color.red));
                    transactionAmount.setTextColor(getResources().getColor(R.color.red));
                    mOutInBtn.setText("Outflow");
                    mInOrOut = false;
                }else{
                    mOutInBtn.setBackgroundColor(getResources().getColor(R.color.green));
                    transactionAmount.setTextColor(getResources().getColor(R.color.green));
                    mOutInBtn.setText("Inflow");
                    mInOrOut = true;
                }
            }
        });
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
        mSelectedAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectAccountIntent = new Intent(MainActivity.this, SelectAccountActivity.class);
                startActivityForResult(selectAccountIntent, SELECT_ACCOUNT_CODE);
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
        }else if(requestCode == SELECT_ACCOUNT_CODE && resultCode == RESULT_OK){
            String accountId = data.getStringExtra("selectedAccountId");
            Log.d(TAG, "onActivityResult: " + accountId);
            new QueryAccountsById(accountId, MainActivity.this).execute(MainActivity.this);
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

    @Override
    public void onAccountByIdReturned(AccountEntity accountEntity) {
        mSelectedAccount = accountEntity;
        if(mSelectedAccount != null){
            String accountName = accountEntity.getName();
            mSelectedAccountTv.setText(accountName);
            mSelectedAccountTv.setTypeface(null, Typeface.BOLD);
        }else{
            mSelectedAccountTv.setText("Select Account");
            mSelectedAccountTv.setTypeface(null, Typeface.ITALIC);
        }
    }
}
