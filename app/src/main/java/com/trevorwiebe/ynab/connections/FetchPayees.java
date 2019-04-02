package com.trevorwiebe.ynab.connections;

import android.os.AsyncTask;
import android.util.Log;

import com.trevorwiebe.ynab.db.entities.PayeeEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class FetchPayees extends AsyncTask<URL, Void, ArrayList<PayeeEntity>> {

    private static final String TAG = "FetchPayees";

    private ArrayList<PayeeEntity> mPayeesList = new ArrayList<>();
    public OnPayeesReturned onPayeesReturned;

    public FetchPayees(OnPayeesReturned onPayeesReturned){
        this.onPayeesReturned = onPayeesReturned;
    }

    public interface OnPayeesReturned{
        void onPayeesReturned(ArrayList<PayeeEntity> payeeEntities);
    }

    @Override
    protected ArrayList<PayeeEntity> doInBackground(URL... urls) {

        URL url = urls[0];

        Log.d(TAG, "doInBackground: " + url.toString());

        try {

            URLConnection urlConnection = url.openConnection();
            InputStream in = urlConnection.getInputStream();

            String result = inputStreamToString(in);

            return parsePayeesString(result);

        }catch (IOException e){
            return null;
        }

    }

    @Override
    protected void onPostExecute(ArrayList<PayeeEntity> payeeEntities) {
        super.onPostExecute(payeeEntities);
        onPayeesReturned.onPayeesReturned(payeeEntities);
    }

    public String inputStreamToString(InputStream inputStream) throws IOException {
        try(ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            return result.toString();
        }
    }

    private ArrayList<PayeeEntity> parsePayeesString(String result){

        ArrayList<PayeeEntity> payeeEntities = new ArrayList<>();

        try {
            JSONObject mainObject = new JSONObject(result).getJSONObject("data");
            JSONArray payeeArray = mainObject.getJSONArray("payees");

            Log.d(TAG, "parsePayeesString: " + payeeArray.toString());

            for(int r=0; r<payeeArray.length(); r++){
                JSONObject jsonObject = payeeArray.getJSONObject(r);
                String payeeId = jsonObject.getString("id");
                String payeeName = jsonObject.getString("name");
                String transfer_account_id = jsonObject.getString("transfer_account_id");
                boolean deleted = jsonObject.getBoolean("deleted");

                PayeeEntity payeeEntity = new PayeeEntity(payeeId, payeeName, transfer_account_id, deleted);

                payeeEntities.add(payeeEntity);
            }

            return payeeEntities;

        }catch (JSONException e){
            Log.e(TAG, "parsePayeesString: ", e);
            return null;

        }

    }
}
