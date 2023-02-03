package com.megha.tatti.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.example.mahesh.knowyourgovt.R;


public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView officeNameView;
    public TextView officialNameView;
    public ViewHolder(View view) {
        super(view);
        officeNameView = (TextView) itemView.findViewById(R.id.officeName);
        officialNameView = (TextView) itemView.findViewById(R.id.officialName);
    }
}
