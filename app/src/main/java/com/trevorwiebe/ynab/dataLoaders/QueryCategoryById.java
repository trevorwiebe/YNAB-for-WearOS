package com.trevorwiebe.ynab.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationManager;

import androidx.annotation.Nullable;

import com.trevorwiebe.ynab.db.AppDatabase;
import com.trevorwiebe.ynab.db.entities.CategoryEntity;

public class QueryCategoryById extends AsyncTask<Context, Void, CategoryEntity> {

    private String categoryId;
    private OnCategoryByIdReturned onCategoryByIdReturned;

    private ComplicationManager complicationManager;
    private Integer complicationId;
    private Integer dataType;

    public QueryCategoryById(String categoryId, OnCategoryByIdReturned onCategoryByIdReturned, @Nullable ComplicationManager complicationManager, @Nullable Integer complicationId, @Nullable Integer datatype){
        this.categoryId = categoryId;
        this.onCategoryByIdReturned = onCategoryByIdReturned;
        this.complicationManager = complicationManager;
        this.complicationId = complicationId;
        this.dataType = datatype;
    }

    public interface OnCategoryByIdReturned{
        void onCategoryByIdReturned(CategoryEntity categoryEntity, @Nullable ComplicationManager complicationManager, @Nullable Integer complicationId, @Nullable Integer datatype);
    }

    @Override
    protected CategoryEntity doInBackground(Context... contexts) {
        return AppDatabase.getAppDatabase(contexts[0]).categoryDao().getCategoryById(categoryId);
    }

    @Override
    protected void onPostExecute(CategoryEntity categoryEntity) {
        super.onPostExecute(categoryEntity);
        onCategoryByIdReturned.onCategoryByIdReturned(categoryEntity, complicationManager, complicationId, dataType);
    }
}
