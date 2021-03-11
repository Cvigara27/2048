package com.cifpfbmoll.a2048;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.cifpfbmoll.a2048.Database.DatabaseHelper;
import com.cifpfbmoll.a2048.Database.ScoreObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GameOver extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private String savedScore;
    private String savedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                savedScore = null;
                savedTime = null;
            } else {
                savedScore= extras.getString("SCORE");
                savedTime= extras.getString("TIME");
            }
        } else {
            savedScore = (String) savedInstanceState.getSerializable("SCORE");
            savedTime = (String) savedInstanceState.getSerializable("TIME");
        }


        databaseHelper = new DatabaseHelper(this);
    }

    public void cancelGameOver(View v){
        finish();
    }

    public void setUsername(View v){
        RelativeLayout relativeLayout = findViewById(R.id.saveUsername);
        relativeLayout.setVisibility(View.VISIBLE);
    }

    public void saveData(View v){
        RelativeLayout relativeLayout = findViewById(R.id.saveUsername);
        relativeLayout.setVisibility(View.VISIBLE);
        ScoreObject scoreObject = new ScoreObject();
        EditText editText = findViewById(R.id.editUsername);
        String username = editText.getText().toString();
        if(username.equals("")){
            username = "Unknown";
        }
        scoreObject.setUsername(username);
        scoreObject.setScore(Integer.parseInt(savedScore));
        scoreObject.setDate(getActualDate());
        scoreObject.setTime(savedTime);
        databaseHelper.insert(scoreObject);
        MainActivity.setGameEnded(true);
        finish();
    }

    public void cancelSaving(View v){
        RelativeLayout relativeLayout = findViewById(R.id.saveUsername);
        relativeLayout.setVisibility(View.GONE);
        EditText editText = findViewById(R.id.editUsername);
        editText.setText("");
        editText.setHint("Enter your username");
    }

    private String getActualDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
        String date = dateFormat.format(calendar.getTime());
        return date;
    }
}
