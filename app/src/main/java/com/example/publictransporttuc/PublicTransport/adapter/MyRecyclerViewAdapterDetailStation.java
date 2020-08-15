package com.example.publictransporttuc.PublicTransport.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.publictransporttuc.NetworkModels.Departure;
import com.example.publictransporttuc.NetworkModels.Station;
import com.example.publictransporttuc.R;
import com.example.publictransporttuc.Util.ObjectSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class MyRecyclerViewAdapterDetailStation extends RecyclerView.Adapter<MyRecyclerViewAdapterDetailStation.ViewHolder> {

    private List<Departure> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    boolean unchecked = true;

    // data is passed into the constructor
    public MyRecyclerViewAdapterDetailStation(Context context, List<Departure> departures) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = departures;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.pt_s_detail_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Departure departure = mData.get(position);
        String departureName1 = departure.getNumber();
        String departureDirection1 = departure.getDirection();
        String departureTime1 = getDate(departure.getDepartureTime());
        String departureType1 = departure.getType();

        holder.departureName1TV.setText(departureName1);
        holder.departureDirection1TV.setText(departureDirection1);
        holder.departureTime1TV.setText(departureTime1);
        if(departureType1.matches("TRAM")){
            holder.departureType1IV.setImageDrawable(holder.tram);
        }else {
            holder.departureType1IV.setImageDrawable(holder.bus);
        }


    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("hh:mm", cal).toString();
        return date;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView departureName1TV;
        TextView departureDirection1TV;
        TextView departureTime1TV;
        ImageView departureType1IV;
        Drawable bus;
        Drawable tram;

        ViewHolder(View itemView) {
            super(itemView);
            departureName1TV = itemView.findViewById(R.id.name);
            departureDirection1TV = itemView.findViewById(R.id.direction);
            departureTime1TV = itemView.findViewById(R.id.time);
            departureType1IV = itemView.findViewById(R.id.type);

            bus = itemView.getResources().getDrawable(R.drawable.ic_bus);
            tram = itemView.getResources().getDrawable(R.drawable.ic_tram);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    /*String getItem(int id) {
        return mData.get(id);
    }*/

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}