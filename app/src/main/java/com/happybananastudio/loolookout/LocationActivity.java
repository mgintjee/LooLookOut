package com.happybananastudio.loolookout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by mgint on 3/11/2018.
 */

public class LocationActivity extends AppCompatActivity
        implements OnMapReadyCallback {
    private Context thisContext = this;
    private int MAX_DISTANCE = 25;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private Marker marker = null;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private int DEFAULT_ZOOM = 19;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private Location mLastKnownLocation;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private LatLng initialLatLng;
    private LatLng newLatLng;
    private String intentInfo;

    private final int REPORT_ACTIVITY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.location_map);
        mapFragment.getMapAsync(this);
        handleIntent();
        setImageButtonListeners();
    }
    private void handleIntent(){
        Bundle b = getIntent().getExtras();
        if( b != null )
        {
            double lat = b.getDouble("lat");
            double lng = b.getDouble("lng");
            initialLatLng = new LatLng(lat, lng);
        }
    }
    private void setImageButtonListeners(){
        ImageButtonCancel();
        ImageButtonContinue();
    }
    private void ImageButtonCancel(){
        ImageButton iBCancel = (ImageButton)findViewById(R.id.Location_iB_Cancel);
        iBCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }
    private void ImageButtonContinue(){
        ImageButton iBContinue = (ImageButton)findViewById(R.id.Location_iB_Continue);
        iBContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float dist = distanceBetween2LatLngs(initialLatLng.latitude, initialLatLng.longitude, newLatLng.latitude, newLatLng.longitude);
                if( newLatLng != null && MAX_DISTANCE > dist ) {
                    toastThisShort("Submitting Restroom");
                    Intent intent = new Intent(thisContext, ReportActivity.class);
                    intent.putExtra("lat", newLatLng.latitude);
                    intent.putExtra("lng", newLatLng.longitude);
                    startActivityForResult(intent, REPORT_ACTIVITY);
                }
                else{
                    toastThisLong("The distance ~" + String.valueOf(dist) + " meters away. \nMust be <" + String.valueOf(MAX_DISTANCE) + " meters away.");
                }
            }
        });
    }

    public Bitmap resizeMapIcons(String iconName){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, 150, 150, false);
    }
    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if( mLastKnownLocation != null ){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                            else{
                                toastThisShort("Error loading last known location");
                            }
                        }
                        else {
                            toastThisShort( "Current location is null. Using defaults.");
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    // Misc Helper Methods
    private void toastThisShort(String message){
        Toast.makeText(thisContext,
                message,
                Toast.LENGTH_SHORT).show();
    }
    private void toastThisLong(String message){
        Toast.makeText(thisContext,
                message,
                Toast.LENGTH_LONG).show();
    }
    private float distanceBetween2LatLngs(double centerLat, double centerLng, double pointLat, double pointLng){
        float[] results = new float[1];
        Location.distanceBetween(centerLat, centerLng, pointLat, pointLng, results);
        float distanceInMeters = results[0];
        return distanceInMeters;
    }

    private void setMarkerDragListener(){

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {}

            @Override
            public void onMarkerDrag(Marker marker) {}

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.d("Stop", marker.getPosition().toString());
                newLatLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            }
        });
    }
    private void handleActivityStart(){

        if( marker == null ){
            newLatLng = initialLatLng;
            MarkerOptions newMarker = new MarkerOptions()
                    .position(initialLatLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("pin")))
                    .draggable(true);

            marker = mMap.addMarker(newMarker);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        setMarkerDragListener();
        handleActivityStart();

    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = new Intent();
        setResult(resultCode, intent);
        if( resultCode == RESULT_OK){
            intent.putExtra("features", data.getStringExtra("features"));
        }
        finish();
        overridePendingTransition(0, 0);
    }
}
