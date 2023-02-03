package com.cs442.assignment3;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StockViewHolder extends RecyclerView.ViewHolder {

    TextView symbolTV;
    TextView companyTV;
    TextView lpriceTV;
    TextView changeTV;

    public StockViewHolder(@NonNull View itemView) {
        super(itemView);
        symbolTV=itemView.findViewById(R.id.symbolTV);
        companyTV=itemView.findViewById(R.id.companyTV);
        lpriceTV=itemView.findViewById(R.id.lpriceTV);
        changeTV=itemView.findViewById(R.id.changeTV);
    }


}
