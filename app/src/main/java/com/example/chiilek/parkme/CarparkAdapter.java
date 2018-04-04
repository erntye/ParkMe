package com.example.chiilek.parkme;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View;

/**
 * Created by Steven T on 05/04/2018.
 */




import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;

import java.util.List;

public class CarparkAdapter extends RecyclerView.Adapter<CarparkAdapter.MyViewHolder> {

    private List<CarParkStaticInfo> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView CPNumber,address;

        public MyViewHolder(View view) {
            super(view);
            CPNumber = (TextView) view.findViewById(R.id.CPNumber);
            address = (TextView) view.findViewById(R.id.address);
        }
    }


    public CarparkAdapter(List<CarParkStaticInfo> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.carpark_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CarParkStaticInfo CP = moviesList.get(position);
        holder.CPNumber.setText(CP.getCPNumber());
        holder.address.setText(CP.getAddress());

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}