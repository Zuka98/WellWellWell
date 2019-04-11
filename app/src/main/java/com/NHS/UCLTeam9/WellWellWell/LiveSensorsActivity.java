package com.NHS.UCLTeam9.WellWellWell;

import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Math.abs;


public class LiveSensorsActivity extends AppCompatActivity {
    public static TextView stepview;
    public static TextView messageview;
    public static TextView callminuteview;
    public static TextView callview;
    public static TextView socialview;
    public static TextView broserview;
    public static TextView cameraview;

    public static int stepstaken;



    DatabaseHelper myDb;
    LifeDataUpdate lifeDataUpdate;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {



        setTitle("Live Statistics");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livesensors);






        preferences = this.getSharedPreferences("MyPreferences", this.MODE_PRIVATE);
        editor = preferences.edit();
        myDb = new DatabaseHelper(this);

        stepview = (TextView) findViewById(R.id.steptext);
        messageview = (TextView) findViewById(R.id.msgcount);
        callview = (TextView) findViewById(R.id.callcount);
        socialview = (TextView) findViewById(R.id.socialView);
        broserview = (TextView) findViewById(R.id.browserView);
        cameraview = (TextView) findViewById(R.id.cameraView);

        callminuteview = (TextView) findViewById(R.id.minuteView);

        lifeDataUpdate=new LifeDataUpdate(this);

        if(lifeDataUpdate.checkIfFirstLaunchApp()){
            lifeDataUpdate.SaveDataToInitialState();
        }

        int callcount= lifeDataUpdate.getCurrentCallsCount();
        if (callcount<0){
            callcount=0;
        }

        int messagecount= lifeDataUpdate.getMessageCount();
        if (messagecount<0){
            messagecount=0;
        }

        int STEPcount= lifeDataUpdate.getStepsCount();
        if (STEPcount<0){
            STEPcount=0;
        }

        float socialcount= lifeDataUpdate.getSocialAppTime();
        if (socialcount<0){
            socialcount=0;
        }

        float cameracount= lifeDataUpdate.getCameraTime();
        if (cameracount<0){
            cameracount=0;
        }

        float callduration = lifeDataUpdate.getCallDurationCount();
        if (callduration<0){
            callduration=0;
        }

        float browserduration = lifeDataUpdate.getBrowserTime();
        if (browserduration<0){
            browserduration=0;
        }


        callview.setText(abs(callcount)+"");
        messageview.setText(abs(messagecount)+"");
        callminuteview.setText(String.format("%.2f", (abs(callduration/60))) + "");
        socialview.setText(String.format("%.2f", (abs(socialcount/60))) + "");
        broserview.setText(String.format("%.2f", (abs(browserduration/60))) + "");
        cameraview.setText(String.format("%.2f", (abs(cameracount/60))) + "");

        //int f=lifeDataUpdate.getStepsCount();
        stepview.setText(Integer.toString(abs(STEPcount)));


    }

    public void setUp(View view){

        float callduration = lifeDataUpdate.getCallDurationCount();
        if (callduration<0){
            callduration=0;
        }

        preferences = this.getSharedPreferences("MyPreferences", this.MODE_PRIVATE);
        editor = preferences.edit();
        callview.setText(abs(lifeDataUpdate.getCurrentCallsCount())+"");
        messageview.setText(abs(lifeDataUpdate.getMessageCount())+"");
        callminuteview.setText(String.format("%.2f", (abs(callduration/60))) + "");
        socialview.setText(String.format("%.2f", (abs(lifeDataUpdate.getSocialAppTime()/60))) + "");
        broserview.setText(String.format("%.2f", (abs(lifeDataUpdate.getBrowserTime()/60))) + "");
        cameraview.setText(String.format("%.2f", (abs(lifeDataUpdate.getCameraTime()/60))) + "");
        Toast.makeText(this,"Data Updated", Toast.LENGTH_SHORT).show();

        int f=abs(lifeDataUpdate.getStepsCount());
        stepview.setText(Integer.toString(f));
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
            Intent intent = new Intent(this, ShareActivity.class);
            startActivity(intent);

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

    public void viewMain(View view) { //takes the user back to the homepage
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void viewHistory(View view) { //takes the user to the score history page
        MainActivity main = new MainActivity();
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }


}
