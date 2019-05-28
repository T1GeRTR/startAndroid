package com.tiger.startandroid;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import java.util.Objects;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    CardView cvStartAndroid;
    CardView cvOther;
    ImageView logoStartAndroid;
    ImageView logoOther;
    //Выбран канал Start Android
    public static boolean selectSA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cvStartAndroid = findViewById(R.id.cvStartAndroid);
        cvOther = findViewById(R.id.cvOther);
        cvStartAndroid.setOnClickListener(this);
        cvOther.setOnClickListener(this);
        logoStartAndroid = findViewById(R.id.logoStartAndroid);
        logoOther = findViewById(R.id.logoOther);
    }

    @Override
    public void onClick(View v) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Intent intent = new Intent(MainActivity.this, PlayList.class);
            ActivityOptionsCompat optionsCompat = null;
            // Флаг, указыващий выбранный пункт меню
            // Ниже реализон переход с общим элементом
            if (v.getId() == R.id.cvStartAndroid) {
                selectSA = true;
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, cvStartAndroid, Objects.requireNonNull(ViewCompat.getTransitionName(cvStartAndroid)));
            } else if (v.getId() == R.id.cvOther) {
                selectSA = false;
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, cvOther, Objects.requireNonNull(ViewCompat.getTransitionName(cvOther)));
            }
            if (optionsCompat != null) {
                startActivity(intent, optionsCompat.toBundle());
            } else {
                startActivity(intent);
                finishAfterTransition();
            }
        }
    }
}
