package com.NHS.UCLTeam9.WellWellWell;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class ViewPDFActivity extends AppCompatActivity {
    private PDFView pdfView;
    private File file;
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
