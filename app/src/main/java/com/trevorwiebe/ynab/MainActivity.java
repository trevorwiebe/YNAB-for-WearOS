package com.trevorwiebe.ynab;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.complications.ComplicationManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.trevorwiebe.ynab.connections.PostTransaction;
import com.trevorwiebe.ynab.connections.RefreshBudgetInfo;
import com.trevorwiebe.ynab.dataLoaders.QueryAccounts;
import com.trevorwiebe.ynab.dataLoaders.QueryAccountsById;
import com.trevorwiebe.ynab.dataLoaders.QueryCategoryById;
import com.trevorwiebe.ynab.dataLoaders.QueryPayeeById;
import com.trevorwiebe.ynab.dataLoaders.QueryPayeeLocationWithLatAndLong;
import com.trevorwiebe.ynab.db.entities.AccountEntity;
import com.trevorwiebe.ynab.db.entities.CategoryEntity;
import com.trevorwiebe.ynab.db.entities.PayeeEntity;
import com.trevorwiebe.ynab.db.entities.PayeeLocationEntity;
import com.trevorwiebe.ynab.utils.Constants;
import com.trevorwiebe.ynab.utils.SyncToDatabaseWorkManger;
import com.trevorwiebe.ynab.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.trevorwiebe.ynab.utils.Constants.BASE_URL;
import static com.trevorwiebe.ynab.utils.Constants.BUDGET_ID;
import static com.trevorwiebe.ynab.utils.Constants.PERSONAL_ACCESS_TOKEN;

public class MainActivity extends WearableActivity implements
        QueryPayeeById.OnPayeeByIdLoaded,
        QueryCategoryById.OnCategoryByIdReturned,
        QueryAccounts.OnAccountsReturned,
        QueryAccountsById.OnAccountByIdReturned,
        RefreshBudgetInfo.OnBudgetRefresh,
        TextWatcher,
        QueryPayeeLocationWithLatAndLong.OnPayeeLocationReturned{

    private static final String TAG = "MainActivity";

    private static final int SELECT_PAYEE_CODE = 23;
    private static final int SELECT_CATEGORY_CODE = 24;
    private static final int SELECT_ACCOUNT_CODE = 473;

    private boolean mInOrOut = false;
    private String currentText = "";

    private PayeeEntity mSelectedPayee;
    private CategoryEntity mSelectedCategory;
    private AccountEntity mSelectedAccount;

    private WorkManager mWorkManager;
    private PeriodicWorkRequest mPeriodicWorkRequest;

    private Button mOutInBtn;
    private EditText mTransactionEt;
    private ImageButton mSyncBudgetBtn;
    private TextView mLastSynced;
    private TextView mSelectedPayeeTv;
    private TextView mSelectedCategoryTv;
    private TextView mSelectedAccountTv;
    private LinearLayout mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mTransactionEt = findViewById(R.id.transaction_amount);
        mTransactionEt.addTextChangedListener(this);


        mWorkManager = WorkManager.getInstance(this);

        Constraints workerConstraint = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        mPeriodicWorkRequest = new PeriodicWorkRequest.Builder(SyncToDatabaseWorkManger.class, 1, TimeUnit.HOURS)
                .setConstraints(workerConstraint)
                .build();

        mWorkManager.enqueueUniquePeriodicWork("hourly_sync", ExistingPeriodicWorkPolicy.KEEP, mPeriodicWorkRequest);


        mOutInBtn = findViewById(R.id.in_out_btn);
        mSyncBudgetBtn = findViewById(R.id.update_budget);
        mLastSynced = findViewById(R.id.last_synced_tv);
        mSelectedPayeeTv = findViewById(R.id.select_payee);
        mSelectedCategoryTv = findViewById(R.id.select_category);
        mSelectedAccountTv = findViewById(R.id.select_account);
        mLoadingView = findViewById(R.id.loading_view);
        TextView date = findViewById(R.id.select_date);
        Button saveTransaction = findViewById(R.id.save_transaction_btn);

        mOutInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mInOrOut){
                    mOutInBtn.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.redText));
                    mTransactionEt.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.redText));
                    mOutInBtn.setText("Outflow");
                    mInOrOut = false;
                }else{
                    mOutInBtn.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.greenText));
                    mTransactionEt.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.greenText));
                    mOutInBtn.setText("Inflow");
                    mInOrOut = true;
                }
            }
        });
        mSyncBudgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                syncBudget();
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

        long lastSyncedTime = Utility.getLastSynced(this);
        if(lastSyncedTime != 0){
            String lastSyncedString = Utility.convertMillisToTime(lastSyncedTime);
            mLastSynced.setText(lastSyncedString);
        }else{
            syncBudget();
        }

        saveTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent transactionFinishedIntent = new Intent(MainActivity.this, ConfirmationActivity.class);

                String postUrl = BASE_URL + "/budgets/" + BUDGET_ID + "/transactions";

                boolean shouldSaveTransaction = true;

                String date = Utility.convertMillisToDate(System.currentTimeMillis());
                double amount = Double.parseDouble(mTransactionEt.getText().toString().substring(1));

                String payee_id = "";
                String payee_name = "";
                String category_id = "";
                String accountId = "";

                if(mSelectedPayee == null){
                    shouldSaveTransaction = false;
                    transactionFinishedIntent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.FAILURE_ANIMATION);
                    transactionFinishedIntent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "No Payee Selected");
                }else{
                    payee_id = mSelectedPayee.getId();
                    payee_name = mSelectedPayee.getName();
                }

                if(shouldSaveTransaction) {
                    if (mSelectedCategory == null) {
                        shouldSaveTransaction = false;
                        transactionFinishedIntent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.FAILURE_ANIMATION);
                        transactionFinishedIntent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "No Category Selected");
                    } else {
                        category_id = mSelectedCategory.getId();
                    }
                }

                if(shouldSaveTransaction) {
                    if (mSelectedAccount == null) {
                        shouldSaveTransaction = false;
                        transactionFinishedIntent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.FAILURE_ANIMATION);
                        transactionFinishedIntent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "No Account Selected");
                    } else {
                        accountId = mSelectedAccount.getId();
                    }
                }

                if(shouldSaveTransaction) {
                    try {
                        JSONObject transactionList = new JSONObject();

                        JSONObject transaction = new JSONObject();
                        transaction.put("account_id", accountId);
                        transaction.put("date", date);
                        transaction.put("amount", amount);
                        transaction.put("payee_id", payee_id);
                        transaction.put("payee_name", payee_name);
                        transaction.put("category_id", category_id);
                        transaction.put("memo", "");
                        transaction.put("cleared", "cleared");
                        transaction.put("approved", true);
                        transaction.put("flag_color", "");
                        transaction.put("import_id", "");

                        transactionList.put("transaction", transaction);

                        Log.d(TAG, "onClick: " + transactionList.toString());

                        new PostTransaction().execute(postUrl, transactionList.toString());

                        transactionFinishedIntent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
                        transactionFinishedIntent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Transaction Saved");

                    } catch (JSONException e) {
                        transactionFinishedIntent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.EXTRA_ANIMATION_TYPE);
                        transactionFinishedIntent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Transaction Failed");
                        Log.e(TAG, "onClick: ", e);
                    }
                }

                startActivity(transactionFinishedIntent);

                mSelectedPayee = null;
                mSelectedPayeeTv.setText("Select Payee");
                mSelectedPayeeTv.setTypeface(null, Typeface.ITALIC);
                mSelectedCategory = null;
                mSelectedCategoryTv.setText("Select Category");
                mSelectedCategoryTv.setTypeface(null, Typeface.ITALIC);
                mSelectedAccount = null;
                mSelectedAccountTv.setText("Select Account");
                mSelectedAccountTv.setTypeface(null, Typeface.ITALIC);

                mTransactionEt.setText("$0.00");

                mOutInBtn.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.redText));
                mTransactionEt.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.redText));
                mOutInBtn.setText("Outflow");
                mInOrOut = false;
            }
        });

        // Enables Always-on
        setAmbientEnabled();

        new QueryAccounts(this).execute(this);
    }

    @Override
    public void onBudgetRefresh(int resultCode) {
        mLoadingView.setVisibility(View.GONE);

        if(resultCode == Constants.RESULT_OK){
            new QueryAccounts(this).execute(this);
            Utility.saveLastSynced(MainActivity.this, System.currentTimeMillis());
            mLastSynced.setText(Utility.convertMillisToTime(System.currentTimeMillis()));
        }else{
            Toast.makeText(this, "An error occurred while trying to connect to the cloud", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(!s.toString().equals(currentText)){
           mTransactionEt.removeTextChangedListener(this);

                String cleanString = s.toString().replaceAll("[$,.]", "");

                double parsed = Double.parseDouble(cleanString);
                String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                currentText = formatted;
           mTransactionEt.setText(formatted);
           mTransactionEt.setSelection(formatted.length());

           mTransactionEt.addTextChangedListener(this);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SELECT_PAYEE_CODE && resultCode == RESULT_OK){
            String payeeId = data.getStringExtra("selectedPayeeId");
            new QueryPayeeById(MainActivity.this, payeeId).execute(MainActivity.this);
        }else if(requestCode == SELECT_CATEGORY_CODE && resultCode == RESULT_OK){
            String categoryId = data.getStringExtra("selectedCategoryId");
            new QueryCategoryById(categoryId, MainActivity.this, null, null, null).execute(MainActivity.this);
        }else if(requestCode == SELECT_ACCOUNT_CODE && resultCode == RESULT_OK){
            String accountId = data.getStringExtra("selectedAccountId");
            Log.d(TAG, "onActivityResult: " + accountId);
            new QueryAccountsById(accountId, MainActivity.this).execute(MainActivity.this);
        }
    }

    @Override
    public void onPayeeByIdLoaded(PayeeEntity payeeEntity) {
        mSelectedPayee = payeeEntity;
        if(mSelectedPayee != null) {
            String payeeName = payeeEntity.getName();
            mSelectedPayeeTv.setText(payeeName);
            mSelectedPayeeTv.setTypeface(null, Typeface.BOLD);
        }else{
            mSelectedPayeeTv.setText("Select Payee");
            mSelectedPayeeTv.setTypeface(null, Typeface.ITALIC);
        }
    }

    @Override
    public void onCategoryByIdReturned(CategoryEntity categoryEntity, ComplicationManager complicationManager, Integer complicationId, Integer datatype) {
        mSelectedCategory = categoryEntity;
        if(mSelectedCategory != null){
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

    private void syncBudget(){
        mLoadingView.setVisibility(View.VISIBLE);
        new RefreshBudgetInfo(this, this).execute();
    }

    @Override
    public void onPayeeLocationReturned(PayeeLocationEntity payeeLocationEntity) {
        if(payeeLocationEntity != null) {
            String payeeId = payeeLocationEntity.getPayee_id();
            new QueryPayeeById(MainActivity.this, payeeId).execute(MainActivity.this);
        }
    }
}
