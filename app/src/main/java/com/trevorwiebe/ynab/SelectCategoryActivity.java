package com.trevorwiebe.ynab;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.view.View;

import com.trevorwiebe.ynab.adapters.SelectCategoryRvAdapter;
import com.trevorwiebe.ynab.dataLoaders.QueryCategories;
import com.trevorwiebe.ynab.db.entities.CategoryEntity;
import com.trevorwiebe.ynab.utils.ItemClickListener;

import java.util.ArrayList;

public class SelectCategoryActivity extends WearableActivity implements
        QueryCategories.OnCategoriesReturned {

    private static final String TAG = "SelectCategoryActivity";

    private SelectCategoryRvAdapter mSelectCategoryRvAdapter;
    private ArrayList<CategoryEntity> mCategoriesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);

        RecyclerView selectCategoryRv = findViewById(R.id.select_category_rv);
        selectCategoryRv.setLayoutManager(new LinearLayoutManager(this));
        mSelectCategoryRvAdapter = new SelectCategoryRvAdapter();
        selectCategoryRv.setAdapter(mSelectCategoryRvAdapter);

        selectCategoryRv.addOnItemTouchListener(new ItemClickListener(this, selectCategoryRv, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CategoryEntity categoryEntity = mCategoriesList.get(position);
                String categoryId = categoryEntity.getId();
                Intent intent = new Intent();
                intent.putExtra("selectedCategoryId", categoryId);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        new QueryCategories(this).execute(this);

    }

    @Override
    public void onCategoriesReturned(ArrayList<CategoryEntity> categoryEntities) {
        mCategoriesList = categoryEntities;
        mSelectCategoryRvAdapter.swapData(categoryEntities);
    }
}
