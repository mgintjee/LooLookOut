package com.happybananastudio.loolookout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class FiltersActivity extends AppCompatActivity {
    private final ArrayList<String> possibleClean = new ArrayList<>(
            Arrays.asList("At Least Very Dirty", "At Least Dirty", "At Least Neutral", "At Least Clean", "At Least Very Clean", "N/A"));
    private ArrayList<Integer> amenities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_settings);
        handleWidgets();
        GetIntentInformation();
    }

    private void GetIntentInformation() {
        Intent intent = getIntent();
        String filters = intent.getStringExtra("filters");

        if (!filters.equals("")) {
            String[] filterParts = filters.split(":");
            HandleGenderIntent(filterParts[0]);
            HandleSizeIntent(filterParts[1]);
            HandleCleanIntent(filterParts[2]);
            HandleTrafficIntent(filterParts[3]);
            HandleAccessIntent(filterParts[4]);
            HandleAmenitiesIntent(filterParts[5]);
        } else {
            DefaultFilters();
        }
    }

    private void HandleGenderIntent(String index) {
        int RB_Index;
        int i = Integer.valueOf(index);
        switch (i) {
            case 0: // Inclusive
                RB_Index = R.id.FS_RB_Inclusive;
                break;
            case 1: // Male
                RB_Index = R.id.FS_RB_Male;
                break;
            case 2: // Female
                RB_Index = R.id.FS_RB_Female;
                break;
            case 3: // Family
                RB_Index = R.id.FS_RB_Family;
                break;
            default: // N/A
                RB_Index = R.id.FS_RB_GenderNA;
                break;
        }
        RadioGroupTableLayout RGTL = (RadioGroupTableLayout) findViewById(R.id.FS_RGTL_Gender);
        RGTL.setCheckedRadioButtonId(RB_Index);
    }

    private void HandleSizeIntent(String index) {
        int RB_Index;
        int i = Integer.valueOf(index);
        switch (i) {
            case 0: // Single
                RB_Index = R.id.FS_RB_Single;
                break;
            case 1: // Small
                RB_Index = R.id.FS_RB_Small;
                break;
            case 2: // Medium
                RB_Index = R.id.FS_RB_Medium;
                break;
            case 3: // Large
                RB_Index = R.id.FS_RB_Large;
                break;
            default: // N/A
                RB_Index = R.id.FS_RB_SizeNA;
                break;
        }
        RadioGroupTableLayout RGTL = (RadioGroupTableLayout) findViewById(R.id.FS_RGTL_Size);
        RGTL.setCheckedRadioButtonId(RB_Index);
    }

    private void HandleCleanIntent(String index) {
        int i = Integer.valueOf(index);
        SeekBar SB = (SeekBar) findViewById(R.id.FS_SB_Clean);
        SB.setProgress(i);
    }

    private void HandleTrafficIntent(String index) {
        int RB_Index;
        int i = Integer.valueOf(index);
        switch (i) {
            case 0: // Low
                RB_Index = R.id.FS_RB_Low;
                break;
            case 1: // Some
                RB_Index = R.id.FS_RB_Some;
                break;
            case 2: // High
                RB_Index = R.id.FS_RB_High;
                break;
            default: // N/A
                RB_Index = R.id.FS_RB_TrafficNA;
                break;
        }
        RadioGroupTableLayout RGTL = (RadioGroupTableLayout) findViewById(R.id.FS_RGTL_Traffic);
        RGTL.setCheckedRadioButtonId(RB_Index);
    }

    private void HandleAccessIntent(String index) {
        int RB_Index;
        int i = Integer.valueOf(index);
        switch (i) {
            case 0: // Public
                RB_Index = R.id.FS_RB_Public;
                break;
            case 1: // Customer
                RB_Index = R.id.FS_RB_Customer;
                break;
            case 2: // Private
                RB_Index = R.id.FS_RB_Private;
                break;
            default: // N/A
                RB_Index = R.id.FS_RB_AccessNA;
                break;
        }
        RadioGroupTableLayout RGTL = (RadioGroupTableLayout) findViewById(R.id.FS_RGTL_Access);
        RGTL.setCheckedRadioButtonId(RB_Index);
    }

    private void HandleAmenitiesIntent(String index) {
        String[] filterAmenities = index.split(",");
        for (String filterAmenity : filterAmenities) {
            int intID = getCheckBoxId(filterAmenity);
            if (intID != 0) {
                CheckBox cB = (CheckBox) findViewById(intID);
                cB.toggle();
            }
        }
    }

    private void handleWidgets() {
        handleCleanSeekBar();
        handleCheckBoxes();
        InitializeButtons();
    }

    private void InitializeButtons() {
        InitializeCancelButton();
        InitializeResetButton();
        InitializeApplyButton();
    }

    private void InitializeCancelButton() {
        Button B = (Button) findViewById(R.id.FS_B_CancelFilters);
        B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }

        });
    }

    private void InitializeResetButton() {
        Button B = (Button) findViewById(R.id.FS_B_ResetFilters);
        B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DefaultFilters();
                SeekBar SB = (SeekBar) findViewById(R.id.FS_SB_Clean);
                SB.setProgress(possibleClean.size() - 1);
                CheckBox diaper = (CheckBox) findViewById(R.id.FS_CB_Diaper);
                CheckBox condom = (CheckBox) findViewById(R.id.FS_CB_Condom);
                CheckBox tampon = (CheckBox) findViewById(R.id.FS_CB_Tampon);

                if (diaper.isChecked()) {
                    diaper.toggle();
                }
                if (condom.isChecked()) {
                    condom.toggle();
                }
                if (tampon.isChecked()) {
                    tampon.toggle();
                }
            }
        });
    }

    private void InitializeApplyButton() {
        Button B = (Button) findViewById(R.id.FS_B_ApplyFilters);
        B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroupTableLayout RGTL_Gender = (RadioGroupTableLayout) findViewById(R.id.FS_RGTL_Gender);
                RadioGroupTableLayout RGTL_Size = (RadioGroupTableLayout) findViewById(R.id.FS_RGTL_Size);
                SeekBar SB_Clean = (SeekBar) findViewById(R.id.FS_SB_Clean);
                RadioGroupTableLayout RGTL_Traffic = (RadioGroupTableLayout) findViewById(R.id.FS_RGTL_Traffic);
                RadioGroupTableLayout RGTL_Access = (RadioGroupTableLayout) findViewById(R.id.FS_RGTL_Access);

                int ValueGender = GetRadioButtonValue(RGTL_Gender);
                int ValueSize = GetRadioButtonValue(RGTL_Size);
                int ValueClean = SB_Clean.getProgress();
                int ValueTraffic = GetRadioButtonValue(RGTL_Traffic);
                int ValueAccess = GetRadioButtonValue(RGTL_Access);

                Intent intent = new Intent();
                StringBuilder filters = new StringBuilder("");
                filters.append(String.valueOf(ValueGender)).append(":");
                filters.append(String.valueOf(ValueSize)).append(":");
                filters.append(String.valueOf(ValueClean)).append(":");
                filters.append(String.valueOf(ValueTraffic)).append(":");
                filters.append(String.valueOf(ValueAccess)).append(":");

                if (amenities.size() == 0) {
                    amenities.add(0);
                }

                for (int i = 0; i < amenities.size() - 1; ++i) {
                    filters.append(amenities.get(i)).append(",");
                }
                filters.append(amenities.get(amenities.size() - 1));
                intent.putExtra("filters", filters.toString());

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void handleCleanSeekBar() {
        final SeekBar SB = (SeekBar) findViewById(R.id.FS_SB_Clean);

        SB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView TV = (TextView) findViewById(R.id.FS_TV_Clean);
                TV.setText(possibleClean.get(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void handleCheckBoxes() {
        CheckBox diaper = (CheckBox) findViewById(R.id.FS_CB_Diaper);
        CheckBox condom = (CheckBox) findViewById(R.id.FS_CB_Condom);
        CheckBox tampon = (CheckBox) findViewById(R.id.FS_CB_Tampon);

        diaper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    amenities.add(1);
                } else {
                    amenities.remove(Integer.valueOf(1));
                }
            }
        });
        condom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    amenities.add(2);
                } else {
                    amenities.remove(Integer.valueOf(2));
                }
            }
        });
        tampon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    amenities.add(3);
                } else {
                    amenities.remove(Integer.valueOf(3));
                }
            }
        });
    }

    private int getCheckBoxId(String stringId) {
        int id = 0;

        switch (stringId) {
            case "0":
                id = 0;
                break;
            case "1":
                id = R.id.FS_CB_Diaper;
                break;
            case "2":
                id = R.id.FS_CB_Condom;
                break;
            case "3":
                id = R.id.FS_CB_Tampon;
                break;
        }

        return id;
    }

    private void DefaultFilters() {
        RadioGroupTableLayout RGTL_Gender = (RadioGroupTableLayout) findViewById(R.id.FS_RGTL_Gender);
        RadioGroupTableLayout RGTL_Size = (RadioGroupTableLayout) findViewById(R.id.FS_RGTL_Size);
        RadioGroupTableLayout RGTL_Traffic = (RadioGroupTableLayout) findViewById(R.id.FS_RGTL_Traffic);
        RadioGroupTableLayout RGTL_Access = (RadioGroupTableLayout) findViewById(R.id.FS_RGTL_Access);

        RGTL_Gender.setCheckedRadioButtonId(R.id.FS_RB_GenderNA);
        RGTL_Size.setCheckedRadioButtonId(R.id.FS_RB_SizeNA);
        RGTL_Traffic.setCheckedRadioButtonId(R.id.FS_RB_TrafficNA);
        RGTL_Access.setCheckedRadioButtonId(R.id.FS_RB_AccessNA);
    }

    private int GetRadioButtonValue(RadioGroupTableLayout RGTL) {
        int ID = RGTL.getCheckedRadioButtonId();
        int value = -1;
        switch (ID) {
            case R.id.FS_RB_GenderNA:
            case R.id.FS_RB_SizeNA:
                value = 4;
                break;
            case R.id.FS_RB_Family:
            case R.id.FS_RB_Large:
            case R.id.FS_RB_TrafficNA:
            case R.id.FS_RB_AccessNA:
                value = 3;
                break;
            case R.id.FS_RB_Female:
            case R.id.FS_RB_Medium:
            case R.id.FS_RB_High:
            case R.id.FS_RB_Private:
                value = 2;
                break;
            case R.id.FS_RB_Male:
            case R.id.FS_RB_Small:
            case R.id.FS_RB_Some:
            case R.id.FS_RB_Customer:
                value = 1;
                break;
            case R.id.FS_RB_Inclusive:
            case R.id.FS_RB_Single:
            case R.id.FS_RB_Low:
            case R.id.FS_RB_Public:
                value = 0;
                break;
            default:
                break;
        }
        return value;
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
