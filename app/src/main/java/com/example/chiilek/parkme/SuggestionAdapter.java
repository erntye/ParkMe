package com.example.chiilek.parkme;

import android.content.Context;
import android.nfc.Tag;
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




import com.example.chiilek.parkme.data_classes.CarParkStaticInfo;

import java.lang.reflect.Field;
import java.util.List;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.MyViewHolder> {

    private List<CarParkStaticInfo> carparkList;
    private Context mContext;

    public SuggestionAdapter(List<CarParkStaticInfo> carparkList, Context context) {
        this.carparkList= carparkList;
        this.mContext = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.carpark_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CarParkStaticInfo CP = carparkList.get(position);

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

        holder.buttonImage.setImageResource(drawableID);
        holder.CPNumber.setText(CP.getCPNumber());
        holder.address.setText(CP.getAddress());
        holder.parentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d("test", "onClick: clicked on: " + CP.getCPNumber());
                Toast.makeText(mContext, CP.getCPNumber(), Toast.LENGTH_SHORT).show();
            }
        });

    }




    @Override
    public int getItemCount() {
        return carparkList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView CPNumber,address;
        private ImageView buttonImage;
        private RelativeLayout parentLayout;


        public MyViewHolder(View view) {
            super(view);
            parentLayout = view.findViewById(R.id.parent_layout);
            CPNumber = view.findViewById(R.id.CPNumber);
            address = view.findViewById(R.id.address);
            buttonImage = view.findViewById(R.id.parking_suggestion_box_png);

        }

    }
}
