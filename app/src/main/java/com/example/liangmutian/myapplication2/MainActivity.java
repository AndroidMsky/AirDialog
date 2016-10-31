package com.example.liangmutian.myapplication2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    AirDialog airDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }


    public void Onclick(View v) {


        if (airDialog == null)

            airDialog = new AirDialog.Builder(this).
                    setLeftText("gogo").
                    setRightText("nono").
                    setRightOnClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            airDialog.dismiss();
                            Toast.makeText(MainActivity.this, "nono", Toast.LENGTH_SHORT).show();
                        }
                    }).
                    create();

        airDialog.show();

//        new Handler().postDelayed(new Runnable() {
//
//            public void run() {
//
//                airDialog.dismiss();
//            }
//
//        }, 3000);


    }
}
