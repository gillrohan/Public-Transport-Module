package com.example.publictransporttuc.PublicTransport.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.publictransporttuc.NetworkModels.Station;
import com.example.publictransporttuc.PublicTransport.PTinterface;
import com.example.publictransporttuc.PublicTransport.adapter.MyRecyclerViewAdapterPTMainAcitvity;
import com.example.publictransporttuc.PublicTransport.presenter.PTPresenter;
import com.example.publictransporttuc.R;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class PublicTransportMainActivity extends AppCompatActivity implements PTinterface.View, MyRecyclerViewAdapterPTMainAcitvity.ItemClickListener, SwipeRefreshLayout.OnRefreshListener {


    PTPresenter ptPresenter;
    RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_transport_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                PublicTransportMainActivity.super.onBackPressed();
            }
        });
        myToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        initDialog();

        // SwipeRefreshLayout
        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        /*mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);


            }
        });*/

        init();
        initViews();

        runPtPresenter();
    }

    private void initDialog(){
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ptmenu, menu);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {
                runPtPresenter();
            }
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.favorites:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                Intent intent = new Intent(PublicTransportMainActivity.this, FavoritesActivity.class);
                try {
                    startActivity(intent);
                }catch (Exception e){
                    Log.d("error:" , e.getMessage());
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
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
        Log.d("stationsList",stations.get(0).getName());
        recyclerView.setLayoutManager(new LinearLayoutManager(PublicTransportMainActivity.this));
        MyRecyclerViewAdapterPTMainAcitvity adapter = new MyRecyclerViewAdapterPTMainAcitvity(PublicTransportMainActivity.this, stations);
        adapter.setClickListener(PublicTransportMainActivity.this);
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

    @Override
    public void onRefresh() {
        runPtPresenter();
        // Stopping swipe refresh
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
