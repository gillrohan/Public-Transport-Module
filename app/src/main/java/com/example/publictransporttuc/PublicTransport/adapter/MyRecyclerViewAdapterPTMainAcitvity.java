package com.example.publictransporttuc.PublicTransport.adapter;

import android.content.Context;
import android.content.Intent;
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

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.publictransporttuc.NetworkModels.Departure;
import com.example.publictransporttuc.NetworkModels.Station;
import com.example.publictransporttuc.PublicTransport.ui.StationDetailActivity;
import com.example.publictransporttuc.R;
import com.example.publictransporttuc.Util.ObjectSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class MyRecyclerViewAdapterPTMainAcitvity extends RecyclerView.Adapter<MyRecyclerViewAdapterPTMainAcitvity.ViewHolder> {

    private List<Station> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    boolean unchecked = true;

    // data is passed into the constructor
    public MyRecyclerViewAdapterPTMainAcitvity(Context context, List<Station> stations) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = stations;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.pt_main_activity_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String stationName = mData.get(position).getName();
        //String stationDistance = Integer.toString(mData.get(position).getDistance());
        //stationDistance = stationDistance.concat("m");

        List<Departure> departureList = mData.get(position).getDepartures();
        String departureName1 = departureList.get(0).getNumber();
        String departureDirection1 = departureList.get(0).getDirection();
        String departureTime1 = getDate(departureList.get(0).getDepartureTime());
        String departureType1 = departureList.get(0).getType();

        if(departureList.size() > 1) {
            String departureName2 = departureList.get(1).getNumber();
            String departureDirection2 = departureList.get(1).getDirection();
            String departureTime2 = getDate(departureList.get(1).getDepartureTime());
            String departureType2 = departureList.get(1).getType();
            holder.departureName2TV.setText(departureName2);
            holder.departureDirection2TV.setText(departureDirection2);
            holder.departureTime2TV.setText(departureTime2);
            if(departureType2.matches("TRAM")){
                holder.departureType2IV.setImageDrawable(holder.tram);
            }else {
                holder.departureType2IV.setImageDrawable(holder.bus);
            }
        }else {
            holder.departureName2TV.setText("N/A");
            holder.departureDirection2TV.setText("No more Departures");
            holder.departureTime2TV.setText("N/A");
        }

        holder.stationNameTV.setText(stationName);
        //holder.stationDistanceTV.setText(stationDistance);
        holder.departureName1TV.setText(departureName1);
        holder.departureDirection1TV.setText(departureDirection1);
        holder.departureTime1TV.setText(departureTime1);
        if(departureType1.matches("TRAM")){
            holder.departureType1IV.setImageDrawable(holder.tram);
        }else {
            holder.departureType1IV.setImageDrawable(holder.bus);
        }




        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("FavoritePT", MODE_PRIVATE);
        try {
            ArrayList<Station> fav = (ArrayList<Station>) ObjectSerializer.deserialize(pref.getString("favorite", null));

            if (fav != null) {
                for (int index = 0; index < fav.size(); index++) {
                    Log.e(TAG, "fav: "+fav.get(index).getId());
                    Log.e(TAG, "mData: "+mData.get(position).getId());
                    if (fav.get(index).getId().equals(mData.get(position).getId())) {
                        Log.d(TAG, "onBindViewHolder: Found");
                        holder.favorite.setImageDrawable(holder.checkedFavorite);
                        unchecked = false;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(unchecked) {
                    holder.favorite.setImageDrawable(holder.checkedFavorite);
                    unchecked = false;
                    SharedPreferences pref = context.getApplicationContext().getSharedPreferences("FavoritePT", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    try {
                        ArrayList<Station> fav = (ArrayList<Station>) ObjectSerializer.deserialize(pref.getString("favorite", null));
                        if(fav == null){
                            fav = new ArrayList<>();
                        }
                        fav.add(mData.get(position));
                        Log.d(TAG, "onClick: Add Done");
                        editor.putString("favorite", ObjectSerializer.serialize(fav));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    editor.commit();
                }else{
                    holder.favorite.setImageDrawable(holder.uncheckedFavorite);
                    unchecked = true;
                    SharedPreferences pref = context.getApplicationContext().getSharedPreferences("FavoritePT", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    try {
                        ArrayList<Station> fav = (ArrayList<Station>) ObjectSerializer.deserialize(pref.getString("favorite", null));
                    if (fav != null) {
                        for (int i = 0; i < fav.size(); i++){
                            if (fav.get(i).getId().equals(mData.get(position).getId())) {

                                fav.remove(i);
                            }
                        }
                    }
                        Log.e(TAG, "onClick: "+fav);
                        editor.putString("favorite", ObjectSerializer.serialize(fav));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    editor.commit();
                }
            }
        });
        holder.layoutRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), StationDetailActivity.class);
                intent.putExtra("stations", mData.get(position));
                try {
                    context.startActivity(intent);
                }catch (Exception e){
                    Log.d("error:" , e.getMessage());
                }
            }
        });
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
        TextView stationNameTV;
        TextView stationDistanceTV;
        TextView departureName1TV;
        TextView departureDirection1TV;
        TextView departureTime1TV;
        TextView departureName2TV;
        TextView departureDirection2TV;
        TextView departureTime2TV;
        ImageView departureType1IV;
        ImageView departureType2IV;
        Drawable bus;
        Drawable tram;
        ImageView favorite;
        Drawable checkedFavorite;
        Drawable uncheckedFavorite;
        ConstraintLayout layoutRow;

        ViewHolder(View itemView) {
            super(itemView);
            stationNameTV = itemView.findViewById(R.id.stationName);
            stationDistanceTV = itemView.findViewById(R.id.stationDistance);

            departureName1TV = itemView.findViewById(R.id.departureName1);
            departureDirection1TV = itemView.findViewById(R.id.departureDirection1);
            departureTime1TV = itemView.findViewById(R.id.departureTime1);
            departureType1IV = itemView.findViewById(R.id.departureType1);

            departureName2TV = itemView.findViewById(R.id.departureName2);
            departureDirection2TV = itemView.findViewById(R.id.departureDirection2);
            departureTime2TV = itemView.findViewById(R.id.departureTime2);
            departureType2IV = itemView.findViewById(R.id.departureType2);

            bus = itemView.getResources().getDrawable(R.drawable.ic_bus);
            tram = itemView.getResources().getDrawable(R.drawable.ic_tram);
            checkedFavorite = itemView.getResources().getDrawable(R.drawable.ic_marked_star);
            uncheckedFavorite = itemView.getResources().getDrawable(R.drawable.ic_unmarked_star);

            favorite = itemView.findViewById(R.id.favorite);

            layoutRow = itemView.findViewById(R.id.layoutRow);
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