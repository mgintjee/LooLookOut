package com.happybananastudio.loolookout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Locale;

public class ReportActivity extends Activity {
    Context thisContext = this;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFinishOnTouchOutside(false);
        getIntentInfo();
        handleWidgets();
    }
    private void getIntentInfo(){
        Bundle b = getIntent().getExtras();
        if( b != null ) {
            double lat = b.getDouble("lat");
            double lng = b.getDouble("lng");
            latLng = new LatLng(lat, lng);
            String zipCode = getZipCode(lat, lng);
            latLng = new LatLng(lat, lng);
            String tVContent = "Zip Code: " + zipCode + "\n" + latLng.toString();
            TextView tVLocation = (TextView) findViewById(R.id.Report_tV_Location);
            tVLocation.setText(tVContent);
        }
    }
    private void handleWidgets(){
        handleButtons();
    }
    private void handleButtons(){
        Button cancelReport = (Button) findViewById(R.id.Report_b_Cancel);
        Button sendReport = (Button) findViewById(R.id.Report_b_Send);

        cancelReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                setResult(RESULT_CANCELED, intent);
                toastThis("Cancelling Report");
                finish();
                overridePendingTransition(0, 0);
            }
        });
        sendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                setResult(RESULT_OK, intent);
                toastThis("Sending Report");
                // TODO
                /*
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("message");

                myRef.setValue("Hello, World!");
                Log.d("bbop",myRef.toString());
                */
                finish();
                overridePendingTransition(0, 0);
            }
        });
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
        /*
        String zipcode = null;
        try
        {
            LatLng latLng = new LatLng();
            latLng.setLat(BigDecimal.valueOf(latitude));
            latLng.setLng(BigDecimal.valueOf(longitude));
            final Geocoder geocoder = new Geocoder();
            GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setLocation(latLng).getGeocoderRequest();
            GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
            List<GeocoderResult> results = geocoderResponse.getResults();
            logger.debug("results :  "+results); //This will print geographical information
            List<GeocoderAddressComponent> geList= results.get(0).getAddressComponents();
            if(geList.get(geList.size()-1).getTypes().get(0).trim().equalsIgnoreCase("postal&#95;code"))
            {
                zipcode = geList.get(geList.size()-1).getLongName();
            }
            else if(geList.get(0).getTypes().get(0).trim().equalsIgnoreCase("postal&#95;code"))
            {
                zipcode = geList.get(0).getLongName();
            }
            logger.debug("zipcode :  " + zipcode);
        }catch (Exception e) {
            logger.debug(e.toString());
            logger.debug(e.getLocalizedMessage());
        }
        */
    }
    private void toastThis(String message){
        Toast.makeText(thisContext,
                message,
                Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
