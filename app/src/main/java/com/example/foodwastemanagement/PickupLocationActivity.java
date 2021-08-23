package com.example.foodwastemanagement;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PickupLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double latitude;
    double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        latitude = getIntent().getDoubleExtra("latitude",latitude);
        longitude = getIntent().getDoubleExtra("longitude",longitude);
        Log.i("lat from pickup",""+latitude);
        String address = getAddress(this,latitude,longitude);
        TextView addressField = (TextView) findViewById(R.id.address);
        addressField.setText(address);
        Log.i("lat",Double.toString(latitude));
        Log.i("long",Double.toString(longitude));

        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Pickup Location Market"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));
    }



    public String getAddress(Context ctx,double lat, double lng){
        String fullAdd = null;

        Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat,lng,1);
            if(addresses.size()>0){
                Address address = addresses.get(0);
                fullAdd = address.getAddressLine(0);


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fullAdd;

    }
    public  void onReturn(View view){
        //todo
//        Intent intent = new Intent(getApplicationContext(),NgoActivity.class);
//        startActivity(intent);
         finish();
    }
}
