package com.happybananastudio.loolookout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {
    private final ArrayList<String> possibleGender = new ArrayList<>(
            Arrays.asList("Inclusive", "Male", "Female", "Family" ));
    private final ArrayList<String> possibleSize = new ArrayList<>(
            Arrays.asList("N/A", "Single", "Small", "Medium", "Large" ));
    private final ArrayList<String> possibleCleanliness = new ArrayList<>(
            Arrays.asList("N/A", "0", "1", "2", "3", "4", "5" ));
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
                            "6:00 pm","7:00 pm","8:00 pm","9:00 pm","10:00 pm","11:00 pm"));
    private String filters = "";


    private Context thisContext = this;
    private String zipCode;
    private DatabaseReference mDatabase;
    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private ImageButton iBAbout, iBFilter, iBReport, iBTemp, iBRefresh;
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

    private final int FILTERS_ACTIVITY = 1;
    private final int REPORT_ACTIVITY = 2;
    private final int ABOUT_ACTIVITY = 3;

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

    // My Code
    private void addMarker( String Info ){
        int genderIndex, sizeIndex, cleanIndex, trafficIndex, accessIndex, closingIndex, amenityCount, voteCount;
        String gender, size, clean, traffic, access, closing, amenitiesIndices;
        StringBuilder amenities = new StringBuilder("");
        LatLng latLng;
        double lat, lng;
        String iconName;
        String[] infoParts = Info.split(":");

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
        amenitiesIndices = infoParts[8];
        voteCount = Integer.valueOf(infoParts[9]);
        Log.d("count",infoParts[9] + " -> " + String.valueOf(voteCount));

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

        //if( amenityCount == 1 ){amenityCount = 2;}

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
        info.setVoteCount(voteCount);

        customInfoWindow customInfoWindow = new customInfoWindow(this);
        mMap.setInfoWindowAdapter(customInfoWindow);

        switch (genderIndex){
            case 0: // Inclusive
                iconName = "pin_inclusive";
                break;
            case 1: // Male
                iconName = "pin_male";
                break;
            case 2: // Female
                iconName = "pin_female";
                break;
            case 3: // Family
                iconName = "pin_family";
                break;
            default:
                iconName = "pin";
        }

        MarkerOptions newMarker = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(iconName)));

        Marker m  = mMap.addMarker(newMarker);
        m.setTag(info);
    }
    private void initializeImageButtons(){
        iBAbout = (ImageButton) findViewById(R.id.iBAbout);
        iBFilter = (ImageButton) this.findViewById(R.id.iBFilter);
        iBReport = (ImageButton) findViewById(R.id.iBReport);
    }
    private void setImageButtonListeners(){

        iBFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastThis("Editing Filters");

                Intent intent = new Intent(thisContext, FiltersActivity.class);
                startActivityForResult(intent, FILTERS_ACTIVITY);
            }
        });
        iBReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastThis("Reporting Restroom");

                Intent intent = new Intent(thisContext, ReportActivity.class);
                intent.putExtra("lat", mLastKnownLocation.getLatitude());
                intent.putExtra("lng", mLastKnownLocation.getLongitude());
                startActivityForResult(intent, REPORT_ACTIVITY);

            }
        });
        iBAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastThis("About this App");

                Intent intent = new Intent(thisContext, AboutActivity.class);
                startActivityForResult(intent, ABOUT_ACTIVITY);

            }
        });
    }
    private void toastThis(String message){
        Toast.makeText(thisContext,
                message,
                Toast.LENGTH_SHORT).show();
    }

    public Bitmap resizeMapIcons(String iconName){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, 150, 150, false);
    }
    private String getZipCode(Double latitude, Double longitude)
    {
        String zipCode = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            zipCode = addresses.get(0).getPostalCode();
        }
        catch (Exception e){
        }
        return zipCode;
    }
    private void loadPostalRestrooms(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Double lat = mLastKnownLocation.getLatitude();
        Double lng = mLastKnownLocation.getLongitude();

        zipCode = getZipCode(lat, lng);
        getRestroomInfoFromDB(mDatabase.child(zipCode));
        /*
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Button bEditRestroom = (Button) findViewById(R.id.bEdit);
                Button bReportRestroom = (Button) findViewById(R.id.bReport);
                bEditRestroom.setVisibility(View.VISIBLE);
                bReportRestroom.setVisibility(View.VISIBLE);
                return false;
            }
        });
        */
    }

    private void getRestroomInfoFromDB(DatabaseReference dbRef){

        final boolean format = true;
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if( format ){
                    Iterable<DataSnapshot> dsChildData = dataSnapshot.getChildren();
                    int c = 0;
                    for( DataSnapshot dsChild : dsChildData){
                        String key = dsChild.getKey().replace("_",".");
                        String value = dsChild.getValue(String.class);
                        String markerInfo = key+":"+value;

                        addMarker(markerInfo);
                        c++;
                    }
                    Log.d("Rest Count ", Integer.toString(c));

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    // The Original Code From The Template
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
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            // DOES ALL THE HEAVY LIFTING HERE
                            loadPostalRestrooms();
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

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch(requestCode) {
                case ABOUT_ACTIVITY:
                    break;
                case FILTERS_ACTIVITY:
                case REPORT_ACTIVITY:
                    loadPostalRestrooms();
                    break;

            }
        }
    }
}
