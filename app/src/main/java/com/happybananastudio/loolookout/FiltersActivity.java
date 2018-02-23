package com.happybananastudio.loolookout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Arrays;

public class FiltersActivity extends Activity {
    private final ArrayList<String> possibleClean = new ArrayList<>(
            Arrays.asList("N/A", "At Least Very Dirty", "At Least Dirty", "At Least Neutral", "At Least Clean", "At Least Very Clean" ));
    private int gender = 0, size = 0, clean = 0, traffic = 0, access = 0;
    private ArrayList<Integer> amenities = new ArrayList<>();
    Context thisContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFinishOnTouchOutside(false);
        handleWidgets();
    }

    private void handleWidgets(){
        handleMinimizeButton();
        handleGenderRadioGroup();
        handleSizeRadioGroup();
        handleCleanSeekBar();
        handleTrafficRadioGroup();
        handleAccessRadioGroup();
        handleCheckBoxes();
        handleClearFiltersButton();
    }
    private void handleMinimizeButton(){
        Button minimize = (Button) findViewById(R.id.Filter_b_Minimize);
        minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                StringBuilder filters = new StringBuilder("");
                filters.append(gender).append(":");
                filters.append(size).append(":");
                filters.append(clean).append(":");
                filters.append(traffic).append(":");
                filters.append(access).append(":");

                if( amenities.size() == 0 ){
                    amenities.add(0);
                }

                for( int i = 0; i < amenities.size() - 1; ++i ){
                    filters.append(amenities.get(i)).append(",");
                }
                filters.append(amenities.get(amenities.size() - 1));
                intent.putExtra("filters", filters.toString());

                setResult(RESULT_OK, intent);
                toastThis("Applying Filters");
                finish();
                overridePendingTransition(0, 0);
            }

        });
    }
    private void handleClearFiltersButton(){
        Button clearFilters = (Button) findViewById(R.id.Filter_b_ClearFilters);
        clearFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup genderGroup = (RadioGroup) findViewById(R.id.Filter_rG_Gender);
                RadioGroup sizeGroup = (RadioGroup) findViewById(R.id.Filter_rG_Size);
                SeekBar cleanSeekBar = (SeekBar) findViewById(R.id.Filter_sB_Clean);
                RadioGroup trafficGroup = (RadioGroup) findViewById(R.id.Filter_rG_Traffic);
                RadioGroup accessGroup = (RadioGroup) findViewById(R.id.Filter_rG_Access);
                CheckBox diaper = (CheckBox) findViewById(R.id.Filter_cB_Diaper);
                CheckBox condom = (CheckBox) findViewById(R.id.Filter_cB_Condom);
                CheckBox tampon = (CheckBox) findViewById(R.id.Filter_cB_Tampon);

                genderGroup.check(R.id.Filter_rB_NA_gender);
                sizeGroup.check(R.id.Filter_rB_NA_size);
                cleanSeekBar.setProgress(0);
                trafficGroup.check(R.id.Filter_rB_NA_traffic);
                accessGroup.check(R.id.Filter_rB_NA_access);

                if(diaper.isChecked()){
                    diaper.toggle();
                }
                if(condom.isChecked()){
                    condom.toggle();
                }
                if(tampon.isChecked()){
                    tampon.toggle();
                }
            }
        });
    }
    private void handleGenderRadioGroup(){
        final RadioGroup genderGroup = (RadioGroup) findViewById(R.id.Filter_rG_Gender);
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedButton = (RadioButton) findViewById(checkedId);
                gender = genderGroup.indexOfChild(checkedButton);
            }
        });
    }
    private void handleSizeRadioGroup(){
        final RadioGroup sizeGroup = (RadioGroup) findViewById(R.id.Filter_rG_Size);
        sizeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedButton = (RadioButton) findViewById(checkedId);
                size = sizeGroup.indexOfChild(checkedButton);
            }
        });
    }
    private void handleCleanSeekBar(){
        final SeekBar cleanSeekBar = (SeekBar) findViewById(R.id.Filter_sB_Clean);
        cleanSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView tVClean = (TextView) findViewById(R.id.Filter_tV_Clean);
                tVClean.setText(possibleClean.get(progress));
                clean = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    private void handleTrafficRadioGroup(){
        final RadioGroup trafficGroup = (RadioGroup) findViewById(R.id.Filter_rG_Traffic);
        trafficGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedButton = (RadioButton) findViewById(checkedId);
                traffic = trafficGroup.indexOfChild(checkedButton);
            }
        });
    }
    private void handleAccessRadioGroup(){
        final RadioGroup accessGroup = (RadioGroup) findViewById(R.id.Filter_rG_Access);
        accessGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedButton = (RadioButton) findViewById(checkedId);
                access = accessGroup.indexOfChild(checkedButton);
            }
        });
    }
    private void handleCheckBoxes(){
        CheckBox diaper = (CheckBox) findViewById(R.id.Filter_cB_Diaper);
        CheckBox condom = (CheckBox) findViewById(R.id.Filter_cB_Condom);
        CheckBox tampon = (CheckBox) findViewById(R.id.Filter_cB_Tampon);

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

    private void toastThis(String message){
        Toast.makeText(thisContext,
                message,
                Toast.LENGTH_LONG).show();
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
