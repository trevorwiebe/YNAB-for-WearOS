package com.trevorwiebe.ynab.adapters;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.ynab.R;
import com.trevorwiebe.ynab.db.entities.PayeeEntity;

import java.util.ArrayList;

public class SelectPayeeRvAdapter extends RecyclerView.Adapter<SelectPayeeRvAdapter.SelectPayeeViewHolder> {

    private ArrayList<PayeeEntity> payeeEntities;

    @Override
    public int getItemCount() {
        if(payeeEntities == null) return 0;
        return payeeEntities.size();
    }

    @NonNull
    @Override
    public SelectPayeeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_select, viewGroup, false);
        return new SelectPayeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectPayeeViewHolder selectPayeeViewHolder, int i) {

        PayeeEntity payeeEntity = payeeEntities.get(i);
        String payeeName = payeeEntity.getName();

        selectPayeeViewHolder.payee.setText(payeeName);
    }

    public void swapData(ArrayList<PayeeEntity> payeeEntities){
        this.payeeEntities = new ArrayList<>(payeeEntities);
        notifyDataSetChanged();
    }

    public class SelectPayeeViewHolder extends RecyclerView.ViewHolder{

        private TextView payee;

        public SelectPayeeViewHolder(View view){
            super(view);

            payee = view.findViewById(R.id.select_item);
        }
    }

}
