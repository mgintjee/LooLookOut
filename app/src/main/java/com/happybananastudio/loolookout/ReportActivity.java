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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
            String tVContent = "Zip Code: " + zipCode + "\n" + latLng.toString();
            TextView tVLocation = (TextView) findViewById(R.id.Report_tV_Location);
            tVLocation.setText(tVContent);
        }
    }

    private void handleWidgets(){
        handleButtons();
        handleGenderRadioGroup();
        handleSizeRadioGroup();
        handleCleanSeekBar();
        handleTrafficRadioGroup();
        handleAccessRadioGroup();
        handleTimeSeekBar();
        handleCheckBoxes();
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

                StringBuilder features = new StringBuilder("");
                features.append(roundDouble(latLng.latitude)).append(",").append(roundDouble(latLng.longitude)).append(":");
                features.append(gender).append(":");
                features.append(size).append(":");
                features.append(clean).append(":");
                features.append(traffic).append(":");
                features.append(access).append(":");
                features.append(time).append(":");

                if( amenities.size() == 0 ){
                    amenities.add(0);
                }

                for( int i = 0; i < amenities.size() - 1; ++i ){
                    features.append(amenities.get(i)).append(",");
                }
                features.append(amenities.get(amenities.size() - 1));
                String featuresString = features.toString();
                intent.putExtra("features", featuresString);
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }
    private void handleGenderRadioGroup(){
        final RadioGroup genderGroup = (RadioGroup) findViewById(R.id.Report_rG_Gender);
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedButton = (RadioButton) findViewById(checkedId);
                gender = genderGroup.indexOfChild(checkedButton);
            }
        });
    }
    private void handleSizeRadioGroup(){
        final RadioGroup sizeGroup = (RadioGroup) findViewById(R.id.Report_rG_Size);
        sizeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedButton = (RadioButton) findViewById(checkedId);
                size = sizeGroup.indexOfChild(checkedButton);
            }
        });
    }
    private void handleCleanSeekBar(){
        final SeekBar cleanSeekBar = (SeekBar) findViewById(R.id.Report_sB_Clean);
        cleanSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView tVClean = (TextView) findViewById(R.id.Report_tV_Clean);
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
        final SeekBar cleanSeekBar = (SeekBar) findViewById(R.id.Report_sB_Time);
        cleanSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView tVTime = (TextView) findViewById(R.id.Report_tV_Time);
                tVTime.setText(possibleClosing.get(progress));
                time = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    private void handleTrafficRadioGroup(){
        final RadioGroup trafficGroup = (RadioGroup) findViewById(R.id.Report_rG_Traffic);
        trafficGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedButton = (RadioButton) findViewById(checkedId);
                traffic = trafficGroup.indexOfChild(checkedButton);
            }
        });
    }
    private void handleAccessRadioGroup(){
        final RadioGroup accessGroup = (RadioGroup) findViewById(R.id.Report_rG_Access);
        accessGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedButton = (RadioButton) findViewById(checkedId);
                access = accessGroup.indexOfChild(checkedButton);
            }
        });
    }
    private void handleCheckBoxes(){
        CheckBox diaper = (CheckBox) findViewById(R.id.Report_cB_Diaper);
        CheckBox condom = (CheckBox) findViewById(R.id.Report_cB_Condom);
        CheckBox tampon = (CheckBox) findViewById(R.id.Report_cB_Tampon);

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
        DecimalFormat dF = new DecimalFormat("#.########");
        return dF.format(n);
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
