package com.happybananastudio.loolookout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReportActivity extends Activity {
    private int gender = 0, size = 0, clean = 0, traffic = 0, access = 0, time = 0;
    private final ArrayList<String> possibleClean = new ArrayList<>(
            Arrays.asList("N/A", "At Least Very Dirty", "At Least Dirty", "At Least Neutral", "At Least Clean", "At Least Very Clean" ));
    private final ArrayList<String> possibleClosing = new ArrayList<>(
            Arrays.asList("N/A",
                    "12:00 am","1:00 am","2:00 am","3:00 am","4:00 am","5:00 am",
                    "6:00 am","7:00 am","8:00 am","9:00 am","10:00 am","11:00 am",
                    "12:00 pm","1:00 pm","2:00 pm","3:00 pm","4:00 pm","5:00 pm",
                    "6:00 pm","7:00 pm","8:00 pm","9:00 pm","10:00 pm","11:00 pm"));
    private ArrayList<Integer> amenities = new ArrayList<>();
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
        if( b != null )
        {
            double lat = b.getDouble("lat");
            double lng = b.getDouble("lng");
            String zipCode = getZipCode(lat, lng);
            latLng = new LatLng(lat, lng);
            String tVContent = "Zip Code: " + zipCode + "\nLat/Lng: " + roundDouble(latLng.latitude) + "/" + roundDouble(latLng.longitude);
            TextView tVLocation = (TextView) findViewById(R.id.RP_TV_Location);
            tVLocation.setText(tVContent);
        }
    }
    private void handleWidgets(){
        SetButtonListeners();
        handleCleanSeekBar();
        handleTimeSeekBar();
        handleCheckBoxes();
        InitializeGenderRGTL();
        InitializeSizeRGTL();
        InitializeTrafficRGTL();
        InitializeAccessRGTL();
    }

    private void InitializeGenderRGTL() {
        RadioGroupTableLayout RGTL = (RadioGroupTableLayout) findViewById(R.id.RP_RGTL_Gender);
        RGTL.setCheckedRadioButtonId(R.id.RP_RB_Inclusive);
    }
    private void InitializeSizeRGTL() {
        RadioGroupTableLayout RGTL = (RadioGroupTableLayout) findViewById(R.id.RP_RGTL_Size);
        RGTL.setCheckedRadioButtonId(R.id.RP_RB_SizeNA);
    }
    private void InitializeTrafficRGTL() {
        RadioGroupTableLayout RGTL = (RadioGroupTableLayout) findViewById(R.id.RP_RGTL_Traffic);
        RGTL.setCheckedRadioButtonId(R.id.RP_RB_TrafficNA);
    }
    private void InitializeAccessRGTL() {
        RadioGroupTableLayout RGTL = (RadioGroupTableLayout) findViewById(R.id.RP_RGTL_Access);
        RGTL.setCheckedRadioButtonId(R.id.RP_RB_AccessNA);
    }

    private void SetButtonListeners(){
        SetButtonCancelListener();
        SetButtonSendListener();
    }
    private void SetButtonCancelListener(){
        Button cancelReport = (Button) findViewById(R.id.RP_B_Cancel);
        cancelReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
    private void SetButtonSendListener(){
        Button B = (Button) findViewById(R.id.RP_B_Send);
        B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroupTableLayout RGTL_Gender = (RadioGroupTableLayout) findViewById(R.id.RP_RGTL_Gender);
                RadioGroupTableLayout RGTL_Size = (RadioGroupTableLayout) findViewById(R.id.RP_RGTL_Size);
                SeekBar SB_Clean = (SeekBar) findViewById(R.id.RP_SB_Clean);
                RadioGroupTableLayout RGTL_Traffic = (RadioGroupTableLayout) findViewById(R.id.RP_RGTL_Traffic);
                RadioGroupTableLayout RGTL_Access = (RadioGroupTableLayout) findViewById(R.id.RP_RGTL_Access);
                SeekBar SB_Time = (SeekBar) findViewById(R.id.RP_SB_Time);

                int ValueGender = GetRadioButtonValue(RGTL_Gender);
                int ValueSize = GetRadioButtonValue(RGTL_Size);
                int ValueClean = SB_Clean.getProgress();
                int ValueTraffic = GetRadioButtonValue(RGTL_Traffic);
                int ValueAccess = GetRadioButtonValue(RGTL_Access);
                int ValueTime = SB_Time.getProgress();

                Intent intent = new Intent();
                StringBuilder Features = new StringBuilder("");
                Features.append(roundDouble(latLng.latitude)).append(",").append(roundDouble(latLng.longitude)).append(":");
                Features.append(String.valueOf(ValueGender)).append(":");
                Features.append(String.valueOf(ValueSize)).append(":");
                Features.append(String.valueOf(ValueClean)).append(":");
                Features.append(String.valueOf(ValueTraffic)).append(":");
                Features.append(String.valueOf(ValueAccess)).append(":");
                Features.append(String.valueOf(ValueTime)).append(":");

                if (amenities.size() == 0) {
                    amenities.add(0);
                }

                for (int i = 0; i < amenities.size() - 1; ++i) {
                    Features.append(amenities.get(i)).append(",");
                }
                Features.append(amenities.get(amenities.size() - 1)).append(":");
                Features.append(getCurrentDate());
                intent.putExtra("features", Features.toString());

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void handleCleanSeekBar(){
        final SeekBar SB = (SeekBar) findViewById(R.id.RP_SB_Clean);
        SB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView tVClean = (TextView) findViewById(R.id.RP_TV_Clean);
                tVClean.setText(possibleClean.get(progress));
                clean = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    private void handleTimeSeekBar(){
        final SeekBar SB = (SeekBar) findViewById(R.id.RP_SB_Time);
        SB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView tVTime = (TextView) findViewById(R.id.RP_TV_Time);
                tVTime.setText(possibleClosing.get(progress));
                time = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    private void handleCheckBoxes(){
        CheckBox diaper = (CheckBox) findViewById(R.id.RP_CB_Diaper);
        CheckBox condom = (CheckBox) findViewById(R.id.RP_CB_Condom);
        CheckBox tampon = (CheckBox) findViewById(R.id.RP_CB_Tampon);

        diaper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked ){
                    amenities.add(1);
                }
                else{
                    amenities.remove(Integer.valueOf(1));
                }
            }
        });
        condom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked ){
                    amenities.add(2);
                }
                else{
                    amenities.remove(Integer.valueOf(2));
                }
            }
        });
        tampon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked ){
                    amenities.add(3);
                }
                else{
                    amenities.remove(Integer.valueOf(3));
                }
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
    }

    private String roundDouble( Double n ){
        DecimalFormat dF = new DecimalFormat("#.#######");
        return dF.format(n);
    }

    private String getCurrentDate(){
        Calendar c = Calendar.getInstance();

        int day = c.get(Calendar.DAY_OF_MONTH);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;

        String date = String.valueOf(month) + "/" + String.valueOf(day) + "/" + String.valueOf(year);

        return date;
    }

    private int GetRadioButtonValue(RadioGroupTableLayout RGTL) {
        int ID = RGTL.getCheckedRadioButtonId();
        int value = -1;
        switch (ID) {
            case R.id.RP_RB_SizeNA:
                value = 4;
                break;
            case R.id.RP_RB_Family:
            case R.id.RP_RB_Large:
            case R.id.RP_RB_TrafficNA:
            case R.id.RP_RB_AccessNA:
                value = 3;
                break;
            case R.id.RP_RB_Female:
            case R.id.RP_RB_Medium:
            case R.id.RP_RB_High:
            case R.id.RP_RB_Private:
                value = 2;
                break;
            case R.id.RP_RB_Male:
            case R.id.RP_RB_Small:
            case R.id.RP_RB_Some:
            case R.id.RP_RB_Customer:
                value = 1;
                break;
            case R.id.RP_RB_Inclusive:
            case R.id.RP_RB_Single:
            case R.id.RP_RB_Low:
            case R.id.RP_RB_Public:
                value = 0;
                break;
            default:
                break;
        }
        return value;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
