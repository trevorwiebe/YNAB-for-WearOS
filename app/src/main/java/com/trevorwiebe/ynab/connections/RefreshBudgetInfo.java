package com.trevorwiebe.ynab.connections;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.trevorwiebe.ynab.db.AppDatabase;
import com.trevorwiebe.ynab.db.entities.AccountEntity;
import com.trevorwiebe.ynab.db.entities.CategoryEntity;
import com.trevorwiebe.ynab.db.entities.PayeeEntity;
import com.trevorwiebe.ynab.db.entities.PayeeLocationEntity;
import com.trevorwiebe.ynab.utils.Constants;
import com.trevorwiebe.ynab.utils.Utility;

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

            String serverKnowledge = dataObject.getString("server_knowledge");
            Utility.saveServerKnowledge(context, serverKnowledge);

            JSONArray payeeArray = budgetObject.getJSONArray("payees");
            ArrayList<PayeeEntity> payeeEntities = new ArrayList<>();
            for(int r=0; r<payeeArray.length(); r++){
                JSONObject jsonObject = payeeArray.getJSONObject(r);
                String payeeId = jsonObject.getString("id");
                String payeeName = jsonObject.getString("name");
                String transfer_account_id = jsonObject.getString("transfer_account_id");
                boolean deleted = jsonObject.getBoolean("deleted");
                int deletedInt;
                if(deleted){
                    deletedInt = 1;
                }else{
                    deletedInt = 0;
                }

                PayeeEntity payeeEntity = new PayeeEntity(payeeId, payeeName, transfer_account_id, deletedInt);
                payeeEntities.add(payeeEntity);
            }
            AppDatabase.getAppDatabase(context).payeeDao().insertPayeeList(payeeEntities);


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
            AppDatabase.getAppDatabase(context).categoryDao().insertCategoryList(categoryEntities);

            JSONArray accountArray = budgetObject.getJSONArray("accounts");
            ArrayList<AccountEntity> accountEntities = new ArrayList<>();
            for(int t=0; t<accountArray.length(); t++){
                JSONObject jsonObject = accountArray.getJSONObject(t);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");

                int balance = jsonObject.getInt("balance");

                boolean deleted = jsonObject.getBoolean("deleted");
                int deletedInt;
                if(deleted) {
                    deletedInt = 1;
                }else{
                    deletedInt = 0;
                }

                boolean on_budget = jsonObject.getBoolean("on_budget");
                int on_budget_int;
                if(on_budget) {
                    on_budget_int = 0;
                }else{
                    on_budget_int = 1;
                }
                AccountEntity accountEntity = new AccountEntity(id, name, balance, on_budget_int, deletedInt);
                accountEntities.add(accountEntity);
            }
            AppDatabase.getAppDatabase(context).accountDao().insertAccountList(accountEntities);

            JSONArray payeeLocationArray = budgetObject.getJSONArray("payee_locations");
            ArrayList<PayeeLocationEntity> payeeLocationEntities = new ArrayList<>();
            for(int l=0; l<payeeLocationArray.length(); l++){
                JSONObject payeeLocationObject = payeeLocationArray.getJSONObject(l);

                String id = payeeLocationObject.getString("id");
                String payee_id = payeeLocationObject.getString("payee_id");
                String latitude = payeeLocationObject.getString("latitude");
                String longitude = payeeLocationObject.getString("longitude");
                boolean deleted = payeeLocationObject.getBoolean("deleted");
                int deletedInt;
                if(deleted){
                    deletedInt = 1;
                }else{
                    deletedInt = 0;
                }

                PayeeLocationEntity payeeLocationEntity = new PayeeLocationEntity(id, payee_id, latitude, longitude, deletedInt);
                payeeLocationEntities.add(payeeLocationEntity);
            }

            AppDatabase.getAppDatabase(context).payeeLocationDao().insertPayeeLocationEntities(payeeLocationEntities);

            return Constants.RESULT_OK;

        }catch (JSONException e){
            Log.e(TAG, "parsePayeesString: ", e);
            return Constants.PARSE_ERROR;

        }

    }

}
