package com.megha.tatti.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.mahesh.knowyourgovt.R;
import java.util.List;


public class Govt_Adapter extends RecyclerView.Adapter<ViewHolder> {
    MainActivity mainActivity;
    List<MyGovernment> governmentList;

    public Govt_Adapter(MainActivity mainActivity, List<MyGovernment> governmentList) {
        this.mainActivity =  mainActivity;
        this.governmentList = governmentList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View thisItemsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_governist,
                parent, false);
        thisItemsView.setOnClickListener(mainActivity);
        return new ViewHolder(thisItemsView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyGovernment myGovernment = governmentList.get(position);
        holder.officeNameView.setText(myGovernment.getOfficeName());
        if(myGovernment.getOfficial().getPartyName()==null)
        {
            holder.officialNameView.setText(myGovernment.getOfficial().getName());
        }
        else
        {
            holder.officialNameView.setText(myGovernment.getOfficial().getName()+ "( "+ myGovernment.getOfficial().getPartyName()+ ")");
        }


    }

    @Override
    public int getItemCount() {
        return governmentList.size();
    }
}
