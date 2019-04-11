package com.NHS.UCLTeam9.WellWellWell;

import android.Manifest;
import android.app.Dialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.Math.abs;

public class ShareActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    Button createPdf;
    Button creattxt;
    Dialog epicDialog;
    Dialog epicDialogtxt;

    String postcode;
    String emis;
    String nhs;

    TextView titleTv,thefilename;
    TextView latestScore;
    ImageView closePopup, pdfimg;

    SharedPreferences preferences;


    static LineChart chart;
    LineDataSet set;
    LineData lineData;
    Button btnSharePDF;
    Button btnSharetxt;
    Button btnSaveLocal;

    private static final int STORAGE_CODE = 1000;
    final private String MY_PREFS_NAME = new String("sharepostcode");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Share Score");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        preferences = this.getSharedPreferences("MyPreferences", this.MODE_PRIVATE);
        //editor = preferences.edit();



        chart = findViewById(R.id.chart1);
        chart.setVisibility(View.INVISIBLE);


        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("text", null);

            postcode = prefs.getString("postcode", "Not given");//"No name defined" is the default value.
            emis = prefs.getString("reference1", "Not given");//"No name defined" is the default value.
            nhs = prefs.getString("reference2", "Not given");//"No name defined" is the default value.






        myDb = new DatabaseHelper(this);
        //classifier = new TensorFlowClassifier(this);
        latestScore = (TextView) findViewById(R.id.latestScoreToShare);



        if (myDb.isdbempty()){
            latestScore.setText("No Score Yet");
        }
        else
        {
            Cursor res=myDb.getLastLine();
            while(res.moveToNext()) {

                latestScore.setText(String.valueOf(res.getInt(8)));

            }
        }

       // res.getInt(8)


        //Toast.makeText(this,String.valueOf(scoreT.getInt(0)), Toast.LENGTH_SHORT).show();



        createPdf = findViewById(R.id.btnSharePDF);
        creattxt = findViewById(R.id.btnSharetxt);







        createPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if permission was granted
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //requestPermissions(permissions, STORAGE_CODE);

                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getApplicationContext(), "Enabling permission to export data as PDF document.", Toast.LENGTH_LONG).show();
                    //String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permissions, STORAGE_CODE);
                } else {
                    makePdf();
                }
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


        creattxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //BASICALLY CHECKING FOR PERMISSION ISSUED OR NOT DUE TO VARIOUS OS SYSTEMS
                if (!myDb.isdbempty()) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                            //no permission granted so need to request it
                            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permissions, STORAGE_CODE);
                        } else {
                            //permission granted
                            //if(!myDb.noFeedback()){
                                ShowCreatetxtPopup();

                            //}else Toast.makeText(getApplicationContext(),"No feedback given yet! Please share your feedback.", Toast.LENGTH_LONG).show();

                        }

                    } else {
                        //if(!myDb.noFeedback()){
                            ShowCreatetxtPopup();

                        //}else Toast.makeText(getApplicationContext(),"No feedback given yet! Please share your feedback.", Toast.LENGTH_LONG).show();

                    }
                }else{
                    ShowCreatetxtPopup();
                    //Toast.makeText(getApplicationContext(),"No weekly scores to share yet! Please wait.", Toast.LENGTH_LONG).show();
                }

            }
        });

        epicDialog=new Dialog(this);
        epicDialogtxt=new Dialog(this);


    }

    @Override
    public void onResume(){
        super.onResume();
        myDb = new DatabaseHelper(this);
        //latestScore = (TextView) findViewById(R.id.latestScoreToShare);

        Cursor res = myDb.getLastLine();


        if (myDb.isdbempty()){
            latestScore.setText("No Score Yet");
        }
        else
        {
            while(res.moveToNext()) {


                //latestScore.setText( String.valueOf(scoreT.getInt(8)));
                latestScore.setText(String.valueOf(res.getInt(8)));

            }

        }



    }

    //TODO
    private void maketxt(){

        if (!myDb.isdbempty()) {


            Cursor score = myDb.getScore();

            score.moveToLast();


            int predictedScore = preferences.getInt("original_prediction", 0);

            int thefeedback = 0;
            Cursor res=myDb.getLastLine();
            while(res.moveToNext()) {

                thefeedback=res.getInt(8);

            }



            try {

                String h = DateFormat.format("MM-dd-yyyyy-h-mmssaa", System.currentTimeMillis()).toString();


                File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(), "txtNotes");

                if (!root.exists()) {

                    root.mkdirs(); // this will create folder.

                }

                File filepath = new File(root, h + ".txt");  // file path to save

                FileWriter writer = new FileWriter(filepath);

                writer.append("User Data:\n");
                writer.append("Reference 1:" + emis + "\n");
                writer.append("Reference 2:" + nhs + "\n");
                writer.append("Postcode:" + postcode + "\n");


                writer.append("Week Number:" + String.valueOf(myDb.getThisWeekNumber()) + "\n");
                writer.append("Predicted Score:"+predictedScore);

                writer.append("Feedback Score:" + thefeedback + "\n");

                //writer.append("Feedback Score:"+feedbackScore+"\n");
                writer.append("Error rate:" + String.valueOf(abs(predictedScore - thefeedback) * 100 / predictedScore + "%\n"));


                writer.flush();

                writer.close();
                Uri Path = FileProvider.getUriForFile(this, "com.NHS.UCLTeam9.WellWellWell.provider", new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "txtNotes/" + h + ".txt"));

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Path);
                shareIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                shareIntent.putExtra(Intent.EXTRA_CC, CC);


                shareIntent.setType("application/txt");
                //shareIntent.setPackage("com.whatsapp");
                startActivity(Intent.createChooser(shareIntent, "share"));


            } catch (IOException e) {

                e.printStackTrace();

            }
        }else{

            try {

                String h = DateFormat.format("MM-dd-yyyyy-h-mmssaa", System.currentTimeMillis()).toString();


                File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(), "txtNotes");

                if (!root.exists()) {

                    root.mkdirs(); // this will create folder.

                }

                File filepath = new File(root, h + ".txt");  // file path to save

                FileWriter writer = new FileWriter(filepath);

                writer.append("User Data:\n");
                writer.append("Reference 1:" + emis + "\n");
                writer.append("Reference 2:" + nhs + "\n");
                writer.append("Postcode:" + postcode + "\n");


                writer.append("Week Number:" +String.valueOf(0));
                writer.append("Feedback Score:" + "Not Scores Recorded!");

                writer.append("Error rate:" + String.valueOf(0));


                writer.flush();

                writer.close();
                Uri Path = FileProvider.getUriForFile(this, "com.NHS.UCLTeam9.WellWellWell.provider", new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "txtNotes/" + h + ".txt"));

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Path);
                shareIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                shareIntent.putExtra(Intent.EXTRA_CC, CC);


                shareIntent.setType("application/txt");
                //shareIntent.setPackage("com.whatsapp");
                startActivity(Intent.createChooser(shareIntent, "share"));


            } catch (IOException e) {

                e.printStackTrace();

            }





        }
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
            Toast.makeText(ShareActivity.this, "Please enable usage stats to use this app!", Toast.LENGTH_LONG).show();
        }
        else if (hasSMSPermission != PackageManager.PERMISSION_GRANTED || hasCallLogPermission != PackageManager.PERMISSION_GRANTED || hasFilesPermission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ShareActivity.this, "Please enable all permissions in order to use this app!", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(ShareActivity.this, new String[] {Manifest.permission.READ_SMS, Manifest.permission.READ_CALL_LOG, Manifest.permission.RECEIVE_SMS, Manifest.permission.PACKAGE_USAGE_STATS, Manifest.permission.READ_EXTERNAL_STORAGE},
                    123);


        }
        else {

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }



    private Bitmap makeChart(){

        List<String> scorelist = new ArrayList<>();
        Cursor res = myDb.getScore();
        while (res.moveToNext()) {
            int s = res.getInt(0);
            if (String.valueOf(s) != null) {
                scorelist.add(String.valueOf(s));
            }
        }
        String[] scores = new String[scorelist.size()];
        for (int i = scores.length-1; i >= 0;  i--) {
            scores[i] = "Week " + String.valueOf(i+1) + ": " + scorelist.get(i); //can add extra predictions or extra graphs
        }
        List<Entry> entries = new ArrayList<Entry>();
        for (int i = 0; i < scores.length; i++) {
            entries.add(new Entry((float) (i+1), Float.parseFloat(scorelist.get(i))));
        }
        set = new LineDataSet(entries,"Overall Weekly Score");
        //set.setColor... .setValueTextColor
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setValueTextSize(10);
        set.setLineWidth(4);

        List<ILineDataSet> list = new ArrayList<ILineDataSet>();
        list.add(set);
        Log.v("Size =", String.valueOf(set.getValues().size()));
        lineData = new LineData(list);
        if (lineData == null || chart == null) {
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
        return chart.getChartBitmap();

    }




    private void makePdf() {

        //CREATE POP UP MADE NOW NEED NO FILE TO CREATE POPUP
        Cursor res;
        if(!myDb.isdbempty()) {




            int thefeedback = 0;
            res=myDb.getLastLine();
            while(res.moveToNext()) {

                thefeedback=res.getInt(8);

            }
            TemplatePDF templatePDF = new TemplatePDF(getApplicationContext());
            templatePDF.openDocument();


            int predictedScore = preferences.getInt("original_prediction", 0);



            String timeStamp = new SimpleDateFormat("dd:MM:yyyy").format(Calendar.getInstance().getTime());
            templatePDF.addEmphasizedSubject("Date:", timeStamp);
            templatePDF.addEmphasizedSubject("Reference 1:",emis);
            templatePDF.addEmphasizedSubject("Reference 2:",nhs);
            templatePDF.addEmphasizedSubject("Postcode:",postcode);
            templatePDF.addEmphasizedSubject("Week Number:",String.valueOf(myDb.getThisWeekNumber()));
            //templatePDF.addEmphasizedSubject("Predicted Score:",String.valueOf(res.getInt(8)));
            templatePDF.addEmphasizedSubject("Predicted Score:",String.valueOf(predictedScore));




            //templatePDF.addEmphasizedSubject("Feedback Message:",fm.getString(0));


            //int feedbackScore = feedbackCounter(fm.getString(0),res.getInt(8));
            //int feedbackScore = Integer.parseInt(fm.getString(0));


            templatePDF.addEmphasizedSubject("Feedback:",String.valueOf(thefeedback));
            //templatePDF.addEmphasizedSubject("Error Rate:", String.valueOf(abs(score.getInt(0)- feedbackScore)*100/score.getInt(0) + "%"));
            templatePDF.addEmphasizedSubject("Error Rate:", String.valueOf(abs(predictedScore- thefeedback)*100/predictedScore + "%"));


            templatePDF.addSubTitle("Feedback Score History Chart:");
            templatePDF.addImage(makeChart());
            templatePDF.closeDocu();
            ShowCreatePDFPopup(templatePDF.getFileName());

        }
        else{
            TemplatePDF templatePDF = new TemplatePDF(getApplicationContext());
            templatePDF.openDocument();
            String timeStamp = new SimpleDateFormat("dd:MM:yyyy").format(Calendar.getInstance().getTime());
            templatePDF.addEmphasizedSubject("Date:", timeStamp);
            templatePDF.addEmphasizedSubject("Reference 1:",emis);
            templatePDF.addEmphasizedSubject("Reference 2:",nhs);
            templatePDF.addEmphasizedSubject("Postcode:",postcode);
            templatePDF.addEmphasizedSubject("Week Number:",String.valueOf(0));
            templatePDF.addEmphasizedSubject("Score:",String.valueOf("Not Scores Recorded!"));
            templatePDF.addEmphasizedSubject("Feedback Message:", "N/A");
            templatePDF.addEmphasizedSubject("Error Rate:", String.valueOf(0));
            templatePDF.addSubTitle("Score History Chart:");
            templatePDF.addEmphasizedSubject("", "Will be available after prediction of first score");
            templatePDF.closeDocu();
            ShowCreatePDFPopup(templatePDF.getFileName());
        }

    }


    String[] TO = {"your_guardian@gmail.com"};
    String[] CC = {"next_of_kin@gmail.com"};

    private void ShowCreatePDFPopup(final String filename) {
        epicDialog.setContentView(R.layout.epic_popup_pdf_create);

        closePopup = (ImageView) epicDialog.findViewById(R.id.closePopup);

        btnSharePDF = (Button) epicDialog.findViewById(R.id.sharePDF);
        btnSaveLocal = (Button) epicDialog.findViewById(R.id.saveToLocal);
        thefilename = (TextView) epicDialog.findViewById(R.id.fileAddress);
        pdfimg = (ImageView)epicDialog.findViewById(R.id.pdfimg);

        thefilename.setText(filename);
        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epicDialog.dismiss();

            }
        });

        pdfimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPdf();
            }
        });

        btnSharePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePdf(filename);
            }
        });

        btnSaveLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),filename+"\nSaved to Downloads", Toast.LENGTH_SHORT).show();

            }
        });



        epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        epicDialog.show();
    }

    private void ShowCreatetxtPopup() {
        epicDialogtxt.setContentView(R.layout.epic_popup_txt_create);

        closePopup = (ImageView) epicDialogtxt.findViewById(R.id.closePopup);

        btnSharetxt = (Button) epicDialogtxt.findViewById(R.id.sharetxt);
        btnSaveLocal = (Button) epicDialogtxt.findViewById(R.id.savetxtToLocal);
        //thefilename = (TextView) epicDialogtxt.findViewById(R.id.fileAddress);
        //pdfimg = (ImageView)epicDialogtxt.findViewById(R.id.pdfimg);

        //thefilename.setText(filename);
        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epicDialogtxt.dismiss();

            }
        });



        btnSharetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maketxt();

            }
        });

        btnSaveLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"txt file saved to Downloads", Toast.LENGTH_SHORT).show();

            }
        });



        epicDialogtxt.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        epicDialogtxt.show();
    }

    private void viewPdf(){
        TemplatePDF.viewPdf();
    }
    private void sharePdf(String filename){
        Uri Path = FileProvider.getUriForFile(this,"com.NHS.UCLTeam9.WellWellWell.provider",new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"PDF/"+filename));

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM,Path);
        shareIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        shareIntent.putExtra(Intent.EXTRA_CC, CC);


        shareIntent.setType("application/pdf");
        //shareIntent.setPackage("com.whatsapp");
        startActivity(Intent.createChooser(shareIntent,"share"));

    }



    public void viewMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void viewStats(View view) {
        Intent intent = new Intent(this, LiveSensorsActivity.class);
        startActivity(intent);
    }


    public void viewShare(View view) {
        //Intent intent = new Intent(this, ShareActivity.class);
        //startActivity(intent);
    }
    public void viewHistory(View view) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }
}

