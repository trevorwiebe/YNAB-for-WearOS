package com.trevorwiebe.ynab.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.trevorwiebe.ynab.MainActivity;
import com.trevorwiebe.ynab.db.AppDatabase;
import com.trevorwiebe.ynab.db.entities.AccountEntity;
import com.trevorwiebe.ynab.db.entities.CategoryEntity;
import com.trevorwiebe.ynab.db.entities.PayeeEntity;
import com.trevorwiebe.ynab.db.entities.PayeeLocationEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static com.trevorwiebe.ynab.utils.Constants.BASE_URL;
import static com.trevorwiebe.ynab.utils.Constants.BUDGET_ID;
import static com.trevorwiebe.ynab.utils.Constants.PERSONAL_ACCESS_TOKEN;

public class SyncToDatabaseWorkManger extends Worker {

    private static final String TAG = "SyncToDatabaseWorkMange";

    private Context mContext;

    public SyncToDatabaseWorkManger (@NonNull Context context, @NonNull WorkerParameters workerParams){
        super(context, workerParams);
        this.mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        try{
            String strUrl;

            String server_knowledge = Utility.getServerKnowledge(mContext);
            if(server_knowledge.length() == 0){
                strUrl = BASE_URL + "/budgets/" + BUDGET_ID + "" + "?access_token=" + PERSONAL_ACCESS_TOKEN;
            }else {
                strUrl = BASE_URL + "/budgets/" + BUDGET_ID + "" + "?access_token=" + PERSONAL_ACCESS_TOKEN + "&last_knowledge_of_server=" + server_knowledge;
            }

            URL url = new URL(strUrl);

            try {

                URLConnection urlConnection = url.openConnection();
                InputStream in = urlConnection.getInputStream();

                String result = inputStreamToString(in);

                int resultCode = parseAndSaveInputStream(result);

                if(resultCode == Constants.RESULT_OK) {
                    Utility.saveLastSynced(mContext, System.currentTimeMillis());
                }

            } catch (IOException e) {
                Log.e(TAG, "doWork: ", e);
            }

        }catch (MalformedURLException e){
            Log.e(TAG, "onCreate: ", e);
        }

        return null;

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
            Utility.saveServerKnowledge(mContext, serverKnowledge);

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
            AppDatabase.getAppDatabase(mContext).payeeDao().insertPayeeList(payeeEntities);


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

                String goal_type = jsonObject.getString("goal_type");
                long goal_target = jsonObject.getLong("goal_target");
                long balance = jsonObject.getLong("balance");

                CategoryEntity categoryEntity = new CategoryEntity(categoryId, category_group_id, name, hiddenInt, deletedInt, goal_type, goal_target, balance);
                categoryEntities.add(categoryEntity);
            }
            AppDatabase.getAppDatabase(mContext).categoryDao().insertCategoryList(categoryEntities);

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
            AppDatabase.getAppDatabase(mContext).accountDao().insertAccountList(accountEntities);

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

            AppDatabase.getAppDatabase(mContext).payeeLocationDao().insertPayeeLocationEntities(payeeLocationEntities);

            return Constants.RESULT_OK;

        }catch (JSONException e){
            Log.e(TAG, "parsePayeesString: ", e);
            return Constants.PARSE_ERROR;

        }

    }

}
