package com.trevorwiebe.ynab.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.ynab.db.AppDatabase;
import com.trevorwiebe.ynab.db.entities.CategoryEntity;

public class QueryCategoryById extends AsyncTask<Context, Void, CategoryEntity> {

    private String categoryId;
    private OnCategoryByIdReturned onCategoryByIdReturned;

    public QueryCategoryById(String categoryId, OnCategoryByIdReturned onCategoryByIdReturned){
        this.categoryId = categoryId;
        this.onCategoryByIdReturned = onCategoryByIdReturned;
    }

    public interface OnCategoryByIdReturned{
        void onCategoryByIdReturned(CategoryEntity categoryEntity);
    }

    @Override
    protected CategoryEntity doInBackground(Context... contexts) {
        return AppDatabase.getAppDatabase(contexts[0]).categoryDao().getCategoryById(categoryId);
    }

    @Override
    protected void onPostExecute(CategoryEntity categoryEntity) {
        super.onPostExecute(categoryEntity);
        onCategoryByIdReturned.onCategoryByIdReturned(categoryEntity);
    }
}
