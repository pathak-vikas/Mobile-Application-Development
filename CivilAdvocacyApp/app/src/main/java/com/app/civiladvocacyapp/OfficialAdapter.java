package com.app.civiladvocacyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<ViewHolder>{

    private List<Official> officialList;
    private MainActivity mainAct;

    public OfficialAdapter(List<Official> officialList, MainActivity mainAct) {
        this.officialList = officialList;
        this.mainAct = mainAct;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row,parent, false);
        view.setOnClickListener(mainAct);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Official official = officialList.get(position);
        if (official.getParty() == null) holder.row_name.setText(official.getName());
        else holder.row_name.setText(official.getName()+'('+official.getParty()+')');
        holder.row_office.setText(official.getOfficeName());
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}