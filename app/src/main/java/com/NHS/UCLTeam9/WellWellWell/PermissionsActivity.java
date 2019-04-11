package com.NHS.UCLTeam9.WellWellWell;

import android.Manifest;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class PermissionsActivity extends AppCompatActivity {
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    Button statsbutton;
    Button mainlauncher;
    private static final int STORAGE_CODE = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
        statsbutton = (Button) findViewById(R.id.statsenable);
        mainlauncher = (Button) findViewById(R.id.mainstarter);


    }

    public void statslaunch(View view) {

        int hasSMSPermission = checkSelfPermission(Manifest.permission.READ_SMS);
        //int hasBrowserPermission = checkSelfPermission(Manifest.permission.);
        int hasCallLogPermission = checkSelfPermission(Manifest.permission.READ_CALL_LOG);
        int hasSMSReceivePermission = checkSelfPermission(Manifest.permission.RECEIVE_SMS);
        int hasUsageStatsPermission = checkSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS);
        int hasFilesPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasSMSPermission != PackageManager.PERMISSION_GRANTED && hasCallLogPermission != PackageManager.PERMISSION_GRANTED && hasSMSReceivePermission != PackageManager.PERMISSION_GRANTED && hasUsageStatsPermission != PackageManager.PERMISSION_GRANTED && hasFilesPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PermissionsActivity.this, new String[] {Manifest.permission.READ_SMS, Manifest.permission.READ_CALL_LOG, Manifest.permission.RECEIVE_SMS, Manifest.permission.PACKAGE_USAGE_STATS, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);

        }
        int hasFilesWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);


        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permissions, STORAGE_CODE);

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            // Toast.makeText(getApplicationContext(), "Enabling permission to export data as PDF document.", Toast.LENGTH_LONG).show();
            //String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //requestPermissions(permissions, STORAGE_CODE);
        }


        final UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);// Context.USAGE_STATS_SERVICE);
        Calendar beginCal = Calendar.getInstance();
        beginCal.set((Calendar.WEEK_OF_MONTH), (Calendar.WEEK_OF_MONTH - 2));

        Calendar endCal = Calendar.getInstance();

        final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginCal.getTimeInMillis(), endCal.getTimeInMillis());
        Log.v("Results:","results for " + beginCal.getTime().toString() + " - " + endCal.getTime().toString());
        if (queryUsageStats.size() == 0) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    public void toRegister(View view) {

        int hasSMSPermission = checkSelfPermission(Manifest.permission.READ_SMS);
        //int hasBrowserPermission = checkSelfPermission(Manifest.permission.);
        int hasCallLogPermission = checkSelfPermission(Manifest.permission.READ_CALL_LOG);
        int hasSMSReceivePermission = checkSelfPermission(Manifest.permission.RECEIVE_SMS);
        int hasUsageStatsPermission = checkSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS);
        int hasFilesPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

        final UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);// Context.USAGE_STATS_SERVICE);
        Calendar beginCal = Calendar.getInstance();
        beginCal.add((Calendar.MINUTE), -60*24*7);
        Calendar endCal = Calendar.getInstance();
        final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginCal.getTimeInMillis(), endCal.getTimeInMillis());

        Log.v("Usagestat list: ", queryUsageStats.toString());
        if (queryUsageStats.size() == 0) {
            Toast.makeText(PermissionsActivity.this, "Please enable usage stats to use this app!", Toast.LENGTH_LONG).show();
        }
        else if (hasSMSPermission != PackageManager.PERMISSION_GRANTED || hasCallLogPermission != PackageManager.PERMISSION_GRANTED || hasFilesPermission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(PermissionsActivity.this, "Please enable all permissions in order to use this app!", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(PermissionsActivity.this, new String[] {Manifest.permission.READ_SMS, Manifest.permission.READ_CALL_LOG, Manifest.permission.RECEIVE_SMS, Manifest.permission.PACKAGE_USAGE_STATS, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);


        }
        else {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }



}
