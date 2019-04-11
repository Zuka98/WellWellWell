package com.NHS.UCLTeam9.WellWellWell;


import android.Manifest;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
        import android.text.Editable;
        import android.text.TextWatcher;
        import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
        import android.widget.Filter;
        import android.widget.ListView;
        import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;


public class Register extends AppCompatActivity {

    private ListView lv;
    private EditText editText;
    private EditText reference1;
    private EditText reference2;
    private Button input_postcode;
    private TextView pc;
    //private EditText editText;


    private ArrayAdapter<String> adapter;
    private String postcodes[];
    //private String postcodes[] = {"N1", "N2", "N3"}; //, "N4", "E15", "E16", "E17", "E18"};


    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    final private String MY_PREFS_NAME = new String("sharepostcode");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources res = getResources();
        setContentView(R.layout.activity_register);
        lv = (ListView) findViewById(R.id.postcodeList);
        editText = (EditText) findViewById(R.id.postCodeInput);
        reference1 = (EditText) findViewById(R.id.inputReference1);
        reference2 = (EditText) findViewById(R.id.inputReference2);
        input_postcode=(Button)findViewById(R.id.btn_update_pc);
        pc=(TextView)findViewById(R.id.pc);

        postcodes = res.getStringArray(R.array.postcodes);
        adapter = new ArrayAdapter<String>(this, R.layout.postcode_item, R.id.postcodes, postcodes);
        lv.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                adapter.getFilter().filter(cs, new Filter.FilterListener() {
                    public void onFilterComplete(int count) {
                        if(count == 0) {
                            Toast.makeText(getApplicationContext(),"Invalid postcode!",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                //lv.setEmptyView(findViewById(R.id.postcodeList));
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                //null
            }
        });

        lv.setOnItemClickListener(listClick);


        input_postcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if permission was granted

                new SimpleSearchDialogCompat(Register.this, "Select your new postcode",
                        "What are you looking for?", null, createSampleData(),
                        new SearchResultListener<SampleSearchModel>() {
                            @Override
                            public void onSelected(BaseSearchDialogCompat dialog,
                                                   SampleSearchModel item, int position) {

                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("postcode", item.getTitle());


                                Toast.makeText(Register.this, "postcode selected:"+item.getTitle(),
                                        Toast.LENGTH_SHORT).show();
                                pc.setText(item.getTitle());
                                dialog.dismiss();
                            }
                        }).show();

            }
        });
    }

    private AdapterView.OnItemClickListener listClick = new AdapterView.OnItemClickListener () {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            String postcode = (String) lv.getItemAtPosition( position );
            editText.setText(postcode);
        }
    };



    private ArrayList<SampleSearchModel> createSampleData(){
        ArrayList<SampleSearchModel> items = new ArrayList<>();
        for (String e : postcodes) {
            items.add(new SampleSearchModel(e));
        }

        return items;
    }

    public void mainlaunch(View view) {
            int hasSMSPermission = checkSelfPermission(Manifest.permission.READ_SMS);
            //int hasBrowserPermission = checkSelfPermission(Manifest.permission.);
            int hasCallLogPermission = checkSelfPermission(Manifest.permission.READ_CALL_LOG);
            int hasSMSReceivePermission = checkSelfPermission(Manifest.permission.RECEIVE_SMS);
            int hasUsageStatsPermission = checkSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS);
            int hasFilesPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

            final UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);// Context.USAGE_STATS_SERVICE);
            Calendar beginCal = Calendar.getInstance();
            beginCal.add((Calendar.MINUTE), -60 * 24 * 7);
            Calendar endCal = Calendar.getInstance();
            final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginCal.getTimeInMillis(), endCal.getTimeInMillis());

            Log.v("Usagestat list: ", queryUsageStats.toString());
            if (queryUsageStats.size() == 0) {
                Toast.makeText(Register.this, "Please enable usage stats to use this app!", Toast.LENGTH_LONG).show();
            } else if (hasSMSPermission != PackageManager.PERMISSION_GRANTED || hasCallLogPermission != PackageManager.PERMISSION_GRANTED || hasFilesPermission != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Register.this, "Please enable all permissions in order to use this app!", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(Register.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CALL_LOG, Manifest.permission.RECEIVE_SMS, Manifest.permission.PACKAGE_USAGE_STATS, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);


            } else {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("postcode", editText.getText().toString());
                editor.putString("reference1", reference1.getText().toString());
                editor.putString("reference2", reference2.getText().toString());


                editor.apply();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }

    }

}