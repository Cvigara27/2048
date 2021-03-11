package com.cifpfbmoll.a2048;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class Menu extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        TextView text2048 = findViewById(R.id.text2048Menu);
        String coloredText =
                "<font color=#D98EF5>2</font>" +
                        "<font color=#AF23EB>0</font>" +
                        "<font color=#F08B94>4</font>" +
                        "<font color=#F54060>8</font>";
        text2048.setText(Html.fromHtml(coloredText));
    }


    public void goToGame(View v){
        startActivity(new Intent(Menu.this,
                MainActivity.class));
        overridePendingTransition(R.anim.fade_in,R.anim.fade_in2);
    }

    public void goToScores(View v){
        startActivity(new Intent(Menu.this,
                ScoreBoard.class));
        overridePendingTransition(R.anim.fade_in,R.anim.fade_in2);
    }
}
