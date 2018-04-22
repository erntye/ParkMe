package com.example.chiilek.parkme.ui;

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

import com.example.chiilek.parkme.R;
import com.example.chiilek.parkme.entity.DirectionsAndCPInfo;

import java.lang.reflect.Field;
import java.util.List;


/**
 * This <code>Adapter</code> is responsible for managing the <code>View Holder</code>s objects, each of which
 *  will display navigation information about each recommended car park in the <code>SelectRouteActivity</code>.
 *  @see SelectRouteActivity
 *  @see RecyclerView.Adapter
 *  @see RecyclerView
 *  @see MyViewHolder
 */
public class SelectRouteAdapter extends RecyclerView.Adapter<SelectRouteAdapter.MyViewHolder> {

    private List<DirectionsAndCPInfo> carParkList;
    private Context mContext;
    private int status;

    public SelectRouteAdapter(List<DirectionsAndCPInfo> carparkList, Context context, int status) {
        this.carParkList= carparkList;
        this.mContext = context;
        this.status = status;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.carpark_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    /**
     * Binds each separate <code>DirectionsAndCPInfo</code> object to its <code>View</code>.
     * @param holder A <code>MyViewHolder</code> object to contain the corresponding car park information.
     * @param position An <code>integer</code> indicating its position within the <code>carParkList</code> object.
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DirectionsAndCPInfo CP = carParkList.get(position);
        //needed to get the image ID
        Class res = R.drawable.class;
        Field field = null;
        int drawableID = 0;
        try {
            field = res.getField("parking_suggestion_box");
            drawableID = field.getInt(null);
        } catch (Exception e) {
            Log.e("ERROR", "Failed to get drawable id", e);
        }
        //----------------------------------------------------------------

        holder.buttonImage.setImageResource(drawableID);
        holder.address.setText(CP.getCarParkInfo().getAddress());
        //holder.distance.setText(String.format("%f m away",CP.getDistance()));
        holder.distance.setText((int)CP.getDistance() + "m away");
        holder.availability.setText(CP.getAvailability() + " Available Lots");
        holder.timeToReach.setText(CP.getDuration() / 60 + " minutes away");
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("SelectRouteAdapter", "onClick: clicked on: " + CP.getCarParkInfo().getCPNumber());
                Toast.makeText(mContext, CP.getCarParkInfo().getCPNumber(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mContext, RouteOverviewActivity.class);
                intent.putExtra("chosenRoute", carParkList.get(position));
                intent.putExtra("destinationAddress", carParkList.get(position).getCarParkInfo().getAddress());
                Log.d("SuggestionsAdapter", "starting new context with DirectionsAndCPInfo");
                mContext.startActivity(intent);
            }
        });

    }

    //function to allow activity to add entire list at one time
    public void addItems(List<DirectionsAndCPInfo> carparkList, int status){
        this.status = status;
        this.carParkList = carparkList;
        notifyDataSetChanged();
    }

    //function to get a certain DirectionsAndCPInfo
    public DirectionsAndCPInfo getCarParkInfo(int position){
        return carParkList.get(position);
    }
    @Override
    public int getItemCount() {
        return carParkList.size();
    }


    /**
     * This <code>Class</code> hods all the <code>View</code>s inside one <code>View Group</code> in the
     * <code>Recycler View</code>.
     * @see RecyclerView
     * @see android.support.v7.widget.RecyclerView.ViewHolder
     * @see SelectRouteAdapter
     * @see SelectRouteActivity
     */
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
