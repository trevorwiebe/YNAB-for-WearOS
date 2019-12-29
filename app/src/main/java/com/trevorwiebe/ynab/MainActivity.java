package com.trevorwiebe.ynab;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.trevorwiebe.ynab.connections.RefreshBudgetInfo;
import com.trevorwiebe.ynab.dataLoaders.QueryAccounts;
import com.trevorwiebe.ynab.dataLoaders.QueryAccountsById;
import com.trevorwiebe.ynab.dataLoaders.QueryCategoryById;
import com.trevorwiebe.ynab.dataLoaders.QueryPayeeById;
import com.trevorwiebe.ynab.db.entities.AccountEntity;
import com.trevorwiebe.ynab.db.entities.CategoryEntity;
import com.trevorwiebe.ynab.db.entities.PayeeEntity;
import com.trevorwiebe.ynab.utils.Constants;
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
        QueryAccountsById.OnAccountByIdReturned,
        RefreshBudgetInfo.OnBudgetRefresh {

    private static final String TAG = "MainActivity";

    private static final int SELECT_PAYEE_CODE = 23;
    private static final int SELECT_CATEGORY_CODE = 24;
    private static final int SELECT_ACCOUNT_CODE = 473;
    private static final  int PERMISSION_REQUEST_LOCATION = 83;

    private boolean mInOrOut = false;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private PayeeEntity mSelectedPayee;
    private CategoryEntity mSelectedCategory;
    private AccountEntity mSelectedAccount;

    private Button mOutInBtn;
    private TextView mSelectedPayeeTv;
    private TextView mSelectedCategoryTv;
    private TextView mSelectedAccountTv;
    private LinearLayout mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){

            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
            }
        }else {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            Log.d(TAG, "onSuccess: " + location.getLatitude() + " " + location.getLongitude());
                        }
                    });
        }

        try{

            String strUrl;

            String server_knowledge = Utility.getServerKnowledge(this);
            if(server_knowledge.length() == 0){
                strUrl = BASE_URL + "/budgets/" + BUDGET_ID + "" + "?access_token=" + PERSONAL_ACCESS_TOKEN;
            }else {
                strUrl = BASE_URL + "/budgets/" + BUDGET_ID + "" + "?access_token=" + PERSONAL_ACCESS_TOKEN + "&last_knowledge_of_server=" + server_knowledge;
            }

            URL url = new URL(strUrl);
            new RefreshBudgetInfo(this, this).execute(url);
        }catch (MalformedURLException e){
            Log.e(TAG, "onCreate: ", e);
        }


        final EditText transactionAmount = findViewById(R.id.transaction_amount);

        mOutInBtn = findViewById(R.id.in_out_btn);
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

                String postUrl = BASE_URL + "/budgets/" + BUDGET_ID + "/transactions";

                Intent intent = new Intent(MainActivity.this, ConfirmationActivity.class);
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
                intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Transaction Saved");
                startActivity(intent);

                mSelectedPayee = null;
                mSelectedPayeeTv.setText("Select Payee");
                mSelectedPayeeTv.setTypeface(null, Typeface.ITALIC);
                mSelectedCategory = null;
                mSelectedCategoryTv.setText("Select Category");
                mSelectedCategoryTv.setTypeface(null, Typeface.ITALIC);
                mSelectedAccount = null;
                mSelectedAccountTv.setText("Select Account");
                mSelectedAccountTv.setTypeface(null, Typeface.ITALIC);

                transactionAmount.setText("$0.00");

                mOutInBtn.setBackgroundColor(getResources().getColor(R.color.red));
                transactionAmount.setTextColor(getResources().getColor(R.color.red));
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
        }else{
            Toast.makeText(this, "An error occurred while trying to connect to the cloud", Toast.LENGTH_SHORT).show();
        }

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        fusedLocationProviderClient.getLastLocation()
                                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        Log.d(TAG, "onSuccess: " + location.getLatitude() + " " + location.getLongitude());
                                    }
                                });
                    }

                }
            }
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
    public void onCategoryByIdReturned(CategoryEntity categoryEntity) {
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
}
