package com.example.publictransporttuc.PublicTransport.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.publictransporttuc.NetworkModels.Station;
import com.example.publictransporttuc.PublicTransport.PTinterface;
import com.example.publictransporttuc.PublicTransport.adapter.MyRecyclerViewAdapterFavorites;
import com.example.publictransporttuc.PublicTransport.adapter.MyRecyclerViewAdapterPTMainAcitvity;
import com.example.publictransporttuc.PublicTransport.presenter.PTPresenter;
import com.example.publictransporttuc.R;
import com.example.publictransporttuc.Util.ObjectSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity implements PTinterface.View, MyRecyclerViewAdapterFavorites.ItemClickListener {

    PTPresenter ptPresenter;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        initDialog();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                FavoritesActivity.super.onBackPressed();
            }
        });
        SharedPreferences pref;
        ArrayList<Station> fav;
        pref = getSharedPreferences("FavoritePT", MODE_PRIVATE);
        try {
            fav = (ArrayList<Station>) ObjectSerializer.deserialize(pref.getString("favorite", null));
            if(fav != null){
                init();
                initViews();

                runPtPresenter();
            }
            else {
                Toast.makeText(this,"No Favorite Stations Yet...",Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }

    }
    private void initDialog(){
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data");
    }
    public void runPtPresenter() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
            }
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        ptPresenter.fetchNearbyStations("tuc",Double.toString(latitude),Double.toString(longitude));
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
        super.onBackPressed();
    }
    public void init()
    {
        ptPresenter = new PTPresenter(this);
    }
    public void initViews(){
        recyclerView = findViewById(R.id.ptRecyclerView);
    }

    @Override
    public void showSuccess(List<Station> stations) {
        onHideProgress();

        // set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyRecyclerViewAdapterFavorites adapter = new MyRecyclerViewAdapterFavorites(this, stations);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showErrorMessage(String message) {
        onHideProgress();
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showConnectionFailure(String message) {
        onHideProgress();
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShowProgress() {
        progressDialog.show();
    }

    @Override
    public void onHideProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void onItemClick(View view, int position) {


    }
}
