package com.happybananastudio.loolookout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by mgint on 1/19/2018.
 */

public class AboutActivity extends Activity {
    Context thisContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFinishOnTouchOutside(false);
        handleWidgets();
    }
    private void handleWidgets(){
        handleMinimizeButton();
    }
    private void toastThis(String message){
        Toast.makeText(thisContext,
                message,
                Toast.LENGTH_SHORT).show();
    }
    private void handleMinimizeButton(){
        Button minimize = (Button) findViewById(R.id.About_Minimize);
        minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(0, 0);
            }

        });
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
