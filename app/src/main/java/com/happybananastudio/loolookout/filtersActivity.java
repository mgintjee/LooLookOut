package com.happybananastudio.loolookout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class filtersActivity extends Activity {
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
        handleCleanRadioGroup();
        handleTrafficRadioGroup();
        handleAccessRadioGroup();
        handleCheckBoxes();
        handleClearFiltersButton();
    }
    private void handleMinimizeButton(){
        Button minimize = (Button) findViewById(R.id.b_Minimize);
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

                Log.d("Start", "----");
                for(int i = 0; i < amenities.size(); ++i ){
                    Log.d("DEBUG", Integer.toString(amenities.get(i)));
                }
                Log.d("End", "----");

                for( int i = 0; i < amenities.size() - 1; ++i ){
                    filters.append(amenities.get(i)).append(",");
                }
                filters.append(amenities.get(amenities.size() - 1));
                intent.putExtra("filters", filters.toString());

                Log.d("Filter",filters.toString());

                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(0, 0);
            }

        });
    }
    private void handleClearFiltersButton(){
        Button clearFilters = (Button) findViewById(R.id.b_ClearFilters);
        clearFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup genderGroup = (RadioGroup) findViewById(R.id.rG_Gender);
                RadioGroup sizeGroup = (RadioGroup) findViewById(R.id.rG_Size);
                RadioGroup cleanGroup = (RadioGroup) findViewById(R.id.rG_Clean);
                RadioGroup trafficGroup = (RadioGroup) findViewById(R.id.rG_Traffic);
                RadioGroup accessGroup = (RadioGroup) findViewById(R.id.rG_Access);
                CheckBox diaper = (CheckBox) findViewById(R.id.cB_Diaper);
                CheckBox condom = (CheckBox) findViewById(R.id.cB_Condom);
                CheckBox tampon = (CheckBox) findViewById(R.id.cB_Tampon);

                genderGroup.check(R.id.NA_gender);
                sizeGroup.check(R.id.NA_size);
                cleanGroup.check(R.id.NA_clean);
                trafficGroup.check(R.id.NA_traffic);
                accessGroup.check(R.id.NA_access);

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
        final RadioGroup genderGroup = (RadioGroup) findViewById(R.id.rG_Gender);
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedButton = (RadioButton) findViewById(checkedId);
                gender = genderGroup.indexOfChild(checkedButton);
                toastThis(Integer.toString(gender));
            }
        });
    }
    private void handleSizeRadioGroup(){
        final RadioGroup sizeGroup = (RadioGroup) findViewById(R.id.rG_Size);
        sizeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedButton = (RadioButton) findViewById(checkedId);
                size = sizeGroup.indexOfChild(checkedButton);
                toastThis(Integer.toString(size));
            }
        });
    }
    private void handleCleanRadioGroup(){
        final RadioGroup cleanGroup = (RadioGroup) findViewById(R.id.rG_Clean);
        cleanGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedButton = (RadioButton) findViewById(checkedId);
                clean = cleanGroup.indexOfChild(checkedButton);
                toastThis(Integer.toString(clean));
            }
        });
    }
    private void handleTrafficRadioGroup(){
        final RadioGroup trafficGroup = (RadioGroup) findViewById(R.id.rG_Traffic);
        trafficGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedButton = (RadioButton) findViewById(checkedId);
                traffic = trafficGroup.indexOfChild(checkedButton);
                toastThis(Integer.toString(traffic));
            }
        });
    }
    private void handleAccessRadioGroup(){
        final RadioGroup accessGroup = (RadioGroup) findViewById(R.id.rG_Access);
        accessGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedButton = (RadioButton) findViewById(checkedId);
                access = accessGroup.indexOfChild(checkedButton);
                toastThis(Integer.toString(access));
            }
        });
    }
    private void handleCheckBoxes(){
        CheckBox diaper = (CheckBox) findViewById(R.id.cB_Diaper);
        CheckBox condom = (CheckBox) findViewById(R.id.cB_Condom);
        CheckBox tampon = (CheckBox) findViewById(R.id.cB_Tampon);

        diaper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked ){
                    amenities.add(1);
                }
                else{
                    amenities.remove(Integer.valueOf(1));
                }
                Log.d("Start", "----");
                for(int i = 0; i < amenities.size(); ++i ){
                    Log.d("DEBUG", Integer.toString(amenities.get(i)));
                }
                Log.d("End", "----");
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
