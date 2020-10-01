package kmcilvai.perfectpoet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final ConstraintLayout background = (ConstraintLayout) findViewById(R.id.splashScreen);




        DisplayMetrics metrics;
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int screenHeight = metrics.heightPixels;
        final int screenWidth = metrics.widthPixels;
        pl.droidsonroids.gif.GifImageView gif = (pl.droidsonroids.gif.GifImageView) findViewById(R.id.logogif);
        gif.getLayoutParams().width = (int) (screenHeight * .65);
        gif.getLayoutParams().height = (int) (screenHeight * .65);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) gif.getLayoutParams();
        params.setMargins(0, 0, 0, (int) (screenHeight / 2.2));
        gif.setLayoutParams(params);


        TextView logo = (TextView) findViewById(R.id.logo);
        TextView catchline = (TextView) findViewById(R.id.catchline);
        String pro = getStringFromInternal("lyricprouser", "false");

        if (pro.equals("true0518")) {
            logo.setText("Lyric Pro");
            logo.setTextSize(TypedValue.COMPLEX_UNIT_SP, screenWidth / 16);
        } else {
            logo.setTextSize(TypedValue.COMPLEX_UNIT_SP, screenWidth / 12);
        }
        catchline.setTextSize(TypedValue.COMPLEX_UNIT_SP, screenWidth / 50);

        String colorTheme = getStringFromInternal("lyriccolortheme", "royal");

        if (colorTheme.equals("royal")) {
            background.setBackgroundColor(getResources().getColor(R.color.color14));
            gif.setImageResource(R.drawable.logogif);
        } else if (colorTheme.equals("sunset")) {
            gif.setImageResource(R.drawable.logogiforange);
            background.setBackgroundColor(getResources().getColor(R.color.color4));
        } else if (colorTheme.equals("joy")) {
            background.setBackgroundColor(getResources().getColor(R.color.color6));
            gif.setImageResource(R.drawable.logogifblue);
        } else if (colorTheme.equals("dark")) {
            gif.setImageResource(R.drawable.logogifcolor);
            background.setBackgroundColor(getResources().getColor(R.color.color17));
        }

        String orientationMode = getStringFromInternal("lyricorientation", "NEW");
        final Typeface satisfy = Typeface.createFromAsset(getAssets(), "fonts/Satisfy-Regular.ttf");

        logo.setTypeface(satisfy);
        catchline.setTypeface(satisfy);


        if (orientationMode.equals("LANDSCAPE")) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            gif.setVisibility(View.GONE);
            LinearLayout linearlogo = (LinearLayout) findViewById(R.id.linearlogo);
            LinearLayout linearlogoHorizontal = (LinearLayout) findViewById(R.id.linearlogohorizontal);
            linearlogoHorizontal.setVisibility(View.VISIBLE);
            linearlogo.setVisibility(View.GONE);
            TextView logoHorizontal = (TextView) findViewById(R.id.logohorizontal);
            TextView catchlineHorizontal = (TextView) findViewById(R.id.catchlinehorizontal);
            if (pro.equals("true0518")) {
                logoHorizontal.setText("Lyric Pro");
            }
            logoHorizontal.setTextSize(TypedValue.COMPLEX_UNIT_SP, screenWidth / 12);
            catchlineHorizontal.setTextSize(TypedValue.COMPLEX_UNIT_SP, screenWidth / 60);
            logoHorizontal.setTypeface(satisfy);
            catchlineHorizontal.setTypeface(satisfy);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }


        new Handler().postDelayed(new Runnable() {
            public void run() {

                startActivity(new Intent(Splash.this, Main2Activity.class));
                finish();
            }
        }, 2500);
    }

    String getStringFromInternal(String fileName, String defaultvalue){
        String value = "";
        File file = new File(getApplicationContext().getDir("LyricLocalBackup", Context.MODE_PRIVATE).getAbsolutePath() + "/" + fileName + ".txt");
        if(!file.exists()){
            return defaultvalue;
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            try {
                String line;
                boolean firstLine = true;
                while( (line = reader.readLine()) != null){
                    if(firstLine){
                        value += line;
                        firstLine = false;
                    }else{
                        value += "\n" + line;
                    }
                }
                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return value;
    }



}
