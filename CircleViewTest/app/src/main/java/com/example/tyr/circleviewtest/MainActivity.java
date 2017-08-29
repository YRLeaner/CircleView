package com.example.tyr.circleviewtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private CircleView circleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circleView = (CircleView)findViewById(R.id.cicleview);
        circleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleView.changeAngle(200);
            }
        });

        circleView.setOnAngleColorListener(new CircleView.OnAngleColorListener() {
            @Override
            public void colorListener(int red, int green) {

            }
        });
    }
}
