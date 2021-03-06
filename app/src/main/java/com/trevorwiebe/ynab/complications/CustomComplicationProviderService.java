package com.trevorwiebe.ynab.complications;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationManager;
import android.support.wearable.complications.ComplicationProviderService;
import android.support.wearable.complications.ComplicationText;
import android.util.Log;

import androidx.annotation.Nullable;

import com.trevorwiebe.ynab.MainActivity;
import com.trevorwiebe.ynab.dataLoaders.QueryCategoryById;
import com.trevorwiebe.ynab.db.AppDatabase;
import com.trevorwiebe.ynab.db.dao.CategoryDao;
import com.trevorwiebe.ynab.db.entities.CategoryEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CustomComplicationProviderService extends ComplicationProviderService implements
    QueryCategoryById.OnCategoryByIdReturned{

    private static final String TAG = "ComplicationProvider";

    /*
     * Called when a complication has been activated. The method is for any one-time
     * (per complication) set-up.
     *
     * You can continue sending data for the active complicationId until onComplicationDeactivated()
     * is called.
     */
    @Override
    public void onComplicationActivated(int complicationId, int dataType, ComplicationManager complicationManager) {

    }

    @Override
    public void onComplicationUpdate(int complicationId, int dataType, ComplicationManager complicationManager) {
        Log.d(TAG, "onComplicationUpdate() id: " + complicationId);

        new QueryCategoryById("d817dfd9-2a1e-4422-a9ab-95a0c9dce94c", CustomComplicationProviderService.this, complicationManager, complicationId, dataType).execute(CustomComplicationProviderService.this);
    }

    /*
     * Called when the complication has been deactivated.
     */
    @Override
    public void onComplicationDeactivated(int complicationId) {
        Log.d(TAG, "onComplicationDeactivated(): " + complicationId);
    }


    @Override
    public void onCategoryByIdReturned(CategoryEntity categoryEntity, @Nullable ComplicationManager complicationManager, @Nullable Integer complicationId, @Nullable Integer dataType) {

        if(complicationManager == null || complicationId == null || dataType == null) return;

        ComplicationData complicationData = null;

        long balance = categoryEntity.getBalance();
        String balanceStr;
        if(balance != 0) {
            BigDecimal unscaled = new BigDecimal(balance);
            BigDecimal scaled = unscaled.scaleByPowerOfTen(-3).setScale(2, RoundingMode.HALF_UP);
            balanceStr = scaled.toPlainString();
        }else{
            balanceStr = "0";
        }

        Intent wearOsIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, wearOsIntent, 0);

        long goal_target = categoryEntity.getGoal_target();

        switch (dataType){
            case ComplicationData.TYPE_SHORT_TEXT:
                complicationData =
                        new ComplicationData.Builder(ComplicationData.TYPE_SHORT_TEXT)
                                .setShortText(ComplicationText.plainText("$" + balanceStr))
                                .setTapAction(pendingIntent)
                                .build();
                break;
            case ComplicationData.TYPE_RANGED_VALUE:
                complicationData =
                        new ComplicationData.Builder(ComplicationData.TYPE_RANGED_VALUE)
                                .setValue(balance)
                                .setMaxValue(goal_target)
                                .setMinValue(0)
                                .setShortText(ComplicationText.plainText("$" + balanceStr))
                                .setTapAction(pendingIntent)
                                .build();
                break;
            case ComplicationData.TYPE_LONG_TEXT:
                complicationData =
                        new ComplicationData.Builder(ComplicationData.TYPE_LONG_TEXT)
                                .setLongTitle(ComplicationText.plainText(categoryEntity.getName()))
                                .setLongText(ComplicationText.plainText("$" + balanceStr))
                                .setTapAction(pendingIntent)
                                .build();
                break;
            default:
                if(Log.isLoggable(TAG, Log.WARN)){
                    Log.w(TAG, "onComplicationUpdate: Unexpected complication type" + dataType);
                }
        }

        if(complicationData != null){
            complicationManager.updateComplicationData(complicationId, complicationData);
        }else{
            complicationManager.noUpdateRequired(complicationId);
        }
    }
}
