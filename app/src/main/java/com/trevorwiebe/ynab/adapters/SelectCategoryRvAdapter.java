package com.trevorwiebe.ynab.adapters;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.ynab.R;
import com.trevorwiebe.ynab.db.entities.CategoryEntity;

import java.util.ArrayList;

public class SelectCategoryRvAdapter extends RecyclerView.Adapter<SelectCategoryRvAdapter.SelectCategoryViewHolder> {


    private ArrayList<CategoryEntity> categoryEntities = new ArrayList<>();

    @Override
    public int getItemCount() {
        if(categoryEntities == null) return 0;
        return categoryEntities.size();
    }

    @NonNull
    @Override
    public SelectCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_select, viewGroup, false);
        return new SelectCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectCategoryViewHolder selectCategoryViewHolder, int i) {

        CategoryEntity categoryEntity = categoryEntities.get(i);
        String categoryName = categoryEntity.getName();
        selectCategoryViewHolder.mCategory.setText(categoryName);

    }

    public void swapData(ArrayList<CategoryEntity> categoryEntities){
        this.categoryEntities = new ArrayList<>(categoryEntities);
        notifyDataSetChanged();
    }

    public class SelectCategoryViewHolder extends RecyclerView.ViewHolder{

        private TextView mCategory;

        public SelectCategoryViewHolder(View view){
            super(view);

            mCategory = view.findViewById(R.id.select_item);
        }
    }
}
