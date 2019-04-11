package com.NHS.UCLTeam9.WellWellWell;

import android.content.Intent;
import android.database.Cursor;
import android.provider.Settings;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    List<String> scorelist;
    static LineChart chart;

    static BarChart stepChart;
    static BarChart usageChart;

    static LineChart chart_8;
    static BarChart stepChart_8;
    static BarChart usageChart_8;

    static LineChart chart_4;
    static BarChart stepChart_4;
    static BarChart usageChart_4;
    LineDataSet set;
    LineDataSet set_4;
    LineDataSet set_8;

    private final static int SCORE_CLICKED =0;
    private final static int STEP_CLICKED =1;
    private final static int SOCIAL_CLICKED =2;

    private  int clicked=0;



    LineData lineData;

    LineData lineData_4;
    LineData lineData_8;

    static int counter;

    static Button scoreButton;
    static Button usageButton;
    static Button stepButton;

    static Button past4week;
    static Button past8week;
    static Button pastyear;

    static TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scorehistory);

        //1 is per year
        chart = findViewById(R.id.chart1);
        stepChart = findViewById(R.id.chart2);
        usageChart = findViewById(R.id.chart3);

        //_1 is per 8 weeks
        chart_8 = findViewById(R.id.chart1_1);
        stepChart_8 = findViewById(R.id.chart2_1);
        usageChart_8 = findViewById(R.id.chart3_1);

        //_2 is per 4 weeks
        chart_4 = findViewById(R.id.chart1_2);
        stepChart_4 = findViewById(R.id.chart2_2);
        usageChart_4 = findViewById(R.id.chart3_2);



        stepChart.setVisibility(View.INVISIBLE);
        usageChart.setVisibility(View.INVISIBLE);


        //should be visible
        chart.setVisibility(View.VISIBLE);



        chart_8.setVisibility(View.INVISIBLE);
        usageChart_8.setVisibility(View.INVISIBLE);
        stepChart_8.setVisibility(View.INVISIBLE);

        chart_4.setVisibility(View.INVISIBLE);
        usageChart_4.setVisibility(View.INVISIBLE);
        stepChart_4.setVisibility(View.INVISIBLE);



        scoreButton = findViewById(R.id.scoregraphbutton);
        usageButton = findViewById(R.id.usagegraphbutton);
        stepButton = findViewById(R.id.stepgraphbutton);

        past4week =findViewById(R.id.past4week);
        past8week =findViewById(R.id.past8week);
        pastyear=findViewById(R.id.past1year);



        title = findViewById(R.id.graphTitle);
        title.setText("Your Calculated Scores So Far");

        //scoreButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
        //pastyear.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorDarkGreenTint, null));

        scoreButton.setBackgroundResource(R.drawable.button_blue_round);
        pastyear.setBackgroundResource(R.drawable.button_deep_green);



        myDb = new DatabaseHelper(this);
        if (myDb.isdbempty()) {
            return;
        }

        //----- Creating line chart of user's scores over time -----------
        List<String> scorelist = new ArrayList<>();

        List<String> scorelist_4 = new ArrayList<>();
        List<String> scorelist_8 = new ArrayList<>();


        Cursor res = myDb.getScore();
        int total =res.getCount();
        int count=0;
        while (res.moveToNext()) {

            int s = res.getInt(0);
            if (String.valueOf(s) != null) {
                scorelist.add(String.valueOf(s));

                if(count>total-5){
                    scorelist_4.add(String.valueOf(s));
                }
                if(count>total-9){
                    scorelist_8.add(String.valueOf(s));
                }
            }
            count +=1;

        }


        String[] scores = new String[scorelist.size()];
        String[] scores_4week = new String[scorelist_4.size()];
        String[] scores_8week = new String[scorelist_8.size()];




        for (int i = scores.length-1; i >= 0;  i--) {
            scores[i] = "Week " + String.valueOf(i+1) + ": " + scorelist.get(i); //can add extra predictions or extra graphs
        }

        for (int i = scores_4week.length-1; i >= 0;  i--) {
            scores_4week[i] = "Week " + String.valueOf(i+1) + ": " + scorelist_4.get(i); //can add extra predictions or extra graphs
        }
        for (int i = scores_8week.length-1; i >= 0;  i--) {
            scores_8week[i] = "Week " + String.valueOf(i+1) + ": " + scorelist_8.get(i); //can add extra predictions or extra graphs
        }


        List<Entry> entries = new ArrayList<Entry>();
        for (int i = 0; i < scores.length; i++) {
            entries.add(new Entry((float) (i+1), Float.parseFloat(scorelist.get(i))));
        }


        List<Entry> entries_4weekscore = new ArrayList<Entry>();
        for (int i = 0; i < scores_4week.length; i++) {
            entries_4weekscore.add(new Entry((float) (i+1), Float.parseFloat(scorelist_4.get(i))));
        }

        List<Entry> entries_8weekscore = new ArrayList<Entry>();
        for (int i = 0; i < scores_8week.length; i++) {
            entries_8weekscore.add(new Entry((float) (i+1), Float.parseFloat(scorelist_8.get(i))));
        }



        set = new LineDataSet(entries,"Overall Weekly Score");
        set_4 = new LineDataSet(entries_4weekscore,"Overall Weekly Score");
        set_8 = new LineDataSet(entries_8weekscore,"Overall Weekly Score");

        set_4.setAxisDependency(YAxis.AxisDependency.LEFT);
        set_4.setValueTextSize(10);
        set_4.setLineWidth(4);

        set_8.setAxisDependency(YAxis.AxisDependency.LEFT);
        set_8.setValueTextSize(10);
        set_8.setLineWidth(4);


        //set.setColor... .setValueTextColor
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setValueTextSize(10);
        set.setLineWidth(4);




        List<ILineDataSet> list = new ArrayList<ILineDataSet>();
        list.add(set);

        List<ILineDataSet> list_4 = new ArrayList<ILineDataSet>();
        List<ILineDataSet> list_8 = new ArrayList<ILineDataSet>();

        list_4.add(set_4);
        list_8.add(set_8);




        Log.v("Size =", String.valueOf(set.getValues().size()));


        lineData = new LineData(list);
        if (lineData == null || chart == null) {
            Log.v("It's null on!", "Wow");
        }

        lineData_4 = new LineData(list_4);
        if (lineData_4 == null || chart_4 == null) {
            Log.v("It's null on!", "Wow");
        }
        lineData_8 = new LineData(list_8);
        if (lineData_8 == null || chart_8 == null) {
            Log.v("It's null on!", "Wow");
        }



        Log.v("linedata data", String.valueOf(lineData.getDataSetCount()));

        setTitle("Your weekly stats");


        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaximum(10);
        yAxis.setAxisMinimum(0);
        yAxis.setGranularity(1);
        yAxis.setDrawGridLines(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1);
        if (scorelist.size() < 20) {
            xAxis.setLabelCount(scorelist.size() + 1);
        }
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        Legend legend = chart.getLegend();
        //chart.getDescription().setText("Your welfare scores so far");
        chart.getDescription().setEnabled(false);
        xAxis.setDrawGridLines(false);
        YAxis axis2 = chart.getAxisRight();
        axis2.setEnabled(false);
        chart.setData(lineData);



        YAxis yAxis_4 = chart_4.getAxisLeft();
        yAxis_4.setAxisMaximum(10);
        yAxis_4.setAxisMinimum(0);
        yAxis_4.setGranularity(1);
        yAxis_4.setDrawGridLines(false);
        XAxis xAxis_4 = chart_4.getXAxis();
        xAxis_4.setGranularity(1);
        if (scorelist_4.size() < 20) {
            xAxis.setLabelCount(scorelist_4.size() + 1);
        }
        xAxis_4.setPosition(XAxis.XAxisPosition.BOTTOM);
        Legend legend_4 = chart_4.getLegend();
        //chart.getDescription().setText("Your welfare scores so far");
        chart_4.getDescription().setEnabled(false);
        xAxis_4.setDrawGridLines(false);
        YAxis axis2_4 = chart_4.getAxisRight();
        axis2_4.setEnabled(false);
        chart_4.setData(lineData_4);

        YAxis yAxis_8 = chart_8.getAxisLeft();
        yAxis_8.setAxisMaximum(10);
        yAxis_8.setAxisMinimum(0);
        yAxis_8.setGranularity(1);
        yAxis_8.setDrawGridLines(false);
        XAxis xAxis_8 = chart_8.getXAxis();
        xAxis_8.setGranularity(1);
        if (scorelist_8.size() < 20) {
            xAxis.setLabelCount(scorelist_8.size() + 1);
        }
        xAxis_8.setPosition(XAxis.XAxisPosition.BOTTOM);
        Legend legend_8 = chart_8.getLegend();
        //chart.getDescription().setText("Your welfare scores so far");
        chart_8.getDescription().setEnabled(false);
        xAxis_8.setDrawGridLines(false);
        YAxis axis2_8 = chart_8.getAxisRight();
        axis2_8.setEnabled(false);
        chart_8.setData(lineData_8);
        //---------------------------------------------------------
        //Creating Barcharts of 1) steps taken and 2) time spent on the phone and on social media apps








        List<BarEntry> steplist = new ArrayList<>();
        List<BarEntry> phonelist = new ArrayList<>();
        List<BarEntry> sociallist = new ArrayList<>();


        List<BarEntry> steplist_4 = new ArrayList<>();
        List<BarEntry> phonelist_4 = new ArrayList<>();
        List<BarEntry> sociallist_4 = new ArrayList<>();


        List<BarEntry> steplist_8 = new ArrayList<>();
        List<BarEntry> phonelist_8 = new ArrayList<>();
        List<BarEntry> sociallist_8 = new ArrayList<>();



        counter = 1;
        int count_4 =1;
        int count_8 =1;
        Cursor c = myDb.getAllEntries();
        while (c.moveToNext()) {
            steplist.add(new BarEntry(counter, c.getInt(0)));
            phonelist.add(new BarEntry(counter, (c.getFloat(2)/60)));
            sociallist.add(new BarEntry(counter, (c.getFloat(6)/60)));

            if (counter>total-4){
                steplist_4.add(new BarEntry(count_4, c.getInt(0)));
                phonelist_4.add(new BarEntry(count_4, (c.getFloat(2)/60)));
                sociallist_4.add(new BarEntry(count_4, (c.getFloat(6)/60)));
                count_4+=1;
            }

            if (counter>total-8){
                steplist_8.add(new BarEntry(count_8, c.getInt(0)));
                phonelist_8.add(new BarEntry(count_8, (c.getFloat(2)/60)));
                sociallist_8.add(new BarEntry(count_8, (c.getFloat(6)/60)));
                count_8+=1;
            }
            counter++;
        }





        BarDataSet stepSet = new BarDataSet(steplist, "Steps taken in week");
        BarDataSet phoneSet = new BarDataSet(phonelist, "Time chatting on phone (mins)");
        BarDataSet socialSet = new BarDataSet(sociallist, "Time on social media (mins)");

        BarDataSet stepSet_4 = new BarDataSet(steplist_4, "Steps taken in week");
        BarDataSet phoneSet_4 = new BarDataSet(phonelist_4, "Time chatting on phone (mins)");
        BarDataSet socialSet_4 = new BarDataSet(sociallist_4, "Time on social media (mins)");

        BarDataSet stepSet_8 = new BarDataSet(steplist_8, "Steps taken in week");
        BarDataSet phoneSet_8 = new BarDataSet(phonelist_8, "Time chatting on phone (mins)");
        BarDataSet socialSet_8 = new BarDataSet(sociallist_8, "Time on social media (mins)");




        stepSet.setColor(ResourcesCompat.getColor(getResources(), R.color.colorLightGreen, null));
        //stepSet.setDrawValues(false);
        stepSet_4.setColor(ResourcesCompat.getColor(getResources(), R.color.colorLightGreen, null));
        stepSet_8.setColor(ResourcesCompat.getColor(getResources(), R.color.colorLightGreen, null));


        socialSet.setColor(ResourcesCompat.getColor(getResources(), R.color.steelblue, null));
        socialSet_4.setColor(ResourcesCompat.getColor(getResources(), R.color.steelblue, null));
        socialSet_8.setColor(ResourcesCompat.getColor(getResources(), R.color.steelblue, null));

        phoneSet.setColor(ResourcesCompat.getColor(getResources(), R.color.colorNHSOrange, null));
        phoneSet_4.setColor(ResourcesCompat.getColor(getResources(), R.color.colorNHSOrange, null));
        phoneSet_8.setColor(ResourcesCompat.getColor(getResources(), R.color.colorNHSOrange, null));


        BarData stepData = new BarData(stepSet);
        BarData stepData_4 = new BarData(stepSet_4);
        BarData stepData_8 = new BarData(stepSet_8);

        //stepData.setBarWidth...
        XAxis stepXAxis = stepChart.getXAxis();
        stepXAxis.setGranularity(1);
        stepXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        stepXAxis.setAxisMinimum(0);
        stepXAxis.setDrawGridLines(false);
        if (steplist.size() < 20) {
            stepXAxis.setLabelCount(steplist.size());
        }

        stepXAxis.setLabelCount(steplist.size());

        YAxis stepYAxis = stepChart.getAxisLeft();
        stepYAxis.setAxisMinimum(0);
        stepYAxis.setDrawGridLines(false);
        stepYAxis.setGranularity(20000f);
        stepYAxis.setDrawLabels(true);

        stepChart.setData(stepData);
        stepChart.setFitBars(true);
        stepChart.getDescription().setEnabled(false);
        stepChart.getAxisRight().setEnabled(false);




        XAxis stepXAxis_4 = stepChart_4.getXAxis();
        stepXAxis_4.setGranularity(1);
        stepXAxis_4.setPosition(XAxis.XAxisPosition.BOTTOM);
        stepXAxis_4.setAxisMinimum(0);
        stepXAxis_4.setDrawGridLines(false);
        if (steplist_4.size() < 20) {
            stepXAxis.setLabelCount(steplist_4.size());
        }

        stepXAxis_4.setLabelCount(steplist_4.size());

        YAxis stepYAxis_4 = stepChart_4.getAxisLeft();
        stepYAxis_4.setAxisMinimum(0);
        stepYAxis_4.setDrawGridLines(false);
        stepYAxis_4.setGranularity(20000f);
        stepYAxis_4.setDrawLabels(true);

        stepChart_4.setData(stepData_4);
        stepChart_4.setFitBars(true);
        stepChart_4.getDescription().setEnabled(false);
        stepChart_4.getAxisRight().setEnabled(false);




        

        XAxis stepXAxis_8 = stepChart_8.getXAxis();
        stepXAxis_8.setGranularity(1);
        stepXAxis_8.setPosition(XAxis.XAxisPosition.BOTTOM);
        stepXAxis_8.setAxisMinimum(0);
        stepXAxis_8.setDrawGridLines(false);
        if (steplist_8.size() < 20) {
            stepXAxis.setLabelCount(steplist_8.size());
        }

        stepXAxis_8.setLabelCount(steplist_8.size());

        YAxis stepYAxis_8 = stepChart_8.getAxisLeft();
        stepYAxis_8.setAxisMinimum(0);
        stepYAxis_8.setDrawGridLines(false);
        stepYAxis_8.setGranularity(20000f);
        stepYAxis_8.setDrawLabels(true);

        stepChart_8.setData(stepData_8);
        stepChart_8.setFitBars(true);
        stepChart_8.getDescription().setEnabled(false);
        stepChart_8.getAxisRight().setEnabled(false);












        BarData usageData = new BarData(phoneSet, socialSet);
        BarData usageData_4 = new BarData(phoneSet_4, socialSet_4);
        BarData usageData_8 = new BarData(phoneSet_8, socialSet_8);


        usageData.setBarWidth(0.45f);
        //usageData.setBarWidth...
        XAxis usageXAxis = usageChart.getXAxis();
        YAxis usageYAxis = usageChart.getAxisLeft();
        usageYAxis.setDrawGridLines(false);
        usageXAxis.setGranularityEnabled(true);
        if (sociallist.size() < 20) {
            usageXAxis.setLabelCount(sociallist.size());
        }

        //usageXAxis.setGranularity(1);
        usageXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        usageXAxis.setAxisMinimum(1);
        usageXAxis.setAxisMaximum(sociallist.size() + 1);
        usageXAxis.setXOffset(0.1f);
        usageXAxis.setDrawGridLines(false);
        Legend leg = usageChart.getLegend();
        usageXAxis.setCenterAxisLabels(true);
        usageChart.setData(usageData);
        usageChart.groupBars(1, 0.1f, 0f);
        usageChart.getDescription().setEnabled(false);
        YAxis axis4 = usageChart.getAxisRight();
        axis4.setEnabled(false);



        usageData_4.setBarWidth(0.45f);
        //usageData.setBarWidth...
        XAxis usageXAxis_4 = usageChart_4.getXAxis();
        YAxis usageYAxis_4 = usageChart_4.getAxisLeft();
        usageYAxis_4.setDrawGridLines(false);
        usageXAxis_4.setGranularityEnabled(true);
        if (sociallist_4.size() < 20) {
            usageXAxis.setLabelCount(sociallist_4.size());
        }

        //usageXAxis.setGranularity(1);
        usageXAxis_4.setPosition(XAxis.XAxisPosition.BOTTOM);
        usageXAxis_4.setAxisMinimum(1);
        usageXAxis_4.setAxisMaximum(sociallist_4.size() + 1);
        usageXAxis_4.setXOffset(0.1f);
        usageXAxis_4.setDrawGridLines(false);
        Legend leg_4 = usageChart_4.getLegend();
        usageXAxis_4.setCenterAxisLabels(true);
        usageChart_4.setData(usageData_4);
        usageChart_4.groupBars(1, 0.1f, 0f);
        usageChart_4.getDescription().setEnabled(false);
        YAxis axis4_4 = usageChart_4.getAxisRight();
        axis4_4.setEnabled(false);



        usageData_8.setBarWidth(0.45f);
        //usageData.setBarWidth...
        XAxis usageXAxis_8 = usageChart_8.getXAxis();
        YAxis usageYAxis_8= usageChart_8.getAxisLeft();
        usageYAxis_8.setDrawGridLines(false);
        usageXAxis_8.setGranularityEnabled(true);
        if (sociallist_8.size() < 20) {
            usageXAxis.setLabelCount(sociallist_8.size());
        }

        //usageXAxis.setGranularity(1);
        usageXAxis_8.setPosition(XAxis.XAxisPosition.BOTTOM);
        usageXAxis_8.setAxisMinimum(1);
        usageXAxis_8.setAxisMaximum(sociallist_8.size() + 1);
        usageXAxis_8.setXOffset(0.1f);
        usageXAxis_8.setDrawGridLines(false);
        Legend leg_8 = usageChart_8.getLegend();
        usageXAxis_8.setCenterAxisLabels(true);
        usageChart_8.setData(usageData_8);
        usageChart_8.groupBars(1, 0.1f, 0f);
        usageChart_8.getDescription().setEnabled(false);
        YAxis axis4_8 = usageChart_8.getAxisRight();
        axis4_8.setEnabled(false);

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




    public void viewSteps(View view) { //method for button to view steps per week chart
        clicked=STEP_CLICKED;
        stepChart_4.setVisibility(View.INVISIBLE);
        stepChart_8.setVisibility(View.INVISIBLE);

        usageChart.setVisibility(View.INVISIBLE);
        usageChart_4.setVisibility(View.INVISIBLE);
        usageChart_8.setVisibility(View.INVISIBLE);


        chart.setVisibility(View.INVISIBLE);
        chart_8.setVisibility(View.INVISIBLE);
        chart_4.setVisibility(View.INVISIBLE);

        stepChart.setVisibility(View.VISIBLE);

        //stepButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
        stepButton.setBackgroundResource(R.drawable.button_blue_round);
        usageButton.setBackgroundResource(R.drawable.button_light_blue_round);
        scoreButton.setBackgroundResource(R.drawable.button_light_blue_round);


        past4week.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorLightGreenTint, null));
        past8week.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorLightGreenTint, null));
        pastyear.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorDarkGreenTint, null));


        past4week.setBackgroundResource(R.drawable.button_light_green);
        past8week.setBackgroundResource(R.drawable.button_light_green);
        pastyear.setBackgroundResource(R.drawable.button_deep_green);




        stepButton.setElevation(1);
        usageButton.setElevation(0);
        scoreButton.setElevation(0);
        title.setText("Steps Counts So Far");
    }

    public void viewUsage(View view) { //method for button to view usage stats per week chart
        clicked=SOCIAL_CLICKED;

        stepChart.setVisibility(View.INVISIBLE);
        stepChart_4.setVisibility(View.INVISIBLE);
        stepChart_8.setVisibility(View.INVISIBLE);

        usageChart_4.setVisibility(View.INVISIBLE);
        usageChart_8.setVisibility(View.INVISIBLE);


        chart.setVisibility(View.INVISIBLE);
        chart_8.setVisibility(View.INVISIBLE);
        chart_4.setVisibility(View.INVISIBLE);

        usageChart.setVisibility(View.VISIBLE);



        past4week.setBackgroundResource(R.drawable.button_light_green);
        past8week.setBackgroundResource(R.drawable.button_light_green);
        pastyear.setBackgroundResource(R.drawable.button_deep_green);


        stepButton.setBackgroundResource(R.drawable.button_light_blue_round);
        usageButton.setBackgroundResource(R.drawable.button_blue_round);
        scoreButton.setBackgroundResource(R.drawable.button_light_blue_round);

        stepButton.setElevation(0);
        usageButton.setElevation(1);
        scoreButton.setElevation(0);
        title.setText("Phone Usage So Far");
    }

    public void viewScore(View view) { //method for button to view scores chart
        clicked=SCORE_CLICKED;

        stepChart.setVisibility(View.INVISIBLE);
        stepChart_4.setVisibility(View.INVISIBLE);
        stepChart_8.setVisibility(View.INVISIBLE);

        usageChart.setVisibility(View.INVISIBLE);
        usageChart_4.setVisibility(View.INVISIBLE);
        usageChart_8.setVisibility(View.INVISIBLE);


        chart_8.setVisibility(View.INVISIBLE);
        chart_4.setVisibility(View.INVISIBLE);

        chart.setVisibility(View.VISIBLE);

        past4week.setBackgroundResource(R.drawable.button_light_green);
        past8week.setBackgroundResource(R.drawable.button_light_green);
        pastyear.setBackgroundResource(R.drawable.button_deep_green);



        stepButton.setBackgroundResource(R.drawable.button_light_blue_round);
        usageButton.setBackgroundResource(R.drawable.button_light_blue_round);
        scoreButton.setBackgroundResource(R.drawable.button_blue_round);


        stepButton.setElevation(0);
        usageButton.setElevation(0);
        scoreButton.setElevation(1);
        title.setText("Wellbeing Scores So Far");
    }




    public void view4WeekStats(View view) { //method for button to view scores chart
        stepChart.setVisibility(View.INVISIBLE);
        stepChart_4.setVisibility(View.INVISIBLE);
        stepChart_8.setVisibility(View.INVISIBLE);

        usageChart.setVisibility(View.INVISIBLE);
        usageChart_4.setVisibility(View.INVISIBLE);
        usageChart_8.setVisibility(View.INVISIBLE);


        chart.setVisibility(View.INVISIBLE);
        chart_8.setVisibility(View.INVISIBLE);
        chart_4.setVisibility(View.INVISIBLE);


        past4week.setBackgroundResource(R.drawable.button_deep_green);
        past8week.setBackgroundResource(R.drawable.button_light_green);
        pastyear.setBackgroundResource(R.drawable.button_light_green);







        if(clicked==SCORE_CLICKED) {
            chart_4.setVisibility(View.VISIBLE);

            stepButton.setBackgroundResource(R.drawable.button_light_blue_round);
            usageButton.setBackgroundResource(R.drawable.button_light_blue_round);
            scoreButton.setBackgroundResource(R.drawable.button_blue_round);

            stepButton.setElevation(0);
            usageButton.setElevation(0);
            scoreButton.setElevation(1);

            title.setText("Wellbeing Scores In Last 4 Weeks");
        }

        if(clicked==STEP_CLICKED) {
            stepChart_4.setVisibility(View.VISIBLE);


            stepButton.setBackgroundResource(R.drawable.button_blue_round);
            usageButton.setBackgroundResource(R.drawable.button_light_blue_round);
            scoreButton.setBackgroundResource(R.drawable.button_light_blue_round);

            stepButton.setElevation(1);
            usageButton.setElevation(0);
            scoreButton.setElevation(0);

            title.setText("Step Counts In Last 4 Weeks");
        }

        if(clicked==SOCIAL_CLICKED) {
            usageChart_4.setVisibility(View.VISIBLE);



            stepButton.setBackgroundResource(R.drawable.button_light_blue_round);
            usageButton.setBackgroundResource(R.drawable.button_blue_round);
            scoreButton.setBackgroundResource(R.drawable.button_light_blue_round);

            stepButton.setElevation(0);
            usageButton.setElevation(1);
            scoreButton.setElevation(0);

            title.setText("Phone Usages In Last 4 Weeks");
        }


    }

    public void view8WeekStats(View view) { //method for button to view scores chart
        stepChart.setVisibility(View.INVISIBLE);
        stepChart_4.setVisibility(View.INVISIBLE);
        stepChart_8.setVisibility(View.INVISIBLE);

        usageChart.setVisibility(View.INVISIBLE);
        usageChart_4.setVisibility(View.INVISIBLE);
        usageChart_8.setVisibility(View.INVISIBLE);


        chart.setVisibility(View.INVISIBLE);
        chart_8.setVisibility(View.INVISIBLE);
        chart_4.setVisibility(View.INVISIBLE);


        past4week.setBackgroundResource(R.drawable.button_light_green);
        past8week.setBackgroundResource(R.drawable.button_deep_green);
        pastyear.setBackgroundResource(R.drawable.button_light_green);

        if(clicked==SCORE_CLICKED) {
            chart_8.setVisibility(View.VISIBLE);


            stepButton.setBackgroundResource(R.drawable.button_light_blue_round);
            usageButton.setBackgroundResource(R.drawable.button_light_blue_round);
            scoreButton.setBackgroundResource(R.drawable.button_blue_round);



            stepButton.setElevation(0);
            usageButton.setElevation(0);
            scoreButton.setElevation(1);

            title.setText("Wellbeing Scores In Last 8 Weeks");
        }

        if(clicked==STEP_CLICKED) {
            stepChart_8.setVisibility(View.VISIBLE);




            stepButton.setBackgroundResource(R.drawable.button_blue_round);
            usageButton.setBackgroundResource(R.drawable.button_light_blue_round);
            scoreButton.setBackgroundResource(R.drawable.button_light_blue_round);
            stepButton.setElevation(1);
            usageButton.setElevation(0);
            scoreButton.setElevation(0);

            title.setText("Step Counts In Last 8 Weeks");
        }

        if(clicked==SOCIAL_CLICKED) {
            usageChart_8.setVisibility(View.VISIBLE);



            stepButton.setBackgroundResource(R.drawable.button_light_blue_round);
            usageButton.setBackgroundResource(R.drawable.button_blue_round);
            scoreButton.setBackgroundResource(R.drawable.button_light_blue_round);
            stepButton.setElevation(0);
            usageButton.setElevation(1);
            scoreButton.setElevation(0);

            title.setText("Phone Usages In Last 8 Weeks");
        }



    }

    public void viewallStats(View view) { //method for button to view scores chart
        stepChart.setVisibility(View.INVISIBLE);
        stepChart_4.setVisibility(View.INVISIBLE);
        stepChart_8.setVisibility(View.INVISIBLE);

        usageChart.setVisibility(View.INVISIBLE);
        usageChart_4.setVisibility(View.INVISIBLE);
        usageChart_8.setVisibility(View.INVISIBLE);


        chart.setVisibility(View.INVISIBLE);
        chart_8.setVisibility(View.INVISIBLE);
        chart_4.setVisibility(View.INVISIBLE);



        past4week.setBackgroundResource(R.drawable.button_light_green);
        past8week.setBackgroundResource(R.drawable.button_light_green);
        pastyear.setBackgroundResource(R.drawable.button_deep_green);

        if(clicked==SCORE_CLICKED) {
            chart.setVisibility(View.VISIBLE);

            stepButton.setBackgroundResource(R.drawable.button_light_blue_round);
            usageButton.setBackgroundResource(R.drawable.button_light_blue_round);
            scoreButton.setBackgroundResource(R.drawable.button_blue_round);
            stepButton.setElevation(0);
            usageButton.setElevation(0);
            scoreButton.setElevation(1);

            title.setText("Wellbeing Scores So Far");
        }

        if(clicked==STEP_CLICKED) {
            stepChart.setVisibility(View.VISIBLE);

            stepButton.setBackgroundResource(R.drawable.button_blue_round);
            usageButton.setBackgroundResource(R.drawable.button_light_blue_round);
            scoreButton.setBackgroundResource(R.drawable.button_light_blue_round);
            stepButton.setElevation(1);
            usageButton.setElevation(0);
            scoreButton.setElevation(0);

            title.setText("Step Counts So Far");
        }

        if(clicked==SOCIAL_CLICKED) {
            usageChart.setVisibility(View.VISIBLE);

            stepButton.setBackgroundResource(R.drawable.button_light_blue_round);
            usageButton.setBackgroundResource(R.drawable.button_blue_round);
            scoreButton.setBackgroundResource(R.drawable.button_light_blue_round);
            stepButton.setElevation(0);
            usageButton.setElevation(1);
            scoreButton.setElevation(0);

            title.setText("Phone Usages So Far");
        }



    }


    public void viewMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void viewLive(View view) {
        /*if (!main.isMyServiceRunning(TheService.class)) {
            Toast.makeText(StatisticsPage.this, "Start monitoring in order to view stats!", Toast.LENGTH_LONG).show();
            return;
        }*/
        Intent intent = new Intent(this, LiveSensorsActivity.class);
        //intent.putExtra("history", "liveweek");
        startActivity(intent);
    }




}
