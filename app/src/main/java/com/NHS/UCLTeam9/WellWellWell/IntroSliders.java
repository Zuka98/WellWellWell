package com.NHS.UCLTeam9.WellWellWell;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class IntroSliders extends AppCompatActivity {

    ViewPager vpager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_welcome_page);
        setTitle("Welcome to WellWellWell!");
        vpager=(ViewPager)findViewById(R.id.vPager);
        vpager.setAdapter(new setViewadapter(getSupportFragmentManager()));


    }

    public class setViewadapter extends FragmentPagerAdapter {
        public setViewadapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            if(position==0) {
                return new SliderAppInfo();
            }

            else if (position==1){
                return  new SliderWellbeingInfo();
            }

            else
                return new SliderPrivacyInfo();

        }


        @Override
        public int getCount() {
            return 3;
        }
    }

    public void permissionsscreen(View view){
        Intent intent = new Intent(this, PermissionsActivity.class);
        startActivity(intent);
    }

    public void privacyInfo(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        String message = "It is OK. You are in control. You can delete the app. Or you can set it up that no data is shared with your health and well-being provider can be attributed to you. You can do this by clicking on the 'Turn on Privacy Filter' button.";
        dialog.setMessage(message);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.show();
    }

    public void waysDetail(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        String message = "The 5 Ways to Wellbeing are a set of steps that we can all take to improve our mental wellbeing. They are as follows: \n\nConnect - with people around you, including friends and family \nBe Active - take a walk, go cycling, go to the gym, etc... \nKeep Learning - for example, pick up a hobby or look up a recipe \nGive To Others - anything from a small act of kindness like a smile, to something like volunteering \nBe Mindful - appreciate your experiences and the world around you, and be more aware of your thoughts and feelings \n\nYour weekly score rates the extent to which you have followed these steps during the week, based on your phone usage.";
        dialog.setMessage(message);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.show();
    }
}

