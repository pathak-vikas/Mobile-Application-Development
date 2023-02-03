package com.app.civiladvocacyapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView row_name;
    public TextView row_office;

    public ViewHolder(View itemView) {
        super(itemView);
        row_name = (TextView) itemView.findViewById(R.id.row_nameText);
        row_office = (TextView) itemView.findViewById(R.id.row_officeText);
    }

}