package com.trevorwiebe.ynab.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.trevorwiebe.ynab.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utility {

    public static String convertMillisToDate(long millis){
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        Date date = new Date(millis);
        return formatter.format(date);
    }

    public static String convertMillisToTime(long millis){
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy - h:mm a", Locale.getDefault());
        Date date = new Date(millis);
        return formatter.format(date);
    }

    public static boolean hasInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static void saveServerKnowledge(Context context, String serverKnowledge){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.server_knowledge_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.server_knowledge_key), serverKnowledge);
        editor.apply();
    }

    public static String getServerKnowledge(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.server_knowledge_name), Context.MODE_PRIVATE);
        return sharedPreferences.getString(context.getResources().getString(R.string.server_knowledge_key), "");
    }

    public static void saveLastSynced(Context context, long lastSynced){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.last_synced_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(context.getResources().getString(R.string.last_synced_key), lastSynced);
        editor.apply();
    }

    public static long getLastSynced(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.last_synced_name), Context.MODE_PRIVATE);
        return sharedPreferences.getLong(context.getResources().getString(R.string.last_synced_key), 0);
    }

}
