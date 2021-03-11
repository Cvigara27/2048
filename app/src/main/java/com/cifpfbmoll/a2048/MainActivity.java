package com.cifpfbmoll.a2048;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cifpfbmoll.a2048.Database.DatabaseHelper;
import com.cifpfbmoll.a2048.Database.ScoreObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    final Handler handler = new Handler(Looper.getMainLooper());
    private final Button[][] buttonArray = new Button[4][4];
    private final String[][] undoValues = new String[4][4];
    private String undoScore;
    private final ArrayList<String> buttonCoords = new ArrayList<>();
    private DatabaseHelper databaseHelper;
    private final int[] numbersGenerated = new int[]{2, 2, 2, 2, 2, 2, 4};
    private boolean actionIsDone = true;
    private static boolean gameReallyEnded = false;
    private boolean cronoIsRunning;
    private boolean notFirstTime = false;
    private Thread chronometer;
    private int mili = 0, seg = 0, min = 0;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        mp = MediaPlayer.create(this, R.raw.music);
        mp.setLooping(true);
        mp.start();


        Button bestScore = findViewById(R.id.scoreBest);
        bestScore.setText(databaseHelper.getBestScore());

        TextView text2048 = findViewById(R.id.text2048);
        String coloredText =
                "<font color=#D98EF5>2</font>" +
                "<font color=#AF23EB>0</font>" +
                "<font color=#F08B94>4</font>" +
                "<font color=#F54060>8</font>";
        text2048.setText(Html.fromHtml(coloredText));

        getAllButtons();
        addListeners();
        generateNumber();
        actionIsDone=true;
        generateNumber();
        setUndoValues();
        startChrono();
        cronoIsRunning = false;
    }


    @Override
    protected void onPause() {
        super.onPause();
        mp.pause();
        cronoIsRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mp.start();
        if(gameReallyEnded){
            restart(findViewById(R.id.restart));
            gameReallyEnded = false;
        }
        if(notFirstTime){
            cronoIsRunning = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.stop();
        mp.release();
    }

    public static void setGameEnded(boolean action){
        gameReallyEnded = action;
    }

    public void GameOver(View v){
        Intent intent = new Intent(MainActivity.this, GameOver.class);
        Button button = findViewById(R.id.scoreCurrent);
        String savedScore = (String) button.getText();
        intent.putExtra("SCORE", savedScore);
        TextView chronoMin = findViewById(R.id.cronoMin);
        TextView chronoSeg = findViewById(R.id.cronoSeg);
        TextView chronoMili = findViewById(R.id.cronoMili);
        String min = (String) chronoMin.getText();
        String seg = (String) chronoSeg.getText();
        String mili = (String) chronoMili.getText();
        String composedTime = min+" : "+seg+" : "+mili;
        intent.putExtra("TIME", composedTime);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_in2);
        cronoIsRunning = false;
    }

    private void startChrono(){
        TextView chronoMin = findViewById(R.id.cronoMin);
        TextView chronoSeg = findViewById(R.id.cronoSeg);
        TextView chronoMili = findViewById(R.id.cronoMili);
        chronometer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(cronoIsRunning){
                        try{
                            Thread.sleep(1);
                        }catch (Exception e){
                            System.out.println("Chronometer exception: "+e);
                        }
                        mili++;
                        if(mili==999){
                            seg++;
                            mili=0;
                        }
                        if(seg==60){
                            min++;
                            seg=0;
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                String m="", s="", mi="";
                                if(mili<10){
                                    m="00"+mili;
                                }else if(mili<100){
                                    m="0"+mili;
                                }else {
                                    m+=mili;
                                }
                                if(seg<10){
                                    s="0"+seg;
                                }else {
                                    s+=seg;
                                }
                                if(min<10){
                                    mi="0"+min;
                                }else {
                                    mi+=min;
                                }
                                chronoMin.setText(mi);
                                chronoSeg.setText(s);
                                chronoMili.setText(m);
                            }
                        });
                    }
                }
            }
        });
        chronometer.start();
    }

    public void restart(View v){
        cronoIsRunning = false;
        Button currentScore = findViewById(R.id.scoreCurrent);
        currentScore.setText("0");
        buttonCoords.clear();
        for (int i = 0; i < buttonArray.length; i++) {
            for (int j = 0; j < buttonArray.length; j++) {
                buttonCoords.add(i+"/"+j);
                int id = getResources().getIdentifier("cell"+i+j,"id", getPackageName());
                Button button = findViewById(id);
                button.setText("");
                checkColors(button);
                undoValues[i][j] = "";
            }
        }
        actionIsDone = true;
        generateNumber();
        actionIsDone = true;
        generateNumber();
        TextView chronoMin = findViewById(R.id.cronoMin);
        TextView chronoSeg = findViewById(R.id.cronoSeg);
        TextView chronoMili = findViewById(R.id.cronoMili);
        chronoMin.setText("00");
        chronoSeg.setText("00");
        chronoMili.setText("000");
        min = 0;
        seg = 0;
        mili = 0;
    }

    private void addListeners(){
        View layout = findViewById(R.id.myLayout);
        layout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this){
            public void onSwipeTop() {
                swipeUp();
                handler.postDelayed(MainActivity.this::sumUp, 200);
                handler.postDelayed(MainActivity.this::swipeUp, 200);
                handler.postDelayed(MainActivity.this::generateNumber, 350);

            }

            public void onSwipeRight() {
                swipeRight();
                handler.postDelayed(MainActivity.this::sumRight, 200);
                handler.postDelayed(MainActivity.this::swipeRight, 200);
                handler.postDelayed(MainActivity.this::generateNumber, 350);
            }

            public void onSwipeLeft() {
                swipeLeft();
                handler.postDelayed(MainActivity.this::sumLeft, 200);
                handler.postDelayed(MainActivity.this::swipeLeft, 200);
                handler.postDelayed(MainActivity.this::generateNumber, 350);
            }

            public void onSwipeBottom() {
                swipeDown();
                handler.postDelayed(MainActivity.this::sumDown, 200);
                handler.postDelayed(MainActivity.this::swipeDown, 200);
                handler.postDelayed(MainActivity.this::generateNumber, 350);
            }
        });
        for (int i = 0; i < buttonArray.length; i++) {
            for (int j = 0; j < buttonArray.length; j++) {
                int id = getResources().getIdentifier("cell" + i + j, "id", getPackageName());
                Button button = findViewById(id);
                button.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
                    public void onSwipeTop() {
                        swipeUp();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sumUp();
                            }
                        }, 200);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipeUp();
                            }
                        }, 200);
                        handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    generateNumber();
                                }
                            }, 350);

                    }

                    public void onSwipeRight() {
                        swipeRight();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sumRight();
                            }
                        }, 200);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipeRight();
                            }
                        }, 200);
                        handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    generateNumber();
                                }
                            }, 350);

                    }

                    public void onSwipeLeft() {
                        swipeLeft();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sumLeft();
                            }
                        }, 200);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipeLeft();
                            }
                        }, 200);
                        handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    generateNumber();
                                }
                            }, 350);

                    }

                    public void onSwipeBottom() {
                        swipeDown();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sumDown();
                            }
                        }, 200);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipeDown();
                            }
                        }, 200);
                        handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    generateNumber();
                                }
                            }, 350);

                    }
                });
            }
        }
    }

    private void checkColors(Button myButton){
       String number = (String) myButton.getText();
       if(number.equals("")){
           myButton.setBackgroundColor(getColor(R.color.colorTile));
       }else {
           int id = getResources().getIdentifier("color"+number,"color", getPackageName());
           int color = getColor(id);
           myButton.setBackgroundColor(color);
       }

    }

    private void checkGameWin(){
        Boolean winner = false;
        for (int i = 0; i < buttonArray.length; i++) {
            for (int j = 0; j <buttonArray[0].length ; j++) {
                if (buttonArray[i][j].getText().equals("2048")){
                    winner = true;
                }
            }
        }
        if (winner){
            Intent intent = new Intent(MainActivity.this, WinScreen.class);
            Button button = findViewById(R.id.scoreCurrent);
            String savedScore = (String) button.getText();
            intent.putExtra("SCORE", savedScore);
            TextView chronoMin = findViewById(R.id.cronoMin);
            TextView chronoSeg = findViewById(R.id.cronoSeg);
            TextView chronoMili = findViewById(R.id.cronoMili);
            String min = (String) chronoMin.getText();
            String seg = (String) chronoSeg.getText();
            String mili = (String) chronoMili.getText();
            String composedTime = min+" : "+seg+" : "+mili;
            intent.putExtra("TIME", composedTime);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_in2);
            cronoIsRunning = false;
            gameReallyEnded = true;
        }
    }

    private boolean checkGameEnded(){
        boolean ended = true;
        if(buttonCoords.size() == 0){
            for (int i = 0; i < buttonArray.length; i++) {
                for (int j = 0; j < buttonArray[0].length; j++) {
                    if ((i > 0 && buttonArray[i - 1][j].getText() == buttonArray[i][j].getText()) ||
                            (i < 3 && buttonArray[i + 1][j].getText() == buttonArray[i][j].getText()) ||
                            (j > 0 && buttonArray[i][j - 1].getText() == buttonArray[i][j].getText()) ||
                            (j < 3 && buttonArray[i][j + 1].getText() == buttonArray[i][j].getText())){
                        ended = false;
                    }
                }
            }
        }else{
            ended = false;
        }
        if(ended){
            Intent intent = new Intent(MainActivity.this, GameOver.class);
            Button button = findViewById(R.id.scoreCurrent);
            String savedScore = (String) button.getText();
            intent.putExtra("SCORE", savedScore);
            TextView chronoMin = findViewById(R.id.cronoMin);
            TextView chronoSeg = findViewById(R.id.cronoSeg);
            TextView chronoMili = findViewById(R.id.cronoMili);
            String min = (String) chronoMin.getText();
            String seg = (String) chronoSeg.getText();
            String mili = (String) chronoMili.getText();
            String composedTime = min+" : "+seg+" : "+mili;
            intent.putExtra("TIME", composedTime);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_in2);
            cronoIsRunning = false;
            gameReallyEnded = true;
        }
        return ended;
    }

    private boolean containsNothing(Button newButton){
        boolean rtn = true;
        String buttonText = (String) newButton.getText();
        if(!buttonText.equals("")){
            rtn = false;
        }
        return rtn;
    }

    private boolean containsSomething(Button newButton){
        boolean rtn = true;
        String buttonText = (String) newButton.getText();
        if(buttonText.equals("")){
            rtn = false;
        }
        return rtn;
    }

    private void generateNumber(){
        if(!checkGameEnded() && actionIsDone){
            int randomNumber = (int) (Math.random() * (numbersGenerated.length-1) - 0 + 1);
            String numberToString = Integer.toString(numbersGenerated[randomNumber]);

            int randomCoord = (int) (Math.random() * (buttonCoords.size()-1) - 0+ 1);
            System.out.println("Coords Size: "+buttonCoords.size());
            if(buttonCoords.size() == 1){
                randomCoord = 0;
            }
            String[] coordinates = buttonCoords.get(randomCoord).split("/");
            int row = Integer.parseInt(coordinates[0]);
            int column = Integer.parseInt(coordinates[1]);
            System.out.println("Row and column:"+row+" "+column);

            Button button = buttonArray[row][column];
            button.setText(numberToString);
            checkColors(button);
            buttonCoords.remove(randomCoord);
        }
        actionIsDone=false;
        checkGameEnded();
    }

    private void getAllButtons(){
        for (int i = 0; i < buttonArray.length; i++) {
            for (int j = 0; j < buttonArray.length; j++) {
                buttonCoords.add(i+"/"+j);
                int id = getResources().getIdentifier("cell"+i+j,"id", getPackageName());
                Button button = findViewById(id);
                buttonArray[i][j] = button;
            }
        }
    }

    private void setUndoValues(){
        for (int i = 0; i < undoValues.length; i++) {
            for (int j = 0; j < undoValues[0].length; j++) {
                undoValues[i][j] = (String) buttonArray[i][j].getText();
            }
        }
        Button button = findViewById(R.id.scoreCurrent);
        undoScore = (String) button.getText();
    }

    public void undoAction(View v){
        for (int i = 0; i < undoValues.length; i++) {
            for (int j = 0; j < undoValues[0].length; j++) {
                buttonArray[i][j].setText(undoValues[i][j]);
                checkColors(buttonArray[i][j]);
            }
        }
        Button button = findViewById(R.id.scoreCurrent);
        button.setText(undoScore);
    }

    private void removeCoord(int coordY, int coordX){
        for (int i = 0; i < buttonCoords.size(); i++) {
            if (buttonCoords.get(i).contains(coordY+"/"+coordX)){
                buttonCoords.remove(i);
            }
        }
    }

    private void swipeUp() {
        notFirstTime = true;
        cronoIsRunning = true;
        if(!actionIsDone){
            setUndoValues();
        }
        for (int i = 1; i < buttonArray.length; i++) {
            for (int j = 0; j < buttonArray[0].length; j++) {
                for (int k = i; k > 0; k--) {

                    int id = getResources().getIdentifier("cell"+k+j,"id", getPackageName());
                    Button button = findViewById(id);

                    int id2 = getResources().getIdentifier("cell"+(k-1)+j,"id", getPackageName());
                    Button newButton = findViewById(id2);

                    if(containsNothing(newButton) && containsSomething(button)){
                        String buttonText = (String) button.getText();
                        newButton.setText(buttonText);
                        button.setText("");
                        removeCoord((k-1), j);
                        buttonCoords.add(k+"/"+j);
                        actionIsDone = true;
                        checkColors(newButton);
                        checkColors(button);
                    }
                }
            }
        }
    }

    private void sumUp(){
        for (int i = 1; i < buttonArray.length; i++) {
            for (int j = 0; j < buttonArray[0].length; j++) {
                int id = getResources().getIdentifier("cell"+i+j,"id", getPackageName());
                Button button = findViewById(id);

                int id2 = getResources().getIdentifier("cell"+(i-1)+j,"id", getPackageName());
                Button newButton = findViewById(id2);

                String oldString = (String) button.getText();
                String newString = (String) newButton.getText();

                if(oldString != ""  && newString != ""){
                    if(Integer.valueOf(oldString).equals(Integer.valueOf(newString))) {
                        int oldButtonNumber = Integer.parseInt(oldString);
                        int newButtonNumber = Integer.parseInt(newString);

                        Button scoreButton = findViewById(R.id.scoreCurrent);
                        int myScore = Integer.parseInt((String) scoreButton.getText());
                        myScore += (oldButtonNumber + newButtonNumber);
                        String newScoreStringed = Integer.toString(myScore);
                        scoreButton.setText(newScoreStringed);

                        String newNumberStringed = Integer.toString(oldButtonNumber + newButtonNumber);
                        newButton.setText(newNumberStringed);
                        button.setText("");
                        removeCoord((i - 1), j);
                        buttonCoords.add(i + "/" + j);
                        actionIsDone = true;
                        checkColors(newButton);
                        checkColors(button);
                    }
                }
            }
        }
        checkGameWin();
    }

    private void swipeRight(){
        notFirstTime = true;
        cronoIsRunning = true;
        if(!actionIsDone){
            setUndoValues();
        }
        for (int i = 0; i < buttonArray.length; i++) {
            for (int j = buttonArray[0].length-2; j > -1; j--) {
                for (int k = j; k < buttonArray[0].length-1; k++) {
                    int id = getResources().getIdentifier("cell"+i+k,"id", getPackageName());
                    Button button = findViewById(id);

                    int id2 = getResources().getIdentifier("cell"+i+(k+1),"id", getPackageName());
                    Button newButton = findViewById(id2);

                    if(containsNothing(newButton) && containsSomething(button)){
                        String buttonText = (String) button.getText();
                        newButton.setText(buttonText);
                        button.setText("");
                        removeCoord(i, (k+1));
                        buttonCoords.add(i+"/"+k);
                        actionIsDone = true;
                        checkColors(newButton);
                        checkColors(button);
                    }
                }
            }
        }
    }

    private void sumRight(){
        for (int i = 0; i < buttonArray.length; i++) {
            for (int j = buttonArray[0].length-2; j > -1; j--) {
                int id = getResources().getIdentifier("cell"+i+j,"id", getPackageName());
                Button button = findViewById(id);

                int id2 = getResources().getIdentifier("cell"+i+(j+1),"id", getPackageName());
                Button newButton = findViewById(id2);

                String oldString = (String) button.getText();
                String newString = (String) newButton.getText();

                if(oldString != ""  && newString != ""){
                    if(Integer.valueOf(oldString).equals(Integer.valueOf(newString))){
                        int oldButtonNumber = Integer.parseInt(oldString);
                        int newButtonNumber = Integer.parseInt(newString);
                        Button scoreButton = findViewById(R.id.scoreCurrent);
                        int myScore = Integer.parseInt((String)scoreButton.getText());
                        myScore+=(oldButtonNumber+newButtonNumber);
                        System.out.println("La suma es: "+oldButtonNumber+newButtonNumber);
                        String newScoreStringed = Integer.toString(myScore);
                        scoreButton.setText(newScoreStringed);
                        String newNumberStringed = Integer.toString(oldButtonNumber + newButtonNumber);
                        newButton.setText(newNumberStringed);
                        button.setText("");
                        removeCoord(i, (j+1));
                        buttonCoords.add(i+"/"+j);
                        actionIsDone = true;
                        checkColors(newButton);
                        checkColors(button);
                    }
                }
            }
        }
        checkGameWin();
    }

    private void swipeLeft(){
        notFirstTime = true;
        cronoIsRunning = true;
        if(!actionIsDone){
            setUndoValues();
        }
        for (int i = 0; i < buttonArray.length; i++) {
            for (int j = 1; j < buttonArray[0].length; j++) {
                for (int k = j; k > 0; k--) {
                    int id = getResources().getIdentifier("cell"+i+k,"id", getPackageName());
                    Button button = findViewById(id);

                    int id2 = getResources().getIdentifier("cell"+i+(k-1),"id", getPackageName());
                    Button newButton = findViewById(id2);

                    if(containsNothing(newButton)  && containsSomething(button)){
                        String buttonText = (String) button.getText();
                        newButton.setText(buttonText);
                        button.setText("");
                        removeCoord(i, (k-1));
                        buttonCoords.add(i+"/"+k);
                        actionIsDone = true;
                        checkColors(newButton);
                        checkColors(button);
                    }
                }
            }
        }
    }

    private void sumLeft(){
        for (int i = 0; i < buttonArray.length; i++) {
            for (int j = 1; j < buttonArray[0].length; j++) {
                int id = getResources().getIdentifier("cell"+i+j,"id", getPackageName());
                Button button = findViewById(id);

                int id2 = getResources().getIdentifier("cell"+i+(j-1),"id", getPackageName());
                Button newButton = findViewById(id2);

                String oldString = (String) button.getText();
                String newString = (String) newButton.getText();

                if(oldString != ""  && newString != ""){
                    if(Integer.valueOf(oldString).equals(Integer.valueOf(newString))) {
                        int oldButtonNumber = Integer.parseInt(oldString);
                        int newButtonNumber = Integer.parseInt(newString);
                        Button scoreButton = findViewById(R.id.scoreCurrent);
                        int myScore = Integer.parseInt((String) scoreButton.getText());
                        myScore += (oldButtonNumber + newButtonNumber);
                        String newScoreStringed = Integer.toString(myScore);
                        scoreButton.setText(newScoreStringed);
                        String newNumberStringed = Integer.toString(oldButtonNumber + newButtonNumber);
                        newButton.setText(newNumberStringed);
                        button.setText("");
                        removeCoord(i, (j - 1));
                        buttonCoords.add(i + "/" + j);
                        actionIsDone = true;
                        checkColors(newButton);
                        checkColors(button);
                    }
                }
            }
        }
        checkGameWin();
    }

    private void swipeDown(){
        notFirstTime = true;
        cronoIsRunning = true;
        if(!actionIsDone){
            setUndoValues();
        }
        for (int i = buttonArray.length-2; i > -1; i--) {
            for (int j = 0; j < buttonArray[0].length; j++) {
                for (int k = i; k < buttonArray.length-1; k++) {
                    int id = getResources().getIdentifier("cell"+k+j,"id", getPackageName());
                    Button button = findViewById(id);

                    int id2 = getResources().getIdentifier("cell"+(k+1)+j,"id", getPackageName());
                    Button newButton = findViewById(id2);

                    if(containsNothing(newButton) && containsSomething(button)){
                        String buttonText = (String) button.getText();
                        newButton.setText(buttonText);
                        button.setText("");
                        removeCoord((k+1), j);
                        buttonCoords.add(k+"/"+j);
                        actionIsDone = true;
                        checkColors(newButton);
                        checkColors(button);
                    }
                }
            }
        }
    }

    private void sumDown(){
        for (int i = buttonArray.length-2; i > -1; i--) {
            for (int j = 0; j < buttonArray[0].length; j++) {
                int id = getResources().getIdentifier("cell"+i+j,"id", getPackageName());
                Button button = findViewById(id);

                int id2 = getResources().getIdentifier("cell"+(i+1)+j,"id", getPackageName());
                Button newButton = findViewById(id2);

                String oldString = (String) button.getText();
                String newString = (String) newButton.getText();

                if(oldString != ""  && newString != ""){
                    if(Integer.valueOf(oldString).equals(Integer.valueOf(newString))) {
                        int oldButtonNumber = Integer.parseInt(oldString);
                        int newButtonNumber = Integer.parseInt(newString);
                        Button scoreButton = findViewById(R.id.scoreCurrent);
                        int myScore = Integer.parseInt((String) scoreButton.getText());
                        myScore += (oldButtonNumber + newButtonNumber);
                        String newScoreStringed = Integer.toString(myScore);
                        scoreButton.setText(newScoreStringed);
                        String newNumberStringed = Integer.toString(oldButtonNumber + newButtonNumber);
                        newButton.setText(newNumberStringed);
                        button.setText("");
                        removeCoord((i + 1), j);
                        buttonCoords.add(i + "/" + j);
                        actionIsDone = true;
                        checkColors(newButton);
                        checkColors(button);
                    }
                }
            }
        }
        checkGameWin();
    }
}