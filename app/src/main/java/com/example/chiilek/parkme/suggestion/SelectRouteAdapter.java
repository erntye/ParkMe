package com.example.chiilek.parkme.suggestion;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Steven T on 05/04/2018.
 */


import com.example.chiilek.parkme.R;
import com.example.chiilek.parkme.data_classes.DirectionsAndCPInfo;
import com.example.chiilek.parkme.navigation.RouteOverviewActivity;

import java.lang.reflect.Field;
import java.util.List;

public class SelectRouteAdapter extends RecyclerView.Adapter<SelectRouteAdapter.MyViewHolder> {

    private List<DirectionsAndCPInfo> carparkList;
    private Context mContext;

    public SelectRouteAdapter(List<DirectionsAndCPInfo> carparkList, Context context) {
        this.carparkList= carparkList;
        this.mContext = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.carpark_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    //binds each seperate carpark to it view
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DirectionsAndCPInfo CP = carparkList.get(position);

        //needed to get the image ID
        Class res = R.drawable.class;
        Field field = null;
        int drawableID = 0;
        try {
            field = res.getField("parking_suggestion_box");
            drawableID = field.getInt(null);
        } catch (Exception e){
            Log.e("ERROR", "Failed to get drawable id", e);
        }
        //----------------------------------------------------------------

        holder.buttonImage.setImageResource(drawableID);
        holder.address.setText(CP.getCarParkStaticInfo().getAddress());
        holder.distance.setText(Double.toString(CP.getDistance()) + "m away");
        holder.availability.setText(CP.getAvailability()+ " Available Lots");
        holder.timeToReach.setText(CP.getDuration()/60+ " minutes away");
        holder.parentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d("SelectRouteAdapter", "onClick: clicked on: " + CP.getCarParkStaticInfo().getCPNumber());
                Toast.makeText(mContext, CP.getCarParkStaticInfo().getCPNumber(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mContext, RouteOverviewActivity.class);
                intent.putExtra("chosenRoute", carparkList.get(position));
                intent.putExtra("destinationAddress", carparkList.get(position).getCarParkStaticInfo().getAddress());
                Log.d("SuggestionsAdapter", "starting new context with DirectionsAndCPInfo");
                mContext.startActivity(intent);
            }
        });
    }

    //function to allow activity to add entire list at one time
    public void addItems(List<DirectionsAndCPInfo> carparkList){
        this.carparkList = carparkList;
        notifyDataSetChanged();
    }

    //function to get a certain DirectionsAndCPInfo
    public DirectionsAndCPInfo getCarParkInfo(int position){
        return carparkList.get(position);
    }
    @Override
    public int getItemCount() {
        return carparkList.size();
    }


    //This class holds all the Views inside one rectangle in the recycler view
    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView address;
        private TextView timeToReach;
        private TextView distance;
        private TextView availability;
        private ImageView buttonImage;
        private RelativeLayout parentLayout;


        public MyViewHolder(View view) {
            super(view);

            parentLayout = view.findViewById(R.id.parent_layout);

            distance = view.findViewById(R.id.distance);
            address = view.findViewById(R.id.address);
            timeToReach = view.findViewById(R.id.time);
            availability = view.findViewById(R.id.availability);
            buttonImage = view.findViewById(R.id.parking_suggestion_box_png);
        }

    }
}
