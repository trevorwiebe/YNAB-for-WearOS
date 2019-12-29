package com.trevorwiebe.ynab.connections;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.trevorwiebe.ynab.db.AppDatabase;
import com.trevorwiebe.ynab.db.entities.AccountEntity;
import com.trevorwiebe.ynab.db.entities.CategoryEntity;
import com.trevorwiebe.ynab.db.entities.PayeeEntity;
import com.trevorwiebe.ynab.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class RefreshBudgetInfo extends AsyncTask<URL, Void, Integer> {

    private static final String TAG = "RefreshBudgetInfo";

    private OnBudgetRefresh onBudgetRefresh;
    private Context context;

    public RefreshBudgetInfo(Context context, OnBudgetRefresh onBudgetRefresh){
        this.context = context;
        this.onBudgetRefresh = onBudgetRefresh;
    }

    public interface OnBudgetRefresh{
        void onBudgetRefresh(int resultCode);
    }

    @Override
    protected Integer doInBackground(URL... urls) {

        URL url = urls[0];

        Log.d(TAG, "doInBackground: " + url.toString());

        try {

            URLConnection urlConnection = url.openConnection();
            InputStream in = urlConnection.getInputStream();

            String result = inputStreamToString(in);

            return parseAndSaveInputStream(result);

        }catch (IOException e){
            return Constants.IO_EXCEPTION;
        }
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        super.onPostExecute(resultCode);
        onBudgetRefresh.onBudgetRefresh(resultCode);
    }

    private String inputStreamToString(InputStream inputStream) throws IOException {
        try(ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            return result.toString();
        }
    }

    private int parseAndSaveInputStream(String result){


        try {

            JSONObject dataObject = new JSONObject(result).getJSONObject("data");
            JSONObject budgetObject = dataObject.getJSONObject("budget");

//            JSONObject serverKnowledge = dataObject.getJSONObject("server_knowledge");

//            Log.d(TAG, "parseAndSaveInputStream: server knowledge: " + serverKnowledge);

            Log.d(TAG, "parseAndSaveInputStream: " + dataObject.toString());

            JSONArray payeeArray = budgetObject.getJSONArray("payees");
            ArrayList<PayeeEntity> payeeEntities = new ArrayList<>();
            for(int r=0; r<payeeArray.length(); r++){
                JSONObject jsonObject = payeeArray.getJSONObject(r);
                String payeeId = jsonObject.getString("id");
                String payeeName = jsonObject.getString("name");
                String transfer_account_id = jsonObject.getString("transfer_account_id");
                boolean deleted = jsonObject.getBoolean("deleted");

                PayeeEntity payeeEntity = new PayeeEntity(payeeId, payeeName, transfer_account_id, deleted);
                payeeEntities.add(payeeEntity);
            }
//            AppDatabase.getAppDatabase(context).payeeDao().insertPayeeList(payeeEntities);


            JSONArray categoriesArray = budgetObject.getJSONArray("categories");
            ArrayList<CategoryEntity> categoryEntities = new ArrayList<>();
            for(int s=0; s<categoriesArray.length(); s++){
                JSONObject jsonObject = categoriesArray.getJSONObject(s);
                String categoryId = jsonObject.getString("id");
                String category_group_id = jsonObject.getString("category_group_id");
                String name = jsonObject.getString("name");
                boolean hidden = jsonObject.getBoolean("hidden");
                int hiddenInt;
                if(hidden){
                    hiddenInt = 1;
                }else{
                    hiddenInt = 0;
                }
                boolean deleted = jsonObject.getBoolean("deleted");
                int deletedInt;
                if(deleted){
                    deletedInt = 1;
                }else{
                    deletedInt = 0;
                }

                CategoryEntity categoryEntity = new CategoryEntity(categoryId, category_group_id, name, hiddenInt, deletedInt);
                categoryEntities.add(categoryEntity);
            }
//            AppDatabase.getAppDatabase(context).categoryDao().insertCategoryList(categoryEntities);

            JSONArray accountArray = budgetObject.getJSONArray("accounts");
            ArrayList<AccountEntity> accountEntities = new ArrayList<>();
            for(int t=0; t<accountArray.length(); t++){
                JSONObject jsonObject = accountArray.getJSONObject(t);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                boolean deleted = jsonObject.getBoolean("deleted");
                int balance = jsonObject.getInt("balance");
                int deletedInt;
                if(deleted) {
                    deletedInt = 1;
                }else{
                    deletedInt = 0;
                }
                AccountEntity accountEntity = new AccountEntity(id, name, balance, deletedInt);
                accountEntities.add(accountEntity);
            }
            Log.d(TAG, "parseAndSaveInputStream: " + accountEntities);
            AppDatabase.getAppDatabase(context).accountDao().insertAccountList(accountEntities);

            return Constants.RESULT_OK;

        }catch (JSONException e){
            Log.e(TAG, "parsePayeesString: ", e);
            return Constants.PARSE_ERROR;

        }

    }

}
