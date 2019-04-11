package com.NHS.UCLTeam9.WellWellWell;

import android.Manifest;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.util.Calendar;
import java.util.List;

public class ViewPDFActivity extends AppCompatActivity {
    private PDFView pdfView;
    private File file;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("View your PDF");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        pdfView=(PDFView)findViewById(R.id.pdfViewer);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            file=new File(bundle.getString("path",""));

        }

        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .load();
    }



}
