package com.iszumi.movielover;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.iszumi.movielover.activity.MainActivity;

public class Splashscreen extends AppCompatActivity {

    //Set waktu lama splashscreen
    private static int splashInterval = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splashscreen);
        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                Intent a = new Intent(Splashscreen.this, MainActivity.class);
                startActivity(a); // menghubungkan activity splashscren ke main activity dengan intent

                //jeda selesai Splashscreen
                this.finish();
            }

            private void finish() {

            }
        }, splashInterval);

    };

}