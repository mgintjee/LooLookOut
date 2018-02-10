package com.happybananastudio.loolookout;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * Created by mgint on 1/19/2018.
 */

public class filtersActivity extends Activity {
    private int gender = 0, size = 0, clean = 0, traffic = 0, access = 0, amenities = 0;
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
    }
    private void toastThis(String message){
        Toast.makeText(thisContext,
                message,
                Toast.LENGTH_SHORT).show();
    }
    private void handleMinimizeButton(){
        Button minimize = (Button) findViewById(R.id.b_Minimize);
        minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void handleGenderRadioGroup(){
        final RadioGroup genderGroup = (RadioGroup) findViewById(R.id.rG_Gender);
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedButton = (RadioButton) findViewById(checkedId);
                int index = genderGroup.indexOfChild(checkedButton);
                gender = index;
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
                int index = sizeGroup.indexOfChild(checkedButton);
                size = index;
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
                int index = cleanGroup.indexOfChild(checkedButton);
                clean = index;
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
                int index = trafficGroup.indexOfChild(checkedButton);
                traffic = index;
                toastThis(Integer.toString(traffic));
            }
        });
    }
    private void handleAccessRadioGroup(){
        final RadioGroup accesGroup = (RadioGroup) findViewById(R.id.rG_Access);
        accesGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedButton = (RadioButton) findViewById(checkedId);
                int index = accesGroup.indexOfChild(checkedButton);
                access = index;
                toastThis(Integer.toString(access));
            }
        });
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
