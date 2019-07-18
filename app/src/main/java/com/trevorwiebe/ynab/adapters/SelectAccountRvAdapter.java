package com.trevorwiebe.ynab.adapters;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.ynab.R;
import com.trevorwiebe.ynab.db.entities.AccountEntity;

import java.util.ArrayList;

public class SelectAccountRvAdapter extends RecyclerView.Adapter<SelectAccountRvAdapter.SelectAccountViewHolder> {

    public ArrayList<AccountEntity> accountEntities;

    public SelectAccountRvAdapter(ArrayList<AccountEntity> accountEntities){
        this.accountEntities = accountEntities;
    }

    @Override
    public int getItemCount() {
        if(accountEntities == null)return 0;
        return accountEntities.size();
    }

    @NonNull
    @Override
    public SelectAccountViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View accountView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_select, viewGroup, false);
        return new SelectAccountViewHolder(accountView);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectAccountViewHolder selectAccountViewHolder, int i) {
        AccountEntity accountEntity = accountEntities.get(i);
        String accountNameText = accountEntity.getName();
        selectAccountViewHolder.accountName.setText(accountNameText);
    }

    public void swapData(ArrayList<AccountEntity> newAccountEntities){
        accountEntities = new ArrayList<>(newAccountEntities);
        notifyDataSetChanged();
    }

    public class SelectAccountViewHolder extends RecyclerView.ViewHolder{

        private TextView accountName;

        public SelectAccountViewHolder(View view){
            super(view);
            accountName = view.findViewById(R.id.select_item);
        }
    }
}
