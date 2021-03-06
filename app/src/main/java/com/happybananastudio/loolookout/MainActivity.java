package com.happybananastudio.loolookout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {
    private final ArrayList<String> possibleGender = new ArrayList<>(
            Arrays.asList("Inclusive", "Male", "Female", "Family"));
    private final ArrayList<String> possibleSize = new ArrayList<>(
            Arrays.asList("N/A", "Single", "Small", "Medium", "Large"));
    private final ArrayList<String> possibleClean = new ArrayList<>(
            Arrays.asList("N/A", "At Least Very Dirty", "At Least Dirty", "At Least Neutral", "At Least Clean", "At Least Very Clean"));
    private final ArrayList<String> possibleTraffic = new ArrayList<>(
            Arrays.asList("N/A", "Low", "Some", "High"));
    private final ArrayList<String> possibleAccess = new ArrayList<>(
            Arrays.asList("N/A", "Public", "Private", "Customers"));
    private final ArrayList<String> possibleAmenity = new ArrayList<>(
            Arrays.asList("N/A", "Diaper Changing Station", "Condom Dispenser", "Female Hygiene Dispenser"));
    private final ArrayList<String> possibleClosing = new ArrayList<>(
            Arrays.asList("N/A",
                    "12:00 am", "1:00 am", "2:00 am", "3:00 am", "4:00 am", "5:00 am",
                    "6:00 am", "7:00 am", "8:00 am", "9:00 am", "10:00 am", "11:00 am",
                    "12:00 pm", "1:00 pm", "2:00 pm", "3:00 pm", "4:00 pm", "5:00 pm",
                    "6:00 pm", "7:00 pm", "8:00 pm", "9:00 pm", "10:00 pm", "11:00 pm"));
    private String filters = "";
    private Marker SelectedMarker = null;
    private Marker ReportMarker = null;
    private LatLng ReportLatLng = null;
    double InitialReportLat, InitialReportLng;
    private boolean newReport = false, newComplaint = false;


    private int MAX_DISTANCE = 25;
    private float ReportDistance = MAX_DISTANCE;
    private int FILTER_DISTANCE = 1000;
    private Context thisContext = this;
    private String zipCode;
    private DatabaseReference mDatabase;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private int DEFAULT_ZOOM = 19;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private Location mLastKnownLocation;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private final int FILTERS_ACTIVITY = 1;
    private final int REPORT_ACTIVITY = 2;
    private final int ABOUT_ACTIVITY = 3;
    private final int LOCATION_ACTIVITY = 4;
    private double lat;
    private double lng;
    private double margin = 0.003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initializeImageButtons();
        //setImageButtonListeners();
        SetWidgetListeners();
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
                .findFragmentById(R.id.M_MainMap);
        mapFragment.getMapAsync(this);
    }


    // Main App Methods
    private void loadPostalRestrooms() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        SelectedMarker = null;
        lat = mLastKnownLocation.getLatitude();
        lng = mLastKnownLocation.getLongitude();

        zipCode = getZipCode(lat, lng);
        clearRestroomsOnMap();

        if (mDatabase != null && !zipCode.equals("")) {
            getRestroomInfoFromDB();

            if (ReportMarker != null) {
                InitialReportLat = mLastKnownLocation.getLatitude();
                InitialReportLng = mLastKnownLocation.getLongitude();
                ReportLatLng = new LatLng(InitialReportLat, InitialReportLng);
                MarkerOptions newMarker = new MarkerOptions()
                        .position(ReportLatLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("pin")))
                        .draggable(true);
                CircleOptions newCircle = new CircleOptions()
                        .center(new LatLng(InitialReportLat, InitialReportLng))
                        .radius(MAX_DISTANCE)
                        .strokeColor(Color.BLACK)
                        .fillColor(Color.TRANSPARENT);
                ReportMarker = mMap.addMarker(newMarker);
                Circle circle = mMap.addCircle(newCircle);
            }

        } else {
            Log.d("Debug", "Error Loading Restrooms...Try Again Later");
        }
    }

    private void getRestroomInfoFromDB() {
        mDatabase.child(zipCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dsChildData = dataSnapshot.getChildren();
                int c = 0;
                for (DataSnapshot dsChild : dsChildData) {
                    try {
                        String key = String.valueOf(dsChild.getKey().replace("_", "."));
                        String value = String.valueOf(dsChild.getValue(String.class));
                        String markerInfo = key + ":" + value;
                        if (withinFilterDistance(markerInfo.split(":")[0]) && passesFilters(markerInfo)) {
                            addMarker(markerInfo);
                            c++;
                        }
                    } catch (DatabaseException e) {
                        Log.d("Database Exception", e.getMessage());
                        Log.d("Debug", "Error Handling DB, try again later.");
                    }
                }
                Log.d("Debug", "Found " + Integer.toString(c) + " Restroom(s) for ZipCode: " + zipCode + " within " + String.valueOf(FILTER_DISTANCE) + " meters.");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void clearRestroomsOnMap() {
        mMap.clear();
    }

    // Google Maps Methods
    private void getDeviceLocation(final boolean loadRestrooms) {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                // DOES ALL THE HEAVY LIFTING HERE
                                if (loadRestrooms) {
                                    loadPostalRestrooms();
                                }
                            } else {
                                Log.d("Debug", "Error loading last known location");
                            }
                        } else {
                            Log.d("Debug", "Current location is null. Using defaults.");
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
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
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    // Widget Methods
    private void SetWidgetListeners() {
        SetButtonListeners();
    }

    private void SetButtonListeners() {
        SetButtonListenerFilter();
        SetButtonListenerUpdate();
        SetButtonListenerCancel();
        SetButtonListenerRefresh();
        SetButtonListenerNew();
        SetButtonListenerNewContinue();
        SetButtonListenerAbout();
    }

    private void SetButtonListenerFilter() {
        Button B = findViewById(R.id.B_FilterSettings);
        B.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v) {
                                     Log.d("Activity", "Filters");
                                     startFiltersActivity();
                                 }
                             }
        );
    }

    private void SetButtonListenerUpdate() {
        Button B_Update = findViewById(R.id.B_UpdateRestroom);
        B_Update.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.d("Debug", "Update");
                                            StartUpdateActivity();
                                        }
                                    }
        );
    }

    private void SetButtonListenerCancel() {
        final Button B_New = findViewById(R.id.B_NewRestroom);
        final Button B_Update = findViewById(R.id.B_UpdateRestroom);
        final Button B_Filter = findViewById(R.id.B_FilterSettings);
        final Button B_About = findViewById(R.id.B_About);

        final Button B_Cancel = findViewById(R.id.B_NewRestroomCancel);
        final Button B_Continue = findViewById(R.id.B_NewRestroomContinue);
        final Button Spacer_0 = findViewById(R.id.Spacer_0);
        final Button Spacer_1 = findViewById(R.id.Spacer_1);

        B_Cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.d("Debug", "Cancel");
                                            B_New.setVisibility(View.VISIBLE);
                                            B_Update.setVisibility(View.VISIBLE);
                                            B_Filter.setVisibility(View.VISIBLE);
                                            B_About.setVisibility(View.VISIBLE);

                                            B_Cancel.setVisibility(View.GONE);
                                            B_Continue.setVisibility(View.GONE);
                                            Spacer_0.setVisibility(View.GONE);
                                            Spacer_1.setVisibility(View.GONE);

                                            if (ReportMarker != null) {
                                                ReportMarker.remove();
                                                ReportMarker = null;
                                                loadPostalRestrooms();
                                            }
                                        }
                                    }
        );
    }

    private void SetButtonListenerRefresh() {
        Button B = findViewById(R.id.B_RefreshMap);
        B.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v) {
                                     Log.d("Debug", "Refresh");
                                     loadPostalRestrooms();
                                 }
                             }
        );
    }

    private void SetButtonListenerNew() {
        final Button B_New = findViewById(R.id.B_NewRestroom);
        final Button B_Update = findViewById(R.id.B_UpdateRestroom);
        final Button B_Filter = findViewById(R.id.B_FilterSettings);
        final Button B_About = findViewById(R.id.B_About);

        final Button B_Cancel = findViewById(R.id.B_NewRestroomCancel);
        final Button B_Continue = findViewById(R.id.B_NewRestroomContinue);
        final Button Spacer_0 = findViewById(R.id.Spacer_0);
        final Button Spacer_1 = findViewById(R.id.Spacer_1);

        B_New.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         ReportLocation();
                                         toastThis("Press and Hold to Drag The Restroom Somewhere Within The Circle");
                                         if (SelectedMarker != null) {
                                             SelectedMarker = null;
                                         }

                                         Log.d("Debug", "New");
                                         B_New.setVisibility(View.GONE);
                                         B_Update.setVisibility(View.GONE);
                                         B_Filter.setVisibility(View.GONE);
                                         B_About.setVisibility(View.GONE);

                                         B_Cancel.setVisibility(View.VISIBLE);
                                         B_Continue.setVisibility(View.VISIBLE);
                                         Spacer_0.setVisibility(View.INVISIBLE);
                                         Spacer_1.setVisibility(View.INVISIBLE);

                                     }
                                 }
        );
    }

    private void toastThis(String message) {
        Toast.makeText(thisContext,
                message,
                Toast.LENGTH_LONG).show();
    }

    private void SetButtonListenerNewContinue() {
        Button B = findViewById(R.id.B_NewRestroomContinue);
        B.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v) {
                                     Log.d("Debug", "New");
                                     StartReportActivity();
                                 }
                             }
        );
    }

    private void SetButtonListenerAbout() {
        Button B = findViewById(R.id.B_About);
        B.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v) {
                                     Log.d("Debug", "About");
                                     startAboutActivity();
                                 }
                             }
        );
    }

    private void setMapMarkerListener() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                SelectedMarker = marker;

                if (marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                } else {
                    marker.showInfoWindow();
                }
                return false;
            }
        });
    }

    public Bitmap resizeMapIcons(String iconName) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, 150, 150, false);
    }

    private void addMarker(String Info) {
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
        amenitiesIndices = infoParts[7];
        String lastDate = infoParts[8];
        voteCount = Integer.valueOf(infoParts[9]);

        gender = possibleGender.get(genderIndex);
        size = possibleSize.get(sizeIndex);
        clean = possibleClean.get(cleanIndex);
        traffic = possibleTraffic.get(trafficIndex);
        access = possibleAccess.get(accessIndex);
        closing = possibleClosing.get(closingIndex);
        String[] amenityParts = amenitiesIndices.split(",");

        for (int i = 0; i < amenityParts.length - 1; ++i) {
            amenities.append(possibleAmenity.get(Integer.valueOf(amenityParts[i]))).append(",\n");
        }
        amenities.append(possibleAmenity.get(Integer.valueOf(amenityParts[amenityParts.length - 1])));

        amenityCount = amenityParts.length;

        if (amenityCount == 1) {
            amenityCount = 2;
        }

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
        info.setLastDate(lastDate);
        info.setVoteCount(voteCount);

        customInfoWindow customInfoWindow = new customInfoWindow(this);
        mMap.setInfoWindowAdapter(customInfoWindow);

        switch (genderIndex) {
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

        Marker m = mMap.addMarker(newMarker);
        m.setTag(info);
    }

    private void setMarkerDragListener() {
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.d("Previous LatLng", ReportLatLng.toString());
                ReportLatLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                ReportDistance = distanceBetween2LatLngs(ReportLatLng.latitude, ReportLatLng.longitude, InitialReportLat, InitialReportLng);
                Log.d("Distance", String.valueOf(ReportDistance));
                Log.d("Drag End LatLng", ReportLatLng.toString());
            }
        });
    }

    // Starting/Handling New Activity Methods
    private void ReportLocation() {
        loadPostalRestrooms();
        if (mLastKnownLocation != null && !zipCode.equals("")) {
            Log.d("Debug", "Creating New Restroom");
            AddReportMarker();
            loadPostalRestrooms();
        }
    }

    private void StartReportActivity() {
        //loadPostalRestrooms();
        if (ReportLatLng != null) {
            zipCode = getZipCode(ReportLatLng.latitude, ReportLatLng.longitude);
            if (!zipCode.equals("")) {
                if (ReportDistance < MAX_DISTANCE) {
                    Log.d("Debug", "Submitting New Restroom");
                    Log.d("Start Activity LatLng", ReportLatLng.toString());
                    Intent intent = new Intent(thisContext, ReportActivity.class);
                    intent.putExtra("lat", ReportLatLng.latitude);
                    intent.putExtra("lng", ReportLatLng.longitude);
                    startActivityForResult(intent, REPORT_ACTIVITY);
                } else {
                    dialogError("New Report Error", "Distance Away Is Over The Limit: " + String.valueOf(MAX_DISTANCE) + " m");
                }
            } else {
                dialogError("New Report Error", "Error Getting Current Location Or Zipcode. Try Again Later.");
            }
        } else {
            dialogError("New Report Error", "Error Getting Current Location Or Zipcode. Try Again Later.");
        }
    }

    private void AddReportMarker() {
        if (ReportMarker == null) {
            double NewLat = mLastKnownLocation.getLatitude();
            double NewLng = mLastKnownLocation.getLongitude();
            ReportLatLng = new LatLng(NewLat, NewLng);
            MarkerOptions newMarker = new MarkerOptions()
                    .position(ReportLatLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("pin")))
                    .draggable(true);

            ReportMarker = mMap.addMarker(newMarker);
        }
    }

    private void startAboutActivity() {
        Log.d("Debug", "About this App");
        Intent intent = new Intent(thisContext, AboutActivity.class);
        startActivityForResult(intent, ABOUT_ACTIVITY);
    }

    private void startFiltersActivity() {
        Log.d("Debug", "Editing Filters");
        Intent intent = new Intent(thisContext, FiltersActivity.class);
        intent.putExtra("filters", filters);
        startActivityForResult(intent, FILTERS_ACTIVITY);
    }

    private void StartUpdateActivity() {
        Log.d("Debug", "Filing Complaint");
        newComplaint = true;
        getDeviceLocation(false);
        if (mLastKnownLocation != null) {
            if (SelectedMarker != null) {
                infoWindowData info = (infoWindowData) SelectedMarker.getTag();
                if (info != null) {
                    double targetLat = info.getLatLng().latitude;
                    double targetLng = info.getLatLng().longitude;
                    double centerLat = mLastKnownLocation.getLatitude();
                    double centerLng = mLastKnownLocation.getLongitude();
                    String gender = String.valueOf(possibleGender.indexOf(info.getGender()));
                    float distance = distanceBetween2LatLngs(centerLat, centerLng, targetLat, targetLng);
                    final String targetKey = (targetLat + "," + targetLng + ":" + gender).replace(".", "_");

                    if (distance < MAX_DISTANCE) {
                        mDatabase.child(zipCode).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Iterable<DataSnapshot> dsChildData = dataSnapshot.getChildren();
                                for (DataSnapshot dsChild : dsChildData) {
                                    try {
                                        String key = String.valueOf(dsChild.getKey());
                                        String value = String.valueOf(dsChild.getValue(String.class));
                                        String[] valueFeatures = value.split(":");
                                        int reportCount = Integer.valueOf(valueFeatures[valueFeatures.length - 1]) - 1;
                                        if (key.equals(targetKey) && SelectedMarker != null && newComplaint) {
                                            dialogConfirmCancelNewComplaint(targetKey, reportCount, valueFeatures);
                                            break;
                                        }
                                    } catch (DatabaseException e) {
                                        Log.d("Database Exception", e.getMessage());
                                        Log.d("Debug", "Error Handling DB, try again later.");
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    } else {
                        dialogError("File Complaint Error", "You are over " + String.valueOf(distance) + " meters away from the restroom you wish to file a complaint on.\nYou need to be within " + String.valueOf(MAX_DISTANCE) + " meters of a restroom to file a complaint on it.");
                    }
                } else {
                    dialogError("Select Error", "Ran into issues loading the selected marker\'s info...");
                }
            } else {
                dialogError("No Selected Restroom", "There is no selected Restroom to file a complaint about.\nPlease select a Restroom to file a complaint on.");
            }
        }
    }

    private void handleRestroomReport(final String features) {
        String[] listOfReportFeatures = features.split(":");
        final String reportCoordinates = listOfReportFeatures[0];
        final String reportGender = listOfReportFeatures[1];
        DatabaseReference dbRef = mDatabase.child(zipCode);
        int coordinates = reportCoordinates.length() + 3;
        final String featureString = features.substring(coordinates);
        String[] centerLatLng = reportCoordinates.split(",");
        final double centerLat = Double.valueOf(centerLatLng[0]);
        final double centerLng = Double.valueOf(centerLatLng[1]);
        if (newReport) {
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> dsChildData = dataSnapshot.getChildren();
                    String oldFeatureString = "";
                    boolean exists = false;
                    String oldKey = "";
                    String reportCount = "1";
                    int nearRestrooms = 0;
                    float nearest = MAX_DISTANCE;
                    float current;

                    for (DataSnapshot dsChild : dsChildData) {
                        try {
                            oldFeatureString = dsChild.getValue(String.class);
                            String markerInfo = (String.valueOf(dsChild.getKey() + ":" + oldFeatureString)).replace("_", ".");
                            String[] listOfTargetFeatures = markerInfo.split(":");
                            String targetCoordinates = listOfTargetFeatures[0];
                            String[] targetLatLng = targetCoordinates.split(",");

                            String targetGender = listOfTargetFeatures[1];
                            double targetLat = Double.valueOf(targetLatLng[0]);
                            double targetLng = Double.valueOf(targetLatLng[1]);
                            current = distanceBetween2LatLngs(centerLat, centerLng, targetLat, targetLng);

                            if (nearest > current && reportGender.equals(targetGender)) {
                                nearest = current;
                                reportCount = String.valueOf(Integer.valueOf(listOfTargetFeatures[9]) + 1);
                                oldKey = String.valueOf(dsChild.getKey());
                                exists = true;
                                nearRestrooms++;
                            }
                        } catch (DatabaseException e) {
                            Log.d("Database Exception", e.getMessage());
                            Log.d("Debug", "Error Handling DB, try again later.");
                        }
                    }

                    if (exists) {
                        dialogConfirmCancelNewRestroom(oldKey, reportCoordinates + ":" + reportGender, featureString, oldFeatureString, Integer.valueOf(reportGender), nearRestrooms, reportCount);
                    } else {
                        handleAbsentRestroom(features);
                    }
                    newReport = false;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    private void handleAbsentRestroom(String features) {
        String[] listOfFeatures = features.split(":");
        String reportCoordinates = listOfFeatures[0];
        String reportGender = listOfFeatures[1];
        int coordinates = reportCoordinates.length();
        String featureString = features.substring(coordinates + 3);
        String key = reportCoordinates + ":" + reportGender;
        key = key.replace(".", "_");
        String value = featureString + ":1";
        handleNearbyZipCodesForSubmit(key, value);
    }

    private void handleNearbyZipCodesForSubmit(String key, String value) {

        String zipCodeC = zipCode;
        String zipCodeN = getZipCode(lat + margin, lng);
        String zipCodeE = getZipCode(lat, lng + margin);
        String zipCodeS = getZipCode(lat - margin, lng);
        String zipCodeW = getZipCode(lat, lng - margin);

        if (!zipCodeN.equals(zipCodeC)) {
            mDatabase.child(zipCodeN).child(key).setValue(value);
        }
        if (!zipCodeE.equals(zipCodeC)) {
            mDatabase.child(zipCodeE).child(key).setValue(value);
        }
        if (!zipCodeS.equals(zipCodeC)) {
            mDatabase.child(zipCodeS).child(key).setValue(value);
        }
        if (!zipCodeW.equals(zipCodeC)) {
            mDatabase.child(zipCodeW).child(key).setValue(value);
        }

        mDatabase.child(zipCode).child(key).setValue(value);
    }

    private void handleNearbyZipCodesForComplaint(String key, int reportCount, String[] values) {
        String zipCodeC = zipCode;
        String zipCodeN = getZipCode(lat + margin, lng);
        String zipCodeE = getZipCode(lat, lng + margin);
        String zipCodeS = getZipCode(lat - margin, lng);
        String zipCodeW = getZipCode(lat, lng - margin);

        if (!zipCodeN.equals(zipCodeC)) {
            handleConfirmNewComplain(zipCodeN, key, reportCount, values);
        }
        if (!zipCodeE.equals(zipCodeC)) {
            handleConfirmNewComplain(zipCodeE, key, reportCount, values);
        }
        if (!zipCodeS.equals(zipCodeC)) {
            handleConfirmNewComplain(zipCodeS, key, reportCount, values);
        }
        if (!zipCodeW.equals(zipCodeC)) {
            handleConfirmNewComplain(zipCodeW, key, reportCount, values);
        }

        handleConfirmNewComplain(zipCodeC, key, reportCount, values);
    }

    // Starting/Handling New Dialogs
    private void dialogConfirmCancelNewRestroom(final String oldKey, final String newKey, final String features, final String oldFeatureString, int gender, int restroomCount, final String reportCount) {
        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(thisContext, R.style.AlertDialogStyle);
        } else {
            builder = new AlertDialog.Builder(thisContext);
        }

        String message = "We found " + String.valueOf(restroomCount) + " restroom(s) of the same gender, " + possibleGender.get(gender) + ", within " + String.valueOf(MAX_DISTANCE) + " meters of your location\n";

        builder.setTitle("Is this an EXISTING or NEW restroom?")
                .setMessage(message)
                .setPositiveButton(R.string.new_one, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        handleAbsentRestroom(newKey + ":" + features);
                        Log.d("Debug", "Sending Report for New Restroom");
                    }
                })
                .setNegativeButton(R.string.exist_one, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String newOldFeatures = handleExistingFeatures(oldFeatureString, features);
                        handleNearbyZipCodesForSubmit(oldKey, newOldFeatures + ":" + reportCount);
                        Log.d("Debug", "Sending Report for Existing Restroom");
                    }
                })
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void dialogError(String title, String message) {
        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(thisContext, R.style.AlertDialogStyle);
        } else {
            builder = new AlertDialog.Builder(thisContext);
        }
        builder.setTitle(title)
                .setMessage(message)
                .setNeutralButton(R.string.minimize, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void dialogConfirmCancelNewComplaint(final String key, final int reportCount, final String[] values) {
        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(thisContext, R.style.AlertDialogStyle);
        } else {
            builder = new AlertDialog.Builder(thisContext);
        }

        String message = "Are you sure you cannot find this restroom within " + String.valueOf(MAX_DISTANCE) + " meters of your location?";

        builder.setTitle("Submitting a New Complaint")
                .setMessage(message)
                .setPositiveButton(R.string.cannot_find, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        handleNearbyZipCodesForComplaint(key, reportCount, values);
                    }
                })
                .setNegativeButton(R.string.look_more, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Debug", "Cancelling Complaint for Restroom");
                    }
                })
                .show();

    }

    private void handleConfirmNewComplain(final String zipCode, final String key, final int reportCount, final String[] values) {
        SelectedMarker = null;
        newComplaint = false;
        mDatabase.child(zipCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(key)) {
                    if (reportCount < 1) {
                        mDatabase.child(zipCode).child(key).removeValue();
                    } else {
                        values[values.length - 1] = String.valueOf(reportCount);
                        String value = joinListByDelimiter(values, ":");
                        mDatabase.child(zipCode).child(key).setValue(value);
                    }
                    getDeviceLocation(true);
                    Log.d("Debug", "Sending Complaint for Restroom");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // Filter Methods
    private boolean passesFilters(String markerInfo) {
        boolean pass = true;

        if (!filters.equals("")) {
            String[] filterParts = filters.split(":");
            String[] markerParts = markerInfo.split(":");

            pass = sameGender(filterParts[0], markerParts[1]) && sameSize(filterParts[1], markerParts[2]) && atLeastClean(filterParts[2], markerParts[3]) && sameTraffic(filterParts[3], markerParts[4]) && sameAccess(filterParts[4], markerParts[5]) && openNow(markerParts[6]) && hasAmenities(filterParts[5], markerParts[7]);
        }

        return pass;
    }

    private boolean withinFilterDistance(String stringLatLng) {
        String[] latLng = stringLatLng.split(",");
        double centerLat = mLastKnownLocation.getLatitude();
        double centerLng = mLastKnownLocation.getLongitude();
        double lat = Double.valueOf(latLng[0]);
        double lng = Double.valueOf(latLng[1]);

        float distance = distanceBetween2LatLngs(centerLat, centerLng, lat, lng);

        return distance < FILTER_DISTANCE;
    }

    private boolean sameGender(String filter, String target) {
        boolean pass = true;

        if (!filter.equals(target) && !filter.equals("4")) {
            Log.d("Gender", "fail");
            pass = false;
        }

        return pass;
    }

    private boolean sameSize(String filter, String target) {
        boolean pass = true;
        int min = Integer.valueOf(filter);
        int tar = Integer.valueOf(target);

        if (min != tar - 1 && min != 4) {
            Log.d("Size", "fail");
            pass = false;
        }

        return pass;
    }

    private boolean atLeastClean(String filter, String target) {
        boolean pass = true;
        int min = Integer.valueOf(filter);
        int tar = Integer.valueOf(target);

        if (min > tar && min != 5) {
            Log.d("Clean", "fail");
            pass = false;
        }

        return pass;
    }

    private boolean sameTraffic(String filter, String target) {
        boolean pass = true;

        int min = Integer.valueOf(filter);
        int tar = Integer.valueOf(target);

        if (min != tar - 1 && min != 3) {
            Log.d("Traffic", "fail");
            pass = false;
        }

        return pass;
    }

    private boolean sameAccess(String filter, String target) {
        boolean pass = true;
        int min = Integer.valueOf(filter);
        int tar = Integer.valueOf(target);

        if (min != tar - 1 && min != 3) {
            Log.d("Access", "fail");
            pass = false;
        }

        return pass;
    }

    private boolean hasAmenities(String desiredAmenities, String amenities) {
        boolean pass = true;

        if (!desiredAmenities.equals("0")) {
            String[] desParts = desiredAmenities.split(",");
            String[] tarParts = amenities.split(",");

            for (int i = 0; i < desParts.length; ++i) {
                if (!Arrays.asList(tarParts).contains(desParts[i])) {
                    pass = false;
                    break;
                }
            }
        }

        return pass;
    }

    private boolean openNow(String time) {
        boolean pass = true;
        if (!time.equals("0")) {
            String stringTime = possibleClosing.get(Integer.valueOf(time));
            String currentTime = getCurrentTime();

            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());

            try {
                Date filterDate = dateFormat.parse(stringTime);
                Date currentDate = dateFormat.parse(currentTime);

                if (currentDate.after(filterDate)) {
                    pass = false;
                }
            } catch (ParseException e) {
                Log.d("Parse Error", stringTime + " " + currentTime);
            }
        }

        return pass;
    }

    // Misc Helper Methods
    private String getCurrentTime() {
        String time;
        String period = "am";
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        if (hour > 12) {
            hour = hour - 12;
            period = "pm";
        }

        String h = String.valueOf(hour);
        String m = String.valueOf(minute);

        time = h + ":" + m + " " + period;
        return time;
    }

    private float distanceBetween2LatLngs(double centerLat, double centerLng, double pointLat, double pointLng) {
        float[] results = new float[1];
        Location.distanceBetween(centerLat, centerLng, pointLat, pointLng, results);
        float distanceInMeters = results[0];
        return distanceInMeters;
    }

    private String getZipCode(Double latitude, Double longitude) {
        String zipCode = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            zipCode = addresses.get(0).getPostalCode();
        } catch (Exception e) {
        }
        return zipCode;
    }

    private String joinListByDelimiter(String[] list, String delim) {
        StringBuilder s = new StringBuilder("");

        for (int i = 0; i < list.length - 1; ++i) {
            s.append(list[i]);
            s.append(delim);
        }
        s.append(list[list.length - 1]);

        return s.toString();
    }

    private String handleExistingFeatures(String oldFeaturesString, String newFeaturesString) {
        String[] newFeaturesList = newFeaturesString.split(":");
        String[] oldFeaturesList = oldFeaturesString.split(":");
        int len = newFeaturesList.length;
        StringBuilder newFeatures = new StringBuilder("");
        for (int i = 0; i < len - 1; ++i) {
            if (newFeaturesList[i].equals("0")) {
                newFeatures.append(oldFeaturesList[i]);
            } else {
                newFeatures.append(newFeaturesList[i]);
            }
            newFeatures.append(":");
        }

        if (newFeaturesList[len - 1].equals("0")) {
            newFeatures.append(oldFeaturesList[len - 1]);
        } else {
            newFeatures.append(newFeaturesList[len - 1]);
        }

        return newFeatures.toString();
    }

    // Override Methods
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
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
        setMapMarkerListener();

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation(true);
        setMarkerDragListener();
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
            switch (requestCode) {
                case ABOUT_ACTIVITY:
                    break;
                case FILTERS_ACTIVITY:
                    filters = data.getStringExtra("filters");
                    break;
                case LOCATION_ACTIVITY:
                case REPORT_ACTIVITY:
                    String features = data.getStringExtra("features");
                    newReport = true;
                    handleRestroomReport(features);

                    final Button B_New = findViewById(R.id.B_NewRestroom);
                    final Button B_Update = findViewById(R.id.B_UpdateRestroom);
                    final Button B_Filter = findViewById(R.id.B_FilterSettings);
                    final Button B_About = findViewById(R.id.B_About);

                    final Button B_Cancel = findViewById(R.id.B_NewRestroomCancel);
                    final Button B_Continue = findViewById(R.id.B_NewRestroomContinue);
                    final Button Spacer_0 = findViewById(R.id.Spacer_0);
                    final Button Spacer_1 = findViewById(R.id.Spacer_1);

                    B_New.setVisibility(View.VISIBLE);
                    B_Update.setVisibility(View.VISIBLE);
                    B_Filter.setVisibility(View.VISIBLE);
                    B_About.setVisibility(View.VISIBLE);

                    B_Cancel.setVisibility(View.GONE);
                    B_Continue.setVisibility(View.GONE);
                    Spacer_0.setVisibility(View.GONE);
                    Spacer_1.setVisibility(View.GONE);

                    if (ReportMarker != null) {
                        ReportMarker.remove();
                        ReportMarker = null;
                    }

                    break;

            }
            getDeviceLocation(true);
        }
        SelectedMarker = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDeviceLocation(true);
    }
}