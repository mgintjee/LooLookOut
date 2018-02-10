package com.happybananastudio.loolookout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
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

import java.util.ArrayList;
import java.util.Arrays;

public class mainActivity extends AppCompatActivity
        implements OnMapReadyCallback {
    private final ArrayList<String> possibleGender = new ArrayList<>(
            Arrays.asList("Inclusive", "Male", "Female", "Family" ));
    private final ArrayList<String> possibleSize = new ArrayList<>(
            Arrays.asList("N/A", "Single", "Small", "Medium", "Large" ));
    private final ArrayList<String> possibleCleanliness = new ArrayList<>(
            Arrays.asList("N/A", "Clean", "Dirty" ));
    private final ArrayList<String> possibleTraffic = new ArrayList<>(
            Arrays.asList("N/A", "Low",  "Some", "High"  ));
    private final ArrayList<String> possibleAccess = new ArrayList<>(
            Arrays.asList("N/A", "Public", "Private", "Customers"  ));
    private final ArrayList<String> possibleAmenity = new ArrayList<>(
            Arrays.asList("N/A", "Diaper Changing Station", "Condom Dispenser", "Female Hygiene Dispenser" ));
    private final ArrayList<String> possibleClosing = new ArrayList<>(
            Arrays.asList("N/A",
                            "12:00 am","1:00 am","2:00 am","3:00 am","4:00 am","5:00 am",
                            "6:00 am","7:00 am","8:00 am","9:00 am","10:00 am","11:00 am",
                            "12:00 pm","1:00 pm","2:00 pm","3:00 pm","4:00 pm","5:00 pm",
                            "6:00 pm","7:00 pm","8:00 pm","9:00 pm","10:00 pm","11:00 pm"                         ));


    Context thisContext = this;

    private static final String TAG = mainActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private ImageButton iBSettings, iBFilter, iBReport, iBTemp, iBRefresh;
    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private int DEFAULT_ZOOM = 19;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private final int  FAMILY = 3;
    private final int FEMALE = 2;
    private final int MALE = 1;
    private final int INCLUSIVE = 0;

    private final int FILTERS_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeImageButtons();
        setImageButtonListeners();
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
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }


    // My Code
    private void addMarker( String Info ){
        int genderIndex, sizeIndex, cleanIndex, trafficIndex, accessIndex, closingIndex, amenityCount;
        String gender, size, clean, traffic, access, closing, amenitiesIndices;
        StringBuilder amenities = new StringBuilder("");
        LatLng latLng;
        double lat, lng;
        float hue;
        String[] infoParts = Info.split(":");
        for( int i = 0; i < infoParts.length; ++i ){
            Log.d("debug", Integer.toString(i) + " " + infoParts[i]);
        }
        lat = Double.parseDouble(infoParts[0].split(",")[0]);
        lng = Double.parseDouble(infoParts[0].split(",")[1]);

        // Handling the data
        latLng = new LatLng(lat, lng);

        genderIndex = Integer.valueOf(infoParts[1]);
        sizeIndex = Integer.valueOf(infoParts[2]);
        cleanIndex = Integer.valueOf(infoParts[3]);
        trafficIndex = Integer.valueOf(infoParts[4]);
        accessIndex = Integer.valueOf(infoParts[5]);
        closingIndex = Integer.valueOf(infoParts[6]);
        amenitiesIndices = infoParts[7];

        gender = possibleGender.get(genderIndex);
        size = possibleSize.get(sizeIndex);
        clean = possibleCleanliness.get(cleanIndex);
        traffic = possibleTraffic.get(trafficIndex);
        access = possibleAccess.get(accessIndex);
        closing = possibleClosing.get(closingIndex);
        String[] amenityParts = amenitiesIndices.split(",");

        for( int i = 0; i < amenityParts.length - 1; ++i){
            amenities.append(possibleAmenity.get(Integer.valueOf(amenityParts[i]))).append(",\n");
        }
        amenities.append(possibleAmenity.get(Integer.valueOf(amenityParts[amenityParts.length - 1])));

        amenityCount = amenityParts.length;

        if( amenityCount == 1 ){amenityCount = 2;}

        // Fill Info Window
        infoWindowData info = new infoWindowData();
        info.setLatLng(latLng);
        info.setGender(gender);
        info.setSize(size);
        info.setClean(clean);
        info.setTraffic(traffic);
        info.setAccess(access);
        info.setClosing(closing);
        info.setAmenities(amenities.toString());
        info.setAmenityCount(amenityCount);

        customInfoWindow customInfoWindow = new customInfoWindow(this);
        mMap.setInfoWindowAdapter(customInfoWindow);

        switch (genderIndex){
            case INCLUSIVE:
                hue = BitmapDescriptorFactory.HUE_BLUE;
                break;
            case MALE:
                hue = BitmapDescriptorFactory.HUE_AZURE;
                break;
            case FEMALE:
                hue = BitmapDescriptorFactory.HUE_ROSE;
                break;
            case FAMILY:
                hue = BitmapDescriptorFactory.HUE_YELLOW;
                break;
            default:
                hue = BitmapDescriptorFactory.HUE_GREEN;
        }

        MarkerOptions newMarker = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(hue));
        Marker m  = mMap.addMarker(newMarker);
        m.setTag(info);
    }
    private void initializeImageButtons(){
        iBSettings = (ImageButton) findViewById(R.id.iBSettings);
        iBFilter = (ImageButton) this.findViewById(R.id.iBFilter);
        iBReport = (ImageButton) findViewById(R.id.iBReport);
        iBTemp = (ImageButton) findViewById(R.id.iBTemp);
        iBRefresh = (ImageButton) findViewById(R.id.iBRefresh);
        if( iBFilter == null ) {
            Log.d("Debug", "filter is broked");
        }
    }
    private void setImageButtonListeners(){

        iBFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastThis("Editing Filters");

                Intent intent = new Intent(thisContext, filtersActivity.class);
                startActivityForResult(intent, FILTERS_ACTIVITY);
            }
        });
    }
    private void toastThis(String message){
        Toast.makeText(thisContext,
                message,
                Toast.LENGTH_SHORT).show();
    }


    // The Original Code From The Template
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            addMarker( Double.toString(mLastKnownLocation.getLatitude())+","+Double.toString(mLastKnownLocation.getLongitude()) + "1:1:1:1:1:1:1:0");

                        }
                        else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
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
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
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

    // The Original Code From The Template
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
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

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

    }
}
