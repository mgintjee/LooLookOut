package com.happybananastudio.loolookout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

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
            TextView tVLocation = (TextView) findViewById(R.id.Report_tV_Location);
            tVLocation.setText(latLng.toString());
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
                finish();
                overridePendingTransition(0, 0);
            }
        });
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
