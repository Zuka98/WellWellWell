package com.NHS.UCLTeam9.WellWellWell;

import android.Manifest;
import android.app.AlertDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.view.LayoutInflater;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;


public class SettingsActivity extends AppCompatActivity  {
    final private static String MY_PREFS_NAME = new String("sharepostcode");
    static String postcode;
    static String emis;
    static String nhs;
    private TextView postcode_update;
    private TextView reference1;
    private TextView reference2;
    private TextView postcode_update_sum;
    private String postcodes[];
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;


    private TextView reference1_sum;
    private TextView reference2_sum;
    private Switch anonyshare;

    final Context context = this;
    //private Button button;
    //private EditText result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("text", null);

        postcode = prefs.getString("postcode", "Not given");//"No name defined" is the default value.
        emis = prefs.getString("reference1", "Not given");//"No name defined" is the default value.
        nhs = prefs.getString("reference2", "Not given");//"No name defined" is the default value.
        Resources res = getResources();
        postcodes = res.getStringArray(R.array.postcodes);







        //setupActionBar();

        setContentView(R.layout.activity_settings);
        //getFragmentManager().beginTransaction().replace(android.R.id.content,
                //new MainSettingsFragment()).commit();

        postcode_update = (TextView) findViewById(R.id.postcode);
        reference1 = (TextView) findViewById(R.id.ref1);
        reference2 = (TextView) findViewById(R.id.ref2);

        postcode_update_sum = (TextView) findViewById(R.id.postcode_text);
        reference1_sum = (TextView) findViewById(R.id.ref1sum);
        reference2_sum = (TextView) findViewById(R.id.ref2sum);
        anonyshare = (Switch) findViewById(R.id.anonymoushare);

        reference1_sum.setText(emis);
        reference2_sum.setText(nhs);
        postcode_update_sum.setText(postcode);
        final SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();




        reference1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if permission was granted

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        reference1_sum.setText(userInput.getText());
                                        editor.putString("reference1", userInput.getText().toString());
                                        editor.apply();



                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();


            }


            //BASICALLY CHECKING FOR PERMISSION ISSUED OR NOT DUE TO VARIOUS OS SYSTEMS
//
//                if (!myDb.isdbempty()) {
//                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//                            //no permission granted so need to request it
//                            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                            requestPermissions(permissions, STORAGE_CODE);
//                        } else {
//                            makePdf();
//                            if (!myDb.noFeedback()) {
//                                makePdf();
//                            } else
//                                Toast.makeText(getApplicationContext(), "No feedback given yet! Please share your feedback.", Toast.LENGTH_LONG).show();
//                        }
//
//                    } else {
//                        Toast.makeText(getApplicationContext(), "No feedback given yet! Please share your feedback.", Toast.LENGTH_LONG).show();
//
//                    }
//
//                } else {
//                    Toast.makeText(getApplicationContext(), "No weekly scores to share yet! Please wait.", Toast.LENGTH_LONG).show();
//                }
//            }
//
        });

        reference2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if permission was granted

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                final TextView notice = (TextView) promptsView
                        .findViewById(R.id.titleTv);
                notice.setText("Please Enter Your Reference 2:");

                // set dialog message
                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        reference2_sum.setText(userInput.getText());
                                        editor.putString("reference2", userInput.getText().toString());
                                        editor.apply();



                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();


            }


            //BASICALLY CHECKING FOR PERMISSION ISSUED OR NOT DUE TO VARIOUS OS SYSTEMS
//
//                if (!myDb.isdbempty()) {
//                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//                            //no permission granted so need to request it
//                            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                            requestPermissions(permissions, STORAGE_CODE);
//                        } else {
//                            makePdf();
//                            if (!myDb.noFeedback()) {
//                                makePdf();
//                            } else
//                                Toast.makeText(getApplicationContext(), "No feedback given yet! Please share your feedback.", Toast.LENGTH_LONG).show();
//                        }
//
//                    } else {
//                        Toast.makeText(getApplicationContext(), "No feedback given yet! Please share your feedback.", Toast.LENGTH_LONG).show();
//
//                    }
//
//                } else {
//                    Toast.makeText(getApplicationContext(), "No weekly scores to share yet! Please wait.", Toast.LENGTH_LONG).show();
//                }
//            }
//
        });

        postcode_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if permission was granted

                new SimpleSearchDialogCompat(SettingsActivity.this, "Select your new postcode",
                        "What are you looking for?", null, createSampleData(),
                        new SearchResultListener<SearchModelForPostcode>() {
                            @Override
                            public void onSelected(BaseSearchDialogCompat dialog,
                                                   SearchModelForPostcode item, int position) {

                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("postcode", item.getTitle());


                                Toast.makeText(SettingsActivity.this, "postcode updated to:"+item.getTitle(),
                                        Toast.LENGTH_SHORT).show();
                                postcode_update_sum.setText(item.getTitle());
                                dialog.dismiss();
                            }
                        }).show();

            }
        });




            //BASICALLY CHECKING FOR PERMISSION ISSUED OR NOT DUE TO VARIOUS OS SYSTEMS
//
//                if (!myDb.isdbempty()) {
//                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//                            //no permission granted so need to request it
//                            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                            requestPermissions(permissions, STORAGE_CODE);
//                        } else {
//                            makePdf();
//                            if (!myDb.noFeedback()) {
//                                makePdf();
//                            } else
//                                Toast.makeText(getApplicationContext(), "No feedback given yet! Please share your feedback.", Toast.LENGTH_LONG).show();
//                        }
//
//                    } else {
//                        Toast.makeText(getApplicationContext(), "No feedback given yet! Please share your feedback.", Toast.LENGTH_LONG).show();
//
//                    }
//
//                } else {
//                    Toast.makeText(getApplicationContext(), "No weekly scores to share yet! Please wait.", Toast.LENGTH_LONG).show();
//                }
//            }
//



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
        beginCal.add((Calendar.MINUTE), -60*24*7);
        Calendar endCal = Calendar.getInstance();
        final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginCal.getTimeInMillis(), endCal.getTimeInMillis());

        Log.v("Usagestat list: ", queryUsageStats.toString());
        if (queryUsageStats.size() == 0) {
            Toast.makeText(SettingsActivity.this, "Please enable usage stats to use this app!", Toast.LENGTH_LONG).show();
        }
        else if (hasSMSPermission != PackageManager.PERMISSION_GRANTED || hasCallLogPermission != PackageManager.PERMISSION_GRANTED || hasFilesPermission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(SettingsActivity.this, "Please enable all permissions in order to use this app!", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(SettingsActivity.this, new String[] {Manifest.permission.READ_SMS, Manifest.permission.READ_CALL_LOG, Manifest.permission.RECEIVE_SMS, Manifest.permission.PACKAGE_USAGE_STATS, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);


        }
        else {

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }


    private ArrayList<SearchModelForPostcode> createSampleData(){
        ArrayList<SearchModelForPostcode> items = new ArrayList<>();
        for (String e : postcodes) {
            items.add(new SearchModelForPostcode(e));
        }

        return items;
    }

    /*
    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }




    public static class MainSettingsFragment extends PreferenceFragment{

        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            EditTextPreference r1 =(EditTextPreference)findPreference("reference1");//.setSummary(emis);
            r1.setSummary(emis);


            findPreference("reference2").setSummary(nhs);
            findPreference("postcode").setSummary(postcode+emis+nhs);

            bindSummaryValue(findPreference("reference1"));
            bindSummaryValue(findPreference("reference2"));
            //bindSummaryValue(findPreference("postcode"));
            //bindSummaryValue(findPreference("anonyshare"));
            //bindSummaryValue(findPreference("notification"));
            bindSummaryValue(findPreference("notification_ringtone"));


        }


        public static void LoadData(Context context)
        {
            //SharedPreferences SaveData = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();


            editor.apply();
        }
    }

    //private SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();


    private static Preference.OnPreferenceChangeListener  listener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if(preference instanceof EditTextPreference){

                preference.setSummary(stringValue);

            }else if (preference instanceof RingtonePreference){
                if(TextUtils.isEmpty(stringValue)){
                    //no ringtone
                    preference.setSummary("Silent");
                }else {
                    Ringtone ringtone = RingtoneManager.getRingtone(preference.getContext(),Uri.parse(stringValue));
                    if(ringtone==null){
                        preference.setSummary("Choose notification ringtone");

                    }else{
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);

                    }
                }
            }
            return false;
        }
    };

    private static void bindSummaryValue(Preference preference){
        preference.setOnPreferenceChangeListener(listener);
        listener.onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference
                        .getContext()).getString(preference.getKey(),""));

    }
    */
}
