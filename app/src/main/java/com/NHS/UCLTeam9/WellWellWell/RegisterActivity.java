package com.NHS.UCLTeam9.WellWellWell;

import android.Manifest;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;


public class RegisterActivity extends AppCompatActivity {
    private ListView lv;
    //private Toolbar toolbar;
    private EditText postcodeinpt;
    private EditText reference1;
    private EditText reference2;
    private String refe1;
    private String refe2;
    private String postcd;
    //private EditText postcodeinpt;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;


    private final String MY_PREFS_NAME = new String("sharepostcode");

    private ArrayAdapter<String> adapter;
    private String postcodes[];
    //private String postcodes[] = {"N1", "N2", "N3"}; //, "N4", "E15", "E16", "E17", "E18"};
    private Button input_postcode;
    private TextView pc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Register");
        super.onCreate(savedInstanceState);
        Resources res = getResources();
        setContentView(R.layout.activity_register);

        reference1 = (EditText) findViewById(R.id.inputReference1);
        reference2 = (EditText) findViewById(R.id.inputReference2);

       // Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

        input_postcode=(Button)findViewById(R.id.btn_update_pc);
        pc=(TextView)findViewById(R.id.pc);

        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        refe1 = sharedPreferences.getString("reference1", "Not given");//"No name defined" is the default value.
        refe2 = sharedPreferences.getString("reference2", "Not given");//"No name defined" is the default value.
        postcd = sharedPreferences.getString("postcode", "Not given");//"No name defined" is the default value.
        reference1.setText(refe1);
        reference2.setText(refe2);






        postcodes = res.getStringArray(R.array.postcodes);




        input_postcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if permission was granted

                new SimpleSearchDialogCompat(RegisterActivity.this, "Select your new postcode",
                        "What are you looking for?", null, createSampleData(),
                        new SearchResultListener<SearchModelForPostcode>() {
                            @Override
                            public void onSelected(BaseSearchDialogCompat dialog,
                                                   SearchModelForPostcode item, int position) {

                                //SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                //editor.putString("postcode", item.getTitle());


                                Toast.makeText(RegisterActivity.this, "postcode selected:"+item.getTitle(),
                                        Toast.LENGTH_SHORT).show();
                                pc.setText(item.getTitle());
                                dialog.dismiss();
                            }
                        }).show();

            }
        });
    }
    private ArrayList<SearchModelForPostcode> createSampleData(){
        ArrayList<SearchModelForPostcode> items = new ArrayList<>();
        for (String e : postcodes) {
            items.add(new SearchModelForPostcode(e));
        }

        return items;
    }


    public void viewMain(View view) {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("postcode", pc.getText().toString());
        editor.putString("reference1", reference1.getText().toString());
        editor.putString("reference2", reference2.getText().toString());
        editor.apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);


    }
    public void mainlaunch(View view) {

        String postcd = pc.getText().toString();

        boolean isEmpty = postcd == null || postcd.equals("");
        Log.v("Postocde default value", postcd);
        if (isEmpty){
            Toast.makeText(getApplicationContext(),"Please indicate the postcode area address to proceed!",Toast.LENGTH_SHORT).show();

        }else {

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
            Toast.makeText(RegisterActivity.this, "Please enable usage stats to use this app!", Toast.LENGTH_LONG).show();
        }
        else if (hasSMSPermission != PackageManager.PERMISSION_GRANTED || hasCallLogPermission != PackageManager.PERMISSION_GRANTED || hasFilesPermission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(RegisterActivity.this, "Please enable all permissions in order to use this app!", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[] {Manifest.permission.READ_SMS, Manifest.permission.READ_CALL_LOG, Manifest.permission.RECEIVE_SMS, Manifest.permission.PACKAGE_USAGE_STATS, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);


        }
        else {
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("postcode", pc.getText().toString());
            editor.putString("reference1", reference1.getText().toString());
            editor.putString("reference2", reference2.getText().toString());



            editor.apply();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }}
    }


}
