package com.trevorwiebe.ynab.adapters;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.ynab.R;
import com.trevorwiebe.ynab.db.entities.AccountEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SelectAccountRvAdapter extends RecyclerView.Adapter<SelectAccountRvAdapter.SelectAccountViewHolder> {

    public ArrayList<AccountEntity> accountEntities;

    private NumberFormat mNumberFormat = DecimalFormat.getNumberInstance(Locale.getDefault());

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
        View accountView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.account_list_select, viewGroup, false);
        return new SelectAccountViewHolder(accountView);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectAccountViewHolder selectAccountViewHolder, int i) {
        AccountEntity accountEntity = accountEntities.get(i);
        String accountNameText = accountEntity.getName();
        selectAccountViewHolder.accountName.setText(accountNameText);

        long accountBalance = accountEntity.getBalance();

        if(accountBalance == 0){
            selectAccountViewHolder.accountBalance.setText("$0.00");
            selectAccountViewHolder.accountBalance.setVisibility(View.VISIBLE);
            selectAccountViewHolder.accountBalanceNeg.setVisibility(View.GONE);
        }else {

            BigDecimal unscaled = new BigDecimal(accountBalance);
            BigDecimal scaled = unscaled.scaleByPowerOfTen(-3).setScale(2, RoundingMode.HALF_UP);
            String balanceStr = "$" + mNumberFormat.format(scaled);

            if(accountBalance < 0){
                selectAccountViewHolder.accountBalanceNeg.setText(balanceStr);

                selectAccountViewHolder.accountBalance.setVisibility(View.GONE);
                selectAccountViewHolder.accountBalanceNeg.setVisibility(View.VISIBLE);
            }else {
                selectAccountViewHolder.accountBalance.setText(balanceStr);

                selectAccountViewHolder.accountBalance.setVisibility(View.VISIBLE);
                selectAccountViewHolder.accountBalanceNeg.setVisibility(View.GONE);
            }
        }
    }

    public void swapData(ArrayList<AccountEntity> newAccountEntities){
        accountEntities = new ArrayList<>(newAccountEntities);
        notifyDataSetChanged();
    }

    public class SelectAccountViewHolder extends RecyclerView.ViewHolder{

        private TextView accountName;
        private TextView accountBalance;
        private TextView accountBalanceNeg;

        public SelectAccountViewHolder(View view){
            super(view);
            accountName = view.findViewById(R.id.select_item);
            accountBalance = view.findViewById(R.id.account_balance);
            accountBalanceNeg = view.findViewById(R.id.account_balance_neg);
        }
    }
}
