package com.example.chiilek.parkme.Suggestion;

import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
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
import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;
import com.example.chiilek.parkme.data_classes.DirectionsAndCPInfo;
import com.example.chiilek.parkme.navigation.RouteOverviewActivity;

import java.lang.reflect.Field;
import java.util.List;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.MyViewHolder> {

    private List<DirectionsAndCPInfo> carparkList;
    private Context mContext;

    public SuggestionAdapter(List<DirectionsAndCPInfo> carparkList, Context context) {
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
        //TODO add travel time and carpark availability
        //String distanceString = Integer.toString(position);
        holder.buttonImage.setImageResource(drawableID);
        holder.address.setText(CP.getCarParkStaticInfo().getAddress());
        holder.distance.setText(Integer.toString((int)CP.getDistance()) + "m away");
        holder.availability.setText("Number of available lots: "+ CP.getAvailability());
        holder.timeToReach.setText("Time away: "+ CP.getDuration());
        holder.parentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d("SuggestionAdapter", "onClick: clicked on: " + CP.getCarParkStaticInfo().getCPNumber());
                Toast.makeText(mContext, CP.getCarParkStaticInfo().getCPNumber(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mContext, RouteOverviewActivity.class);
                intent.putExtra("cp_pop_up", carparkList.get(position).getCarParkStaticInfo().getCPNumber());
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
