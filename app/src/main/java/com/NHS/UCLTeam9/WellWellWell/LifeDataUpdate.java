package com.NHS.UCLTeam9.WellWellWell;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import me.everything.providers.android.browser.BrowserProvider;
import me.everything.providers.android.calllog.CallsProvider;
import me.everything.providers.android.media.Image;
import me.everything.providers.android.media.MediaProvider;
import me.everything.providers.android.media.Video;
import me.everything.providers.android.telephony.TelephonyProvider;

public class LifeDataUpdate {

    public static int stepstaken;

    DatabaseHelper myDb;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    TelephonyProvider telephonyProvider;
    BrowserProvider browserProvider;
    CallsProvider callsProvider;


    private static Context context;



    public LifeDataUpdate(Context context){
        this.context= context;




        preferences =  context.getSharedPreferences("TheLiveData", context.MODE_PRIVATE);
        editor = preferences.edit();
        myDb = new DatabaseHelper(context);

    }

    public boolean checkIfFirstLaunchApp(){
        boolean firstrun = false;
        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;


        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;
        // Get saved version code
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);
        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            // This is just a normal run
        } else if (savedVersionCode == DOESNT_EXIST) {
            firstrun=true;
        } else if (currentVersionCode > savedVersionCode) {

        }
        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();

        return firstrun;
    }

    public void SaveDataToInitialState(){
        int initialCallCounts = getCallDuration().size();
        int initialMessageCounts= getTotalMessagaCount();
        float initialCallDuration = getTotalCallDuration();
        int initialStepCount = getTotalStepsCount();
        float initialSocialAppCount = getTotalsocialAppTime();
        float initialCameraTime = getTotalCameraTime();
        float initialBrowserTime = getTotalBrowserAppTime();

        editor.putInt("prevTotalCallCount",initialCallCounts);
        editor.putInt("prevTotalMessageCount",initialMessageCounts);

        editor.putFloat("prevTotalCallsDuration",initialCallDuration);
        editor.putInt("prevTotalStepsCount",initialStepCount);

        editor.putFloat("prevSocialTime",initialSocialAppCount);

        editor.putFloat("prevCameraTime",initialCameraTime);

        editor.putFloat("prevBrowserTim",initialBrowserTime);





        editor.apply();
    }




    public int getCurrentCallsCount() {
        int totalCallCount=0;
        int previousCallCount=0;
        int currentCallCounts=0;


        totalCallCount=getCallDuration().size();

        previousCallCount =preferences.getInt("prevTotalCallCount",0);

        currentCallCounts=totalCallCount-previousCallCount;

        return currentCallCounts;

    }


    public float getSocialAppTime(){

        float totalSocialTime=getTotalsocialAppTime();
        float previousSocialTime =preferences.getFloat("prevSocialTime",0);
        return totalSocialTime-previousSocialTime;


    }

    public float getCameraTime(){

        float totalCameraTime=getTotalCameraTime();
        float previousCameraTime =preferences.getFloat("prevCameraTime",0);
        return totalCameraTime-previousCameraTime;


    }

    public float getBrowserTime(){

        float totalBrowserTim=getTotalBrowserAppTime();
        float previousBrowserTim =preferences.getFloat("prevBrowserTim",0);
        return totalBrowserTim-previousBrowserTim;


    }


    public int getMessageCount(){

        int totalMessageCount=getTotalMessagaCount();
        int previousMessageCount=preferences.getInt("prevTotalMessageCount",0);
        return totalMessageCount-previousMessageCount;


    }


    public float getCallDurationCount(){
        float totalCallDuration= getTotalCallDuration();
        float previousCallDuration=preferences.getFloat("prevTotalCallsDuration",0);
        float currentCallDuration=totalCallDuration-previousCallDuration;
        return currentCallDuration;

    }

    public int getStepsCount(){
        int totalStepsCount= getTotalStepsCount();

        int previousStepsCount=preferences.getInt("prevTotalStepsCount",0);
        return totalStepsCount-previousStepsCount;

    }


    private int getTotalStepsCount(){

        SharedPreferences preferences2 = context.getSharedPreferences("MyPreferences", context.MODE_PRIVATE);


        SharedPreferences prefStep = context.getSharedPreferences("MyPreferences", context.MODE_PRIVATE);;

        //Toast.makeText(context,preferences2.getInt("stepcount",0)+"", Toast.LENGTH_LONG).show();

        //return prefStep.getInt("freshsteps",0);
        return preferences2.getInt("stepcount",0);
    }




    private float getTotalsocialAppTime(){
        float socialapptime = 0;

        //long startedtime = preferences.getLong("alarmtime", 0);
        //Date intervaldate = new Date(startedtime);


        Date intervaldate=(new GregorianCalendar(2019 , Calendar.MARCH, 1,0,0,0)).getTime();

        //long time= );
        Date d = new Date(System.currentTimeMillis());

        Calendar endCal = Calendar.getInstance();
        Calendar beginCal = Calendar.getInstance();
        beginCal.setTime(intervaldate);
        //endCal.setTime(d);
        //beginCal.add((Calendar.MINUTE), -60*24*7); //sets the stats gathering period to start from when the last score was predicted

        final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);// Context.USAGE_STATS_SERVICE);


        final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginCal.getTimeInMillis(), endCal.getTimeInMillis());
        for (UsageStats app : queryUsageStats) {
            if (app.getPackageName() == null || app.getTotalTimeInForeground() == 0) {
                continue;
            }
            if (app.getPackageName().contains("messaging") || app.getPackageName().contains("nstagram") ||app.getPackageName().contains("eixin")|| app.getPackageName().contains(".orca") ||app.getPackageName().contains(".echat")|| app.getPackageName().contains("napchat") || app.getPackageName().contains("hatsapp")|| app.getPackageName().contains("acebook")||app.getPackageName().contains("encent.mm")) {
                socialapptime += ((float) (app.getTotalTimeInForeground() / 1000));

            }
        }


        //Toast.makeText(context,socialapptime+"", Toast.LENGTH_LONG).show();

        return socialapptime;


    }


    private float getTotalBrowserAppTime(){
        float browserapptime = 0;

        //long startedtime = preferences.getLong("alarmtime", 0);
        //Date intervaldate = new Date(startedtime);


        Date intervaldate=(new GregorianCalendar(2019 , Calendar.MARCH, 1,0,0,0)).getTime();

        //long time= );
        Date d = new Date(System.currentTimeMillis());

        Calendar endCal = Calendar.getInstance();
        Calendar beginCal = Calendar.getInstance();
        beginCal.setTime(intervaldate);
        //endCal.setTime(d);
        //beginCal.add((Calendar.MINUTE), -60*24*7); //sets the stats gathering period to start from when the last score was predicted

        final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);// Context.USAGE_STATS_SERVICE);


        final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginCal.getTimeInMillis(), endCal.getTimeInMillis());
        for (UsageStats app : queryUsageStats) {
            if (app.getPackageName() == null || app.getTotalTimeInForeground() == 0) {
                continue;
            }
            if (app.getPackageName().contains("hrome") || app.getPackageName().contains("uicksearchbox") ||app.getPackageName().contains("rowser")) {
                browserapptime += ((float) (app.getTotalTimeInForeground() / 1000));

            }
        }


        //Toast.makeText(context,socialapptime+"", Toast.LENGTH_LONG).show();

        return browserapptime;


    }


    private float getTotalCameraTime(){
        float cameratime = 0;

        //long startedtime = preferences.getLong("alarmtime", 0);
        //Date intervaldate = new Date(startedtime);


        Date intervaldate=(new GregorianCalendar(2019 , Calendar.MARCH, 1,0,0,0)).getTime();

        //long time= );
        Date d = new Date(System.currentTimeMillis());

        Calendar endCal = Calendar.getInstance();
        Calendar beginCal = Calendar.getInstance();
        beginCal.setTime(intervaldate);
        //endCal.setTime(d);
        //beginCal.add((Calendar.MINUTE), -60*24*7); //sets the stats gathering period to start from when the last score was predicted

        final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);// Context.USAGE_STATS_SERVICE);


        final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginCal.getTimeInMillis(), endCal.getTimeInMillis());
        for (UsageStats app : queryUsageStats) {
            if (app.getPackageName() == null || app.getTotalTimeInForeground() == 0) {
                continue;
            }

            if (app.getPackageName().contains("amera") || app.getPackageName().contains("eitu") ) {
                cameratime += ((float) (app.getTotalTimeInForeground() / 1000));

            }
        }


        //Toast.makeText(context,socialapptime+"", Toast.LENGTH_LONG).show();

        return cameratime;


    }





    private List<String> getCallDuration() {
        List<String> calls = new ArrayList<>();
        Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                null, null, null);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        while (managedCursor.moveToNext()) {
            String callDuration = managedCursor.getString(duration);

            calls.add(callDuration);
        }
        managedCursor.close();
        return calls;

    }




    private int getTotalMessagaCount(){
        telephonyProvider = new TelephonyProvider(context);

        return telephonyProvider.getSms(TelephonyProvider.Filter.SENT).getList().size()+telephonyProvider.getMms(TelephonyProvider.Filter.SENT).getList().size();
    }


    private float getTotalCallDuration(){
        float callsduaration=0;

        List<String> calls_duration = getCallDuration();

        for (String duration:calls_duration){
            int durarion_int = Integer.parseInt(duration);
            callsduaration+=durarion_int;
        }

        return callsduaration;

    }

    public float getMedia(){

        MediaProvider mediaProvider= new MediaProvider(context);
        float imagecount=0;
        float videocount=0;
        List<Image> imagelist = mediaProvider.getImages(MediaProvider.Storage.EXTERNAL).getList();
        List<Video> videolist = mediaProvider.getVideos(MediaProvider.Storage.EXTERNAL).getList();
        for (Image image : imagelist) {
            String dateadded = image.toString().split(", ")[3].substring(10);
            Date date = new Date(Long.parseLong(dateadded)*1000);
            Calendar cal = Calendar.getInstance();
            cal.add((Calendar.MINUTE), -60*24*7);
            Date thisweek = cal.getTime();
            if (thisweek.compareTo(date) < 0) {
                imagecount++;
            }
            else {
                continue;
            }

        }

        for (Video video : videolist) {
            String dateadded = video.toString().split(", ")[7].substring(10);
            Date date = new Date(Long.parseLong(dateadded)*1000);
            Calendar cal = Calendar.getInstance();
            cal.add((Calendar.MINUTE), -60*24*7);
            Date thisweek = cal.getTime();
            if (thisweek.compareTo(date) < 0) {
                videocount++;
            }
            else {
                continue;
            }

        }


        return imagecount + videocount;

    }




}
