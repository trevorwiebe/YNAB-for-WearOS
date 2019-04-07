package com.trevorwiebe.ynab.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.ynab.db.AppDatabase;
import com.trevorwiebe.ynab.db.entities.CategoryEntity;

import java.util.ArrayList;
import java.util.List;

public class QueryCategories extends AsyncTask<Context, Void, ArrayList<CategoryEntity>> {

    private OnCategoriesReturned onCategoriesReturned;

    public QueryCategories(OnCategoriesReturned onCategoriesReturned){
        this.onCategoriesReturned = onCategoriesReturned;
    }

    public interface OnCategoriesReturned{
        void onCategoriesReturned(ArrayList<CategoryEntity> categoryEntities);
    }

    @Override
    protected ArrayList<CategoryEntity> doInBackground(Context... contexts) {
        List<CategoryEntity> categoryEntities = AppDatabase.getAppDatabase(contexts[0]).categoryDao().getCategoryList();
        return (ArrayList<CategoryEntity>) categoryEntities;
    }

    @Override
    protected void onPostExecute(ArrayList<CategoryEntity> categoryEntities) {
        super.onPostExecute(categoryEntities);
        onCategoriesReturned.onCategoriesReturned(categoryEntities);
    }

}
