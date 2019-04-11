package com.NHS.UCLTeam9.WellWellWell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setTitle("Welcome to WellWellWell!");
    }
    public void infoscreen(View view){
        Intent intent = new Intent(this, MonitoringInfo.class);
        startActivity(intent);
    }
}
