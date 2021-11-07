package com.example.foodwastemanagement;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Location address;
    private AutoCompleteTextView searchBoxEt;
    private SessionHelper sessionHelper;


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                        200000,
                        10, locationListener);
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sessionHelper=new SessionHelper(this);
        searchBoxEt= findViewById(R.id.searchLocationET);
        Button btn =findViewById(R.id.ReturnAdd);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String location [] = new String[2];
                location [0] = Double.toString(address.getLatitude());
                location [1] = Double.toString(address.getLongitude());
                Intent intent = new Intent();
                intent.putExtra("latitude",location[0]);
                intent.putExtra("longitude",location[1]);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });

        searchBoxEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
        searchBoxEt.setAdapter(new PlaceAutocompleteAdapter(MapsActivity.this, android.R.layout.simple_list_item_1));
    }


    private void performSearch() {

        String searchString = searchBoxEt.getText().toString();

        Geocoder geocoder = new Geocoder(this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e("geoLocate", ": IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address location = list.get(0);

            Log.d("geoLocate", "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            mMap.clear();
            location = list.get(0);
            LatLng userLoc = new LatLng(location.getLatitude(), location.getLongitude());

            mMap.addMarker(new MarkerOptions().position(userLoc).title("Your Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 15));

            sessionHelper.setLastKnownLAT(String.valueOf(location.getLatitude()));
            sessionHelper.setLastKnownLON(String.valueOf(location.getLongitude()));

        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                Toast.makeText(MapsActivity.this,location.toString(),Toast.LENGTH_SHORT).show();
//                Log.i("location LAT",Double.toString(lat));
                address= location;
                LatLng userLoc = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(userLoc).title("Your Location"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLoc,15));


                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> listAddress = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                    if(listAddress !=  null && listAddress.size()>0 ){
                        Log.i("PlaceInfo",listAddress.get(0).toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if(Build.VERSION.SDK_INT < 23){
            locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                    20000,
                    10, locationListener);

        } else{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else{
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000000,0,locationListener);
                mMap.setMyLocationEnabled(true);
                Location lastknownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                LatLng userLoc;
                if(lastknownLocation!=null) {
                    if(sessionHelper.getLastKnownLAT().isEmpty() || sessionHelper.getLastKnownLON().isEmpty())
                         userLoc = new LatLng(lastknownLocation.getLatitude(), lastknownLocation.getLongitude());
                    else
                        userLoc = new LatLng(Double.parseDouble(sessionHelper.getLastKnownLAT()), Double.parseDouble(sessionHelper.getLastKnownLON()));


                    mMap.clear();
                    address = lastknownLocation;
                    mMap.addMarker(new MarkerOptions().position(userLoc).title("Your Location"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 15));
                }else
                    userLoc =new LatLng(23.8103,90.4125);
                    mMap.clear();
                    address = lastknownLocation;
                    mMap.addMarker(new MarkerOptions().position(userLoc).title("Your Location"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLoc,8));

            }
        }


//        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng)
            {
                mMap.clear();
                sessionHelper.setLastKnownLAT(String.valueOf(latLng.latitude));
                sessionHelper.setLastKnownLON(String.valueOf(latLng.longitude));
                mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
            }
        });

    }

    public void onReturn(View view){

         double location1 = address.getLatitude();
         double location2 = address.getLongitude();

         sessionHelper.setLastKnownLAT(String.valueOf(address.getLatitude()));
         sessionHelper.setLastKnownLON(String.valueOf(address.getLongitude()));
      Log.i("Location Latlong","location LAT : "+ location1 +"location Long : " + location2);

    }
}
