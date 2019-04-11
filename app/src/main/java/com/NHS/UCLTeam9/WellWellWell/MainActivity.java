package com.NHS.UCLTeam9.WellWellWell;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.content.BroadcastReceiver;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import me.everything.providers.android.browser.BrowserProvider;
import me.everything.providers.android.calllog.Call;
import me.everything.providers.android.calllog.CallsProvider;
import me.everything.providers.android.telephony.Mms;
import me.everything.providers.android.telephony.Sms;
import me.everything.providers.android.telephony.TelephonyProvider;
import me.everything.providers.core.Data;


public class MainActivity extends AppCompatActivity {

    public static Button viewstats;
    static Button startServiceBtn;
    static Button stopServiceBtn;
    static Button startServiceBtn2;


    boolean serviceOn;
    Dialog epicDialog1;
    Dialog epicDialog2;
    EditText inputfeedbackscore;
    Button btnSaveFeedback;
    Button one,two,thr,four,five,six,seven,thezero,eight,nine,ten;

    LifeDataUpdate lifeDataUpdate;





    ImageView    closePopup,closePopup1;

    Button btnYes,btnNo;
    TextView tvScoreDisplay;

    TextView selectedScoreDisplay;


    TelephonyProvider telephonyProvider;
    BrowserProvider browserProvider;
    CallsProvider callsProvider;

    //Accessing SMS and MMS sent text (for "Connect")
    private List<Sms> smses;
    private List<Mms> mmses;

    public String smsString;
    public String mmsString;

    public int oldsmssize;
    public int oldmmssize;
    int zero;

    //Accessing call logs (for "Give", "Connect")
    private Data<Call> calls;

    //Phone activity info
    static String countedSteps;
    String detectedSteps;
    String messageContent;
    int countedMessages;
    int countedCalls;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static int old;

    String alarmcheck;


    private static final String TAG = "SensorEvent";
    //Strings for the service



    public static final String UPDATE_TEXT = "com.example.jakesetton.myfirstapp.MESSAGE";










    DatabaseHelper myDb;

    private TensorFlowClassifier classifier;
    private float[] results;
    float[] dummy;
    public static TextView scoreView;
    public static TextView denominator;
    int weeklyscore;

    private static String choice;

    Switch privacySwitch;
    static String privacyStatus;

    static Button moreinfo;
    static TextView scoreMsg;
    static ImageView waysinfo;





    //insert button
    static Button insertDummy;






    private static final int STORAGE_CODE = 1000;


    static List<JavaClassifier.Instance> instances;
    static double[] x;
    static int score;

    static double[] mean;
    static double[] stdev;

    static ProgressBar progress;
    static ProgressBar progress2;

    static View circle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTitle("Home");
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        lifeDataUpdate=new LifeDataUpdate(this);







        insertDummy = (Button) findViewById(R.id.dummy_insert);

        insertDummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if permission was granted
                insertDummy.setEnabled(false);
                // call insert dummy here

                String checkalarm = preferences.getString("alarmstatus", "");


                if (checkalarm.equals("on") && isMyServiceRunning(ThePedometerService.class)) {

                    try{
                        insertDummyData();
                    }
                    catch (Exception e){}


                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1*
                                        60*
                                        1000);//min secs millisecs
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            MainActivity.this.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    insertDummy.setEnabled(true);

                                }
                            });
                        }
                    }).start();

                    Toast.makeText(MainActivity.this, "Dummy data inserted, please wait for one minute to click again",Toast.LENGTH_LONG).show();

                }

                else{
                    Toast.makeText(MainActivity.this, "Please start tracking first!",Toast.LENGTH_LONG).show();
                    insertDummy.setEnabled(true);

                }






            }
        });








        viewstats = (Button) findViewById(R.id.stats);
        epicDialog1=new Dialog(this);
        epicDialog2=new Dialog(this);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress2 = (ProgressBar) findViewById(R.id.progBackground);
        waysinfo = (ImageView) findViewById(R.id.waysinfo);
        progress.setRotation(270);
        progress2.setRotation(270);
        scoreMsg = (TextView) findViewById(R.id.scoremsg);
        scoreView = (TextView) findViewById(R.id.score);




        circle = (View) findViewById(R.id.view);
        privacySwitch = (Switch) findViewById(R.id.switchprivacy);


        moreinfo = (Button) findViewById(R.id.moreinfo);
        //moreinfo.setPaintFlags(moreinfo.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG );
        myDb = new DatabaseHelper(this);
        //classifier = new TensorFlowClassifier(this);
        preferences = this.getSharedPreferences("MyPreferences", this.MODE_PRIVATE);
        editor = preferences.edit();



        //a check to see if the app has already been set up (i.e. if Usage Stats have been enabled) - if not, launch the welcome page
        final UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);// Context.USAGE_STATS_SERVICE);
        Calendar beginCal = Calendar.getInstance();
        beginCal.add((Calendar.MINUTE), -60*24*7);
        Calendar endCal = Calendar.getInstance();
        final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginCal.getTimeInMillis(), endCal.getTimeInMillis());

        int hasSMSPermission = checkSelfPermission(Manifest.permission.READ_SMS);
        int hasCallLogPermission = checkSelfPermission(Manifest.permission.READ_CALL_LOG);
        int hasCallLogPermission2 = checkSelfPermission(Manifest.permission.WRITE_CALL_LOG);

        int hasSMSReceivePermission = checkSelfPermission(Manifest.permission.RECEIVE_SMS);
        int hasUsageStatsPermission = checkSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS);
        int hasFilesPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);








        int hasFilesWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);


        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //requestPermissions(permissions, STORAGE_CODE);

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
           // Toast.makeText(getApplicationContext(), "Enabling permission to export data as PDF document.", Toast.LENGTH_LONG).show();
            //String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions, STORAGE_CODE);
        }






        final String[] NECESSARY_PERMISSIONS = new String[] {Manifest.permission.GET_ACCOUNTS };

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {

            //Permission is granted

        } else {

            //ask for permission

            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    NECESSARY_PERMISSIONS, 123);
        }



        if (hasSMSPermission != PackageManager.PERMISSION_GRANTED && hasCallLogPermission2 != PackageManager.PERMISSION_GRANTED && hasCallLogPermission != PackageManager.PERMISSION_GRANTED && hasSMSReceivePermission != PackageManager.PERMISSION_GRANTED && hasUsageStatsPermission != PackageManager.PERMISSION_GRANTED && hasFilesPermission != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
        }
        if (queryUsageStats.size() == 0) {
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
        }



        Log.d("Calling onCreate main: ", String.valueOf(System.currentTimeMillis()));

        privacySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    editor.putString("privacystatus", "on");
                    editor.apply();
                }
                else {
                    editor.putString("privacystatus", "off");
                    editor.apply();
                }
            }
        });

        setup();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {

        case R.id.share:
            Intent shareActivity = new Intent(this, ShareActivity.class);
            startActivity(shareActivity);
            return(true);

        case R.id.about:
            Intent aboutPage = new Intent(this, AboutPage.class);
            startActivity(aboutPage);
            return(true);

        case R.id.edit_permission:
            Intent permissions = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(permissions);
            return(true);

        case R.id.settings:
            Intent appSettings = new Intent(this, SettingsActivity.class);
            startActivity(appSettings);
            return(true);

    }
        return(super.onOptionsItemSelected(item));
    }


    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }




    public void viewAbout(View view){
        Intent intent = new Intent(this, AboutPage.class);
        startActivity(intent);
    }

    public void viewShare(View view) {
        Intent intent = new Intent(this, ShareActivity.class);
        startActivity(intent);
    }


    public void viewlive(View view) {
        if (countedSteps == null) {
            Toast.makeText(MainActivity.this, "Start monitoring in order to view stats!", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, LiveSensorsActivity.class);
        //intent.putExtra(UPDATE_TEXT, countedSteps);
        editor.putInt("freshsteps",Integer.parseInt(countedSteps));
        editor.apply();

        Log.d("Counted steps: ", countedSteps);

        if (isMyServiceRunning(ThePedometerService.class) == true) {
            old = preferences.getInt("oldstep", 0);
            Log.d("Old value is now:", String.valueOf(old));

        }
        else if (isMyServiceRunning(ThePedometerService.class) == false) {
            Toast.makeText(MainActivity.this, "Start monitoring in order to view stats!", Toast.LENGTH_LONG).show();
            return;
        }
        startActivity(intent);
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        scoreView = (TextView) findViewById(R.id.score);
        denominator = (TextView) findViewById(R.id.denominator);
        alarmcheck = preferences.getString("alarmstatus", "");
        if (alarmcheck.equals("on") && isMyServiceRunning(ThePedometerService.class)) {
            stopServiceBtn.setVisibility(View.VISIBLE);
            startServiceBtn.setVisibility(View.INVISIBLE);
            scoreMsg.setVisibility(View.VISIBLE);
            waysinfo.setVisibility(View.VISIBLE);
            circle.setVisibility(View.INVISIBLE);
            if (myDb.isdbempty()) {
                scoreView.setVisibility(View.VISIBLE);
                circle.setVisibility(View.VISIBLE);
            }
        }
        Intent fromalarm = getIntent();
        if (fromalarm.getStringExtra("origin") != null) {
            if (fromalarm.getStringExtra("origin").equals("alarm")) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.v("On feedback called:", "now");
                            showFeedBack();
                    }
                }, 900);
            }
            fromalarm.removeExtra("origin");
        }
        if (fromalarm.getStringExtra("history") != null) { //code to jump to live week page from history
            if (fromalarm.getStringExtra("history").equals("liveweek")) {
                viewstats.performClick();
            }
        }
        Log.v("onResume:", "executed");

        //Send people to the first page of the app if they haven't granted permissions yet
        Calendar beginCal = Calendar.getInstance(); //is there a better way of doing this than checking for usage stats?
        beginCal.add((Calendar.MINUTE), -60*24*7);
        Calendar endCal = Calendar.getInstance();
        final UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginCal.getTimeInMillis(), endCal.getTimeInMillis());
        Intent intent;
        if (queryUsageStats.size() == 0) {
            intent = new Intent(this, StartActivity.class);
            startActivity(intent);
        }

        String latestscore = preferences.getString("score", "");
        Log.v("Latest score in S.Pref:", latestscore);
        if (!myDb.isdbempty() && !latestscore.equals("-1")) { //if there's a score in the db
                int scoreint = Integer.parseInt(latestscore);
                circle.setVisibility(View.INVISIBLE);
                //scoreint = 10;
                scoreView.setVisibility(View.VISIBLE);
                denominator.setVisibility(View.VISIBLE);
                moreinfo.setVisibility(View.VISIBLE);
                scoreMsg.setVisibility(View.VISIBLE);
                scoreView.setTextSize(145);
                waysinfo.setVisibility(View.VISIBLE);
                setProgressColours(scoreint);
            if (isMyServiceRunning(ThePedometerService.class)) {
                startServiceBtn.setVisibility(View.INVISIBLE);
                stopServiceBtn.setVisibility(View.VISIBLE);
            }
            else {
                stopServiceBtn.setVisibility(View.INVISIBLE);
                startServiceBtn2.setVisibility(View.VISIBLE);
                startServiceBtn.setVisibility(View.INVISIBLE);
            }
        }
    }


    public void setup() {

        privacyStatus = preferences.getString("privacystatus", "");
        if (privacyStatus.equals("on")) {
            privacySwitch.setChecked(true);
        }
        startServiceBtn = (Button) findViewById(R.id.startmonitoring);
        startServiceBtn2 = (Button) findViewById(R.id.startmonitoring2);
        startServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //starts the pedometer counting service
                editor.putInt("oldstep", zero);
                editor.apply();
                startStepCount(); //the method that starts the Service class
                insertData(view); //the method that establishes weekly data logging and classifications
            }
        });
        startServiceBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //starts the pedometer counting service
                String latest = preferences.getString("score", "");
                startStepCount(); //the method that starts the Service class
                insertData(view); //the method that establishes weekly data logging and classifications
                startServiceBtn2.setVisibility(View.INVISIBLE);
                circle.setVisibility(View.INVISIBLE);
                progress.setVisibility(View.VISIBLE);
                progress2.setVisibility(View.VISIBLE);
                scoreView.setVisibility(View.VISIBLE);
                editor.putString("score", latest);
                editor.apply();

            }
        });

        stopServiceBtn = (Button) findViewById(R.id.stopmonitoring);
        stopServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //if the user clicks 'stop monitoring'
                if (serviceOn) {
                    unregisterReceiver(broadcastReceiver);
                    stopService(new Intent(getBaseContext(), ThePedometerService.class));
                    editor.putString("alarmstatus", "off");
                    editor.apply();
                    serviceOn = false;
                    stopServiceBtn.setVisibility(View.INVISIBLE);
                    startServiceBtn2.setVisibility(View.VISIBLE);
                    if (myDb.isdbempty()) {
                        scoreMsg.setVisibility(View.INVISIBLE);
                        circle.setVisibility(View.VISIBLE);
                        denominator.setVisibility(View.INVISIBLE);
                        progress.setVisibility(View.INVISIBLE);
                        progress2.setVisibility(View.INVISIBLE);
                        scoreView.setVisibility(View.INVISIBLE);
                        startServiceBtn2.setVisibility(View.INVISIBLE);
                        startServiceBtn.setVisibility(View.VISIBLE);
                        waysinfo.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

/*
        if(lifeDataUpdate.checkIfFirstLaunchApp()){
            lifeDataUpdate.SaveDataToInitialState();
        }
        */

        if (isMyServiceRunning(ThePedometerService.class) == true) {
            startStepCount();
        }

        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {

            finish();
            return;
        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) { //checks whether the pedometer is working
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void startStepCount(){
        Intent intent = new Intent(MainActivity.this, ThePedometerService.class);
        ContextCompat.startForegroundService(this, intent);
        registerReceiver(broadcastReceiver, new IntentFilter(ThePedometerService.BROADCAST_ACTION));
        serviceOn = true;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // call updateUI passing in our intent which is holding the data to display.
            updateViews(intent);
        }
    };



    private void updateViews(Intent intent) { //the pedometer service updates the static variable countedsteps
        if (intent.getStringExtra("Counted_Step") != null) {
            countedSteps = intent.getStringExtra("Counted_Step");

            editor.putInt("freshsteps",Integer.parseInt(countedSteps));
            editor.apply();
        }
        else {
            Log.v("counted int value", "null");
        }
        detectedSteps = intent.getStringExtra("Detected_Step");
        Log.d(TAG, String.valueOf(countedSteps));
    }



    //this is initializing data
    public void insertData(View view) {
        //Start by converting training set .csv file into String,
        // to be written as new file into internal storage
        //internal storage is not read-only,
        // whereas Assets folder is, so cannot update file with new data otherwise
        String data = loadAssetTextAsString(this, "welldata_java_vsn_2.csv");
        writeStringAsFile(data, "welldata_java_vsn_2.csv");
        editor.putString("alarmstatus", "on");
        editor.apply();
        editor.putString("score", "-1");
        editor.apply();
        circle.setVisibility(View.VISIBLE);
        scoreMsg.setVisibility(View.VISIBLE);
        scoreView.setVisibility(View.VISIBLE);
        waysinfo.setVisibility(View.VISIBLE);

        telephonyProvider = new TelephonyProvider(this); //when the alarm is turned on, store the current total number of outgoing calls and texts
        browserProvider = new BrowserProvider(this);
        callsProvider = new CallsProvider(this);
        smses = telephonyProvider.getSms(TelephonyProvider.Filter.SENT).getList();
        mmses = telephonyProvider.getMms(TelephonyProvider.Filter.SENT).getList();
        messageContent = smses.toString();
        countedMessages = smses.size() + mmses.size();
        calls = callsProvider.getCalls();
        countedCalls = calls.getList().size();

        editor.putInt("callcount", countedCalls);
        editor.putInt("messagecount", countedMessages);
        editor.apply();
        //(this is for comparison when the data is stored at the end of the first week)


        stopServiceBtn.setVisibility(View.VISIBLE);
        startServiceBtn.setVisibility(View.INVISIBLE);
        Log.v("alarmstatus is ", preferences.getString("alarmstatus", ""));
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long time= System.currentTimeMillis();
        Date d = new Date(time);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.MINUTE, 60*24*7);
        time = c.getTimeInMillis();
        editor.putLong("alarmtime", time);
        editor.apply();
        Log.v("alarmtime is now: ",String.valueOf(new Date(preferences.getLong("alarmtime", 0))));
        Intent intent = new Intent(this, Alarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setRepeating(AlarmManager.RTC, time,1000*60*60*24*7, pendingIntent);
        Toast.makeText(MainActivity.this, "Tracking Started!", Toast.LENGTH_LONG).show();

    }



    @Override
    protected void onDestroy() {
        if (isMyServiceRunning(ThePedometerService.class) == true) {
            if (countedSteps != null) {
                old = preferences.getInt("oldstep", 0);
                int newcount = Integer.parseInt(countedSteps);
                Log.v("int new count = ", String.valueOf(newcount));
                Long testtime = preferences.getLong("alarmtime", 0);
                Log.v("current long:", String.valueOf(testtime));  //will need to delete this towards the end of the project
                editor.putInt("oldstep", newcount);
                editor.commit();
            }
        }
        super.onDestroy();
    }




    //this will be automated when the alarm goes off
    public void showFeedBack() {
        //final TextView textViewtmp = (TextView) findViewById(R.id.text);
        Cursor res = myDb.getLastLine();
        Cursor res1 = myDb.getAllData();


        epicDialog1.setContentView(R.layout.epic_yesnofeedback);

        closePopup = (ImageView) epicDialog1.findViewById(R.id.closePopup);

        btnYes = (Button) epicDialog1.findViewById(R.id.yes);
        btnNo = (Button) epicDialog1.findViewById(R.id.no);
        tvScoreDisplay = (TextView) epicDialog1.findViewById(R.id.titleTv);





        epicDialog2.setContentView(R.layout.epic_inputfeedback);
        closePopup1=(ImageView) epicDialog2.findViewById(R.id.closePopup);
        btnSaveFeedback=(Button) epicDialog2.findViewById(R.id.savefeedback);

        thezero=(Button) epicDialog2.findViewById(R.id.zero);
        one=(Button) epicDialog2.findViewById(R.id.one);
        two=(Button) epicDialog2.findViewById(R.id.two);
        thr=(Button) epicDialog2.findViewById(R.id.three);
        four=(Button) epicDialog2.findViewById(R.id.four);
        five =(Button) epicDialog2.findViewById(R.id.five);
        six=(Button) epicDialog2.findViewById(R.id.six);
        seven=(Button) epicDialog2.findViewById(R.id.seven);
        eight=(Button) epicDialog2.findViewById(R.id.eight);
        nine =(Button) epicDialog2.findViewById(R.id.nine);
        ten=(Button) epicDialog2.findViewById(R.id.ten);


        inputfeedbackscore=(EditText) epicDialog2.findViewById(R.id.inputfeedbackscore);
        selectedScoreDisplay=(TextView) epicDialog2.findViewById(R.id.selectedscore);







        thezero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedScoreDisplay.setText("0");
            }
        });

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedScoreDisplay.setText("1");
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedScoreDisplay.setText("2");
            }
        });

        thr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedScoreDisplay.setText("3");
            }
        });

        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedScoreDisplay.setText("4");
            }
        });

        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedScoreDisplay.setText("5");
            }
        });

        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedScoreDisplay.setText("6");
            }
        });

        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedScoreDisplay.setText("7");
            }
        });

        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedScoreDisplay.setText("8");
            }
        });

        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedScoreDisplay.setText("9");
            }
        });
        ten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedScoreDisplay.setText("10");
            }
        });


        closePopup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epicDialog2.dismiss();
            }
        });



        int sss;

        if (res1.getCount() == 0){
            sss = res1.getInt(8);


            editor.putString("score", String.valueOf(sss));
            editor.putInt("original_prediction", sss);
            editor.apply();

            tvScoreDisplay.setText(String.valueOf(sss));

            //scoreView = (TextView) findViewById(R.id.score);


            //int thefeedbackscore = res.getInt(8);

        }
        else{
        while (res.moveToNext()) {


             sss = res.getInt(8);


            editor.putString("score", String.valueOf(sss));
            editor.putInt("original_prediction", sss);
            editor.apply();

            tvScoreDisplay.setText(String.valueOf(sss));

            //scoreView = (TextView) findViewById(R.id.score);


            //int thefeedbackscore = res.getInt(8);

        }
        }



        final int prediction = preferences.getInt("original_prediction",0);
        scoreView.setText(String.valueOf(prediction));

        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                boolean insertedFB = false;


                Cursor res2 = myDb.getDataToClassify();
                while (res2.moveToNext()) {
                    String data = String.valueOf(res2.getInt(0)) + "," + String.valueOf(res2.getInt(1)) + "," + String.valueOf(res2.getFloat(2)) + "," + String.valueOf(res2.getInt(3)) + "," + String.valueOf(res2.getInt(4)) + "," + String.valueOf(res2.getFloat(5)) + "," + String.valueOf(res2.getFloat(6)) + "," + String.valueOf(res2.getFloat(7));
                    try {
                        Log.v("Writing this: ", data + " " + "to text file");
                        addnewinfo(String.valueOf(prediction), prediction, data);
                        Toast.makeText(MainActivity.this, String.valueOf(prediction), Toast.LENGTH_LONG).show();


                    } catch (Exception e) {
                        //Toast.makeText(MainActivity.this, "Unable to record feedback", Toast.LENGTH_LONG).show();
                        Log.v("newFeedback:", "not written to file");


                    }
                }


                scoreView = (TextView) findViewById(R.id.score);
                scoreView.setTextSize(145);
                scoreView.setText(String.valueOf(prediction));
                insertedFB = true;

                editor.putString("score", String.valueOf(prediction));
                editor.apply();


                denominator = (TextView) findViewById(R.id.denominator);
                denominator.setVisibility(View.VISIBLE);
                moreinfo.setVisibility(View.VISIBLE);

                circle.setVisibility(View.INVISIBLE);
                setProgressColours(prediction);
                waysinfo.setVisibility(View.VISIBLE);
                scoreMsg.setVisibility(View.VISIBLE);
                if (privacySwitch.isChecked()) {
                    //String filteredresponse = dfFilter(choices, choice);
                    //textViewtmp.setText("You chose " + filteredresponse);
                    myDb.insertFeedback(prediction, String.valueOf(prediction));
                    postRequest(findViewById(android.R.id.content));


                } else {
                    //textViewtmp.setText("You chose " + choice);
                    myDb.insertFeedback(prediction, String.valueOf(prediction));
                }


                if(insertedFB){
                    epicDialog1.dismiss();}




                epicDialog1.dismiss();
            }
        });

        btnSaveFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean insertedFB = false;

                    String editTextValue = inputfeedbackscore.getText().toString();
                    editTextValue=selectedScoreDisplay.getText().toString();
                    boolean isEmpty = editTextValue == null || editTextValue.trim().length() == 0;

                    if (isEmpty) {
                        Toast.makeText(getApplicationContext(), "Please input your feedback score range from 1 to 10.", Toast.LENGTH_SHORT).show();

                    } else if (!isNumeric(editTextValue.trim())) {
                        Toast.makeText(getApplicationContext(), "Please input integer numbers only.", Toast.LENGTH_SHORT).show();

                    } else if (!isInteger(editTextValue.trim())) {
                        Toast.makeText(getApplicationContext(), "Please input integer numbers only.", Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(editTextValue.trim()) >= 0 && Integer.parseInt(editTextValue.trim()) <= 10) {

                        //if(isInteger(editTextValue.trim())) {


                        Cursor res2 = myDb.getDataToClassify();
                        while (res2.moveToNext()) {
                            String data = String.valueOf(res2.getInt(0)) + "," + String.valueOf(res2.getInt(1)) + "," + String.valueOf(res2.getFloat(2)) + "," + String.valueOf(res2.getInt(3)) + "," + String.valueOf(res2.getInt(4)) + "," + String.valueOf(res2.getFloat(5)) + "," + String.valueOf(res2.getFloat(6)) + "," + String.valueOf(res2.getFloat(7));
                            try {
                                Log.v("Writing this: ", data + " " + "to text file");
                                addnewinfo(editTextValue.trim(), prediction, data);
                                //Toast.makeText(MainActivity.this, editTextValue.trim(), Toast.LENGTH_LONG).show();


                            } catch (Exception e) {
                                //Toast.makeText(MainActivity.this, "Unable to record feedback", Toast.LENGTH_LONG).show();
                                Log.v("newFeedback:", "not written to file");


                            }
                        }
                        scoreView = (TextView) findViewById(R.id.score);
                        scoreView.setTextSize(145);
                        scoreView.setText(editTextValue.trim());
                        insertedFB = true;

                        editor.putString("score", editTextValue.trim());
                        editor.apply();



                        insertScore(Integer.parseInt(editTextValue.trim()));









                        denominator = (TextView) findViewById(R.id.denominator);
                        denominator.setVisibility(View.VISIBLE);
                        moreinfo.setVisibility(View.VISIBLE);

                        circle.setVisibility(View.INVISIBLE);
                        setProgressColours(prediction);
                        waysinfo.setVisibility(View.VISIBLE);
                        scoreMsg.setVisibility(View.VISIBLE);
                        if (privacySwitch.isChecked()) {
                            //String filteredresponse = dfFilter(choices, choice);
                            //textViewtmp.setText("You chose " + filteredresponse);
                            myDb.insertFeedback(prediction, editTextValue.trim());
                            postRequest(findViewById(android.R.id.content));


                        } else {
                            //textViewtmp.setText("You chose " + choice);
                            myDb.insertFeedback(prediction, editTextValue.trim());
                        }


                    } else {

                        Toast.makeText(MainActivity.this, "Your input does not meet the requirement", Toast.LENGTH_SHORT).show();

                    }

                    if (insertedFB) {

                        //Toast.makeText(MainActivity.this, "Your input does not meet the requirement", Toast.LENGTH_SHORT).show();

                        epicDialog2.dismiss();
                        epicDialog1.dismiss();

                        if (Integer.parseInt(editTextValue.trim()) == 10) {
                            scoreView.setTextSize(120);
                            scoreView.setText("10");
                            scoreView.setTextScaleX(0.85f);
                        }
                        else {
                        scoreView.setText(editTextValue.trim());}

                    }


                }

            });



        btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {








                    boolean insertedFB = false;


                    Cursor res2 = myDb.getDataToClassify();
                    while (res2.moveToNext()) {
                        String data = String.valueOf(res2.getInt(0)) + "," + String.valueOf(res2.getInt(1)) + "," + String.valueOf(res2.getFloat(2)) + "," + String.valueOf(res2.getInt(3)) + "," + String.valueOf(res2.getInt(4)) + "," + String.valueOf(res2.getFloat(5)) + "," + String.valueOf(res2.getFloat(6)) + "," + String.valueOf(res2.getFloat(7));
                        try {
                            Log.v("Writing this: ", data + " " + "to text file");
                            addnewinfo(String.valueOf(prediction), prediction, data);
                            Toast.makeText(MainActivity.this, String.valueOf(prediction), Toast.LENGTH_LONG).show();


                        } catch (Exception e) {
                            //Toast.makeText(MainActivity.this, "Unable to record feedback", Toast.LENGTH_LONG).show();
                            Log.v("newFeedback:", "not written to file");


                        }
                    }


                    scoreView = (TextView) findViewById(R.id.score);
                    scoreView.setTextSize(145);
                    scoreView.setText(String.valueOf(prediction));
                    insertedFB = true;

                    editor.putString("score", String.valueOf(prediction));
                    editor.apply();


                    denominator = (TextView) findViewById(R.id.denominator);
                    denominator.setVisibility(View.VISIBLE);
                    moreinfo.setVisibility(View.VISIBLE);

                    circle.setVisibility(View.INVISIBLE);
                    setProgressColours(prediction);
                    waysinfo.setVisibility(View.VISIBLE);
                    scoreMsg.setVisibility(View.VISIBLE);
                    if (privacySwitch.isChecked()) {
                        //String filteredresponse = dfFilter(choices, choice);
                        //textViewtmp.setText("You chose " + filteredresponse);
                        myDb.insertFeedback(prediction, String.valueOf(prediction));
                        postRequest(findViewById(android.R.id.content));


                    } else {
                        //textViewtmp.setText("You chose " + choice);
                        myDb.insertFeedback(prediction, String.valueOf(prediction));
                    }


                    if(insertedFB){
                    epicDialog1.dismiss();}



                }


            });

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    epicDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    epicDialog2.show();
                    //Toast.makeText(getApplicationContext(),filename+"\nSaved to Downloads", Toast.LENGTH_SHORT).show();

                }
            });



            epicDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            epicDialog1.show();


            editor.putInt("callstime",1);
            editor.putInt("socialtime",1);



            lifeDataUpdate.SaveDataToInitialState();



        editor.apply();


    }

    public static boolean isNumeric(String strNum) {
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }


    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }

    public static String seventythirty() {  //for when you want the user's true feedback c.70% of the time
        Random random = new Random();
        String[] array = new String[10]; // set up String array of 7 "True"s and 3 "False"s
        for (int i = 0; i < 7; i++) {
            array[i] = "True";
        }
        for (int i = 7; i < 10; i++) {
            array[i] = "False";
        }
        int index = random.nextInt(10); //randomly generate an index for the array
        Log.v("returning: ", array[index]);
        return array[index]; //return (randomly) one of the strings in the array; has a 70% chance of returning "True"
    }



    public void viewHistory(View view) {

        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);


    }

    public void insertDummyData() throws FileNotFoundException {
        final Handler handler = new Handler();
        final Context con = this;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {



                myDb.insertData(getRandomNumberInRange(0,80000), getRandomNumberInRange(0,12), getRandomNumberInRange(0,260), getRandomNumberInRange(0,120), getRandomNumberInRange(0,20), getRandomNumberInRange(0,34), getRandomNumberInRange(0,29000), getRandomNumberInRange(0,2600));
                Intent toAlarm = new Intent("ALARM_INTENT");
                LocalBroadcastManager.getInstance(con).sendBroadcast(toAlarm);
                editor.putString("score", "-1"); //this should be in the insertdata method
                editor.apply();
            }
        }, 2000);
    }

    public void moreDetail(View view) { //code for the dialog box that will pop up if users wants a little more detail on where they can improve their scores
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        Cursor res = myDb.getLastLine();
        Cursor fm = myDb.getFeedbackMessage();

        while (res.moveToNext()) {
            String message = "";
            int stepcount = res.getInt(0);


            fm.moveToLast();

               // message+="feedback";
                //message+=fm.getString(0);


         //   message+="Stepcount";
          //  message+=stepcount;
           // message+="\n";
            int calltextcount = res.getInt(1) + res.getInt(3);
            float calltimwt = res.getInt(2);

        //    message+="calltime";
          //  message+=calltimwt;
            //message+="\n";

   //         message+="call and text total";
     //       message+=calltextcount;
       //     message+="\n";

            float socialapptime = res.getFloat(6);

//            message+="social";
  //          message+=socialapptime;
    //        message+="\n";



            if (stepcount < 70000) { //these can be adjusted depending on how low you want activity to be before you tell users to do better
                message += "It doesn’t look like you were that physically active last week. As little as moderate exercise such as a light jog or even a long walk a couple of times a week can greatly improve mental wellbeing. Give it a try! (But not in a thunderstorm). ";
            }
            if (calltextcount < 20 && socialapptime < (float) 10000) {
                if (message.length() > 0) {
                    message += "Also, it ";
                }
                else {
                    message += "It ";
                }
                message += "doesn't look like you were particularly talkative last week. This might be because you do all your socialising in person, and prefer a face-to-face to a phone-to-ear conversation. But not reaching out, or replying to texts and calls, is a potential indicator that a person is not as happy as usual. ";
            }
            if (socialapptime > (float) 25200) {
                message += "You're spending a LOT of time on your social media apps. Studies have shown that if you spend too much time browsing through them, you're more likely to feel depression and social isolation. 30 minutes a day or less is considered relatively healthy (but even that sounds like a lot, right?). ";
            }
            if (message.equals("")) {
                message = "You're actually doing pretty well! You're getting a good amount of exercise, staying in contact with people but not overdoing social media use. If you're still not feeling great, try adding an extra exercise routine, learning a new skill, spending more time with friends, or just treating yourself to a thing or two that you enjoy.";
            }
            dialog.setMessage(message);
            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            dialog.show();
        }
    }

    private String loadAssetTextAsString(Context context, String name) { //used to convert training set file to string (see method below)
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = context.getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ( (str = in.readLine()) != null ) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.v("loadAssetText: ", "Error opening asset " + name);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("loadAssetText: ", "Error closing asset " + name);
                }
            }
        }

        return null;
    }

    public void writeStringAsFile(final String fileContents, String fileName) { //writes the training set string produced by method above to internal storage
        try {
            FileWriter out = new FileWriter(new File(this.getFilesDir(), fileName));
            out.write(fileContents);
            out.close();
            Log.v("written: ", "success");
        } catch (IOException e) {
            Log.d("writeStringAsFile", "Error writing file to internal storage");
        }
    }



    private class task extends AsyncTask<List<JavaClassifier.Instance>, Integer, JavaClassifier> { //also not currently in use but left for illustration as above

        @Override
        protected JavaClassifier doInBackground(List<JavaClassifier.Instance>... lists) {
            List<JavaClassifier.Instance> instances = lists[0];
            Log.v("Does logging work", "in here");
            for (int i = 0; i < instances.size(); i++) {
                instances.get(i).x = JavaClassifier.inputstandardise(instances.get(i).x, mean, stdev);
                //publishProgress((int) (i / (float) instances.size()) * 100);
                if (i%100 == 0) {
                    Log.v("at least we're", " iterating: " + String.valueOf(i));
                }
            }
            Log.v("is this done?", "hopefully");
            JavaClassifier logistic = new JavaClassifier(10);
            logistic.train(instances); // might have to put a second background task here...
            Log.v("is this done too?", "hopefully");
            return logistic;
        }

        @Override
        protected void onPostExecute(JavaClassifier c) {
            super.onPostExecute(c);
            double[] result = c.classify(x);
            score = JavaClassifier.score(result);
            Log.v("Score is: ", String.valueOf(score));
             // to insert the new week's score after new week's data is inserted, we have to update the score value in the new line of the db
            int week = (int) myDb.getThisWeekNumber();
            Log.v("week is: ", String.valueOf(week));
            myDb.insertScore(String.valueOf(week), score);
            editor.putString("score", String.valueOf(score));
            editor.apply();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showFeedBack();
                }
            }, 5000);
        }
    }



    private void insertScore(int score){
        int week = (int) myDb.getThisWeekNumber();
        myDb.insertScore(String.valueOf(week), score);

    }
    public int addnewinfo(String feedback, int score, String data) throws Exception{
        int newscore = 0;
        try {
            String filepath = this.getFilesDir() + "/" + "welldata_java_vsn_2.csv";
            FileWriter fw = new FileWriter(filepath, true);
            BufferedWriter bw = new BufferedWriter(fw);

            newscore=Integer.parseInt(feedback.trim());

            bw.write("\r\n"+ String.valueOf(newscore) + "," + data);


            SharedPreferences.Editor editor = getSharedPreferences("error_rate", MODE_PRIVATE).edit();

            editor.putInt("feedback", newscore);
            scoreView.setText(newscore);

            Toast.makeText(MainActivity.this, "Feedback score - "+newscore+" is inserted!", Toast.LENGTH_SHORT).show();


            editor.apply();

            bw.close();

        } catch (IOException e){


            Log.v("fileWriter:", "could not append new line");
        }
        return newscore;
    }

    public void setProgressColours(int score) {
        scoreView.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        if (score == 10) {
            scoreView.setTextSize(120);
            scoreView.setText("10");
            scoreView.setTextScaleX(0.85f);
        }
        scoreView.setText(String.valueOf(score));
        progress.setVisibility(View.VISIBLE);
        progress2.setVisibility(View.VISIBLE);
        progress2.setProgress(100);
        progress.setProgress(score*10);
        if (score == 1 || score == 2) {
            progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorNHSDarkBlue)));
            progress2.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorNHSDarkBlueTint)));
            scoreView.setTextColor(ContextCompat.getColor(this, R.color.colorNHSDarkBlue));
        }
        if (score == 3 || score == 4) {
            progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryDark)));
            progress2.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryDarkTint)));
            scoreView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        if (score == 5 || score == 6) {
            progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorNHSOrange)));
            progress2.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorNHSOrangeTint)));
            scoreView.setTextColor(ContextCompat.getColor(this, R.color.colorNHSOrange));
        }
        if (score == 7 || score == 8) {
            progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorNHSGreen)));
            progress2.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorNHSGreenTint)));
            scoreView.setTextColor(ContextCompat.getColor(this, R.color.colorNHSGreen));
        }
        if (score == 9 || score == 10) {
            progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGreen)));
            progress2.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGreenTint)));
            scoreView.setTextColor(ContextCompat.getColor(this, R.color.colorLightGreen));
        }
    }

    public void waysDetail(View view) { //code for the dialog box that will pop up if users want a reminder of what the 5 ways are
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        String message = "The 4 Ways to Wellbeing are a set of steps that we can all take to improve our mental wellbeing. They are as follows: \n\nConnect - with people around you, including friends and family \nBe Active - take a walk, go cycling, go to the gym, etc... \nKeep Learning - for example, pick up a hobby or look up a recipe  \nBe Mindful - appreciate your experiences and the world around you, and be more aware of your thoughts and feelings \n\nYour weekly score rates the extent to which you have followed these steps during the week, based on your phone usage. \n\n This app tracks the following: steps taken per week, outgoing calls and texts per week, time spent in cameras, quantity of photos and videos taken per week, and time spent in calls, using browsers, and on social media apps.";
        dialog.setMessage(message);
            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            dialog.show();
    }

    public void privacyDetail(View view) { //code for the dialog box that will pop up if users want to know what the switch does
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        String message = "If Anonymous data sharing is enabled, your wellbeing score and error rate will be sent to the NHS nationwide database. Note that no other information will leave your phone. In addition to this, wellbeing score is statistically deniable at an individual level as we randomize it to 70% accuracy - in other words, it will be completely anonymous!";
        dialog.setMessage(message);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.show();
    }

    public void postRequest(View view){

        try {

            //Toast.makeText(MainActivity.this, "score and feedback shared anonymously to database",Toast.LENGTH_LONG).show();


            String MY_PREFS_NAME = new String("sharepostcode");
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            String postcode = prefs.getString("postcode", "");//"No name defined" is the default value.
            int score = Integer.parseInt(preferences.getString("score", ""));
            score=preferences.getInt("original_prediction",0);
            //int errorRate = Integer.parseInt(preferences.getString("error-rate", "3"));;
            SharedPreferences prefserr = getSharedPreferences("error_rate", MODE_PRIVATE);
            int feedback = prefserr.getInt("feedback",0);
            int errorRate = Math.abs(score - feedback) / score * 100;
            Toast.makeText(MainActivity.this, "Data is being sent", Toast.LENGTH_LONG).show();
            //Starting thread to send postRequest
            Log.d("1","Data to be sent: " + postcode + " " + score + " " + errorRate);
            //PostRequest prequest = new PostRequest(postcode,score,errorRate);
            PostRequest prequest = new PostRequest(postcode,feedback,errorRate);
            new Thread(prequest).start();
//            else{
//                Toast.makeText(MainActivity.this, "Anonymous data could not be sent, please indicate your postcode", Toast.LENGTH_LONG).show();
//            }
        }

        catch(Exception e){
            Log.d(TAG, "postRequest:" + e.getMessage());
        }
    }



}
