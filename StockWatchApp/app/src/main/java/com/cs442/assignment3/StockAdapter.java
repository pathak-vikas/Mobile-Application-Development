package com.cs442.assignment3;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {

    private static final String TAG = "StockAdapter";
    private List<Stock> stocks;
    private MainActivity mainAct;

    public StockAdapter(List<Stock> stocks, MainActivity mainAct) {
        this.stocks = stocks;
        this.mainAct = mainAct;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_rows, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: FILLING VIEW HOLDER Stocks " + position);

        Stock stock=stocks.get(position);

        holder.symbolTV.setText(stock.getSymbol());
        holder.companyTV.setText(stock.getCompanyName());
        holder.lpriceTV.setText(String.format("%.2f", stock.getLatestPrice()));
        String changeStr=String.format("%.2f", stock.getChange())+" ("+String.format("%.2f", stock.getChangePercent()*100.00)+"%)";
        if(stock.getChange()<0){
            changeStr="\u25BC "+changeStr;
            holder.changeTV.setTextColor(Color.RED);
            holder.lpriceTV.setTextColor(Color.RED);
            holder.companyTV.setTextColor(Color.RED);
            holder.symbolTV.setTextColor(Color.RED);
        }
        else{
            changeStr="\u25B2"+changeStr;
            holder.changeTV.setTextColor(Color.GREEN);
            holder.lpriceTV.setTextColor(Color.GREEN);
            holder.companyTV.setTextColor(Color.GREEN);
            holder.symbolTV.setTextColor(Color.GREEN);
        }

        holder.changeTV.setText(changeStr);


    }

    @Override
    public int getItemCount() {
        if(stocks!=null)
            return stocks.size();
        else
             return 0;
    }
}
