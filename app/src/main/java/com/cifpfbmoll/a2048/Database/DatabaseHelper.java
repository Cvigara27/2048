package com.cifpfbmoll.a2048.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase WritableDB;
    private static final String DB_NAME = "ScoresDB";
    private static final String TABLE_NAME = "ScoreTable";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_USERNAME = "NAME";
    private static final String COLUMN_SCORE = "SCORE";
    private static final String COLUMN_DATE = "DATE";
    private static final String COLUMN_TIME = "TIME";
    private static final String CREATE_TABLE_STATEMENT =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT, " +
                    COLUMN_SCORE + " INT, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_TIME + " INT) ";

    public DatabaseHelper(@Nullable Context context){
        super(context, DB_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insert(ScoreObject scoreObject){
        ContentValues values = new ContentValues();
        try {
            if (WritableDB == null) {
                WritableDB = getWritableDatabase();
            }
            values.put(COLUMN_USERNAME, scoreObject.getUsername());
            values.put(COLUMN_SCORE, scoreObject.getScore());
            values.put(COLUMN_DATE, scoreObject.getDate());
            values.put(COLUMN_TIME, scoreObject.getTime());
            WritableDB.insert(TABLE_NAME,null,values);
        } catch (Exception e) {
            System.out.println("Error on insert(): "+e);
        }
    }

    public List<ScoreObject> getAllScores(){
        ArrayList<ScoreObject> myScores = new ArrayList<>();
        try {
            if (WritableDB == null) {
                WritableDB = getWritableDatabase();
            }
            String getAllScoreQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY "+ COLUMN_SCORE +" DESC";
            Cursor cursor = WritableDB.rawQuery(getAllScoreQuery, null);
            if(cursor.moveToNext()){
                do{
                    int scoreId = cursor.getInt(0);
                    String usernameId = cursor.getString(1);
                    Integer score = cursor.getInt(2);
                    String date = cursor.getString(3);
                    String time = cursor.getString(4);
                    ScoreObject newScore = new ScoreObject(scoreId, usernameId, score, date, time);
                    myScores.add(newScore);
                }while(cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println("Error on getBestScore(): "+e);
        }
        return myScores;
    }


    public void deleteScoreByID(Integer id){
        try {
            if (WritableDB == null) {
                WritableDB = getWritableDatabase();
            }
            String queryDeleteByID = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + id;
            WritableDB.execSQL(queryDeleteByID);
        }catch (Exception e){
            System.out.println("deleteByID: "+e);
        }
    }

    public ScoreObject findByID(String id){
        ScoreObject ScoreByID = null;
        if(!id.equals("")){
            Integer ID = Integer.valueOf(id);
            try {
                if (WritableDB == null) {
                    WritableDB = getWritableDatabase();
                }
                String findByID = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + ID;
                Cursor cursor = WritableDB.rawQuery(findByID, null);
                if(cursor.moveToFirst()){
                    int scoreId = cursor.getInt(0);
                    String usernameId = cursor.getString(1);
                    Integer score = cursor.getInt(2);
                    String date = cursor.getString(3);
                    String time = cursor.getString(4);
                    ScoreByID = new ScoreObject(scoreId, usernameId, score, date, time);
                }
                cursor.close();
            }catch (Exception e){
                System.out.println("deleteByID: "+e);
            }
        }
        return ScoreByID;
    }

    public List<ScoreObject> findByName(String Name){
        ArrayList<ScoreObject> myScores = new ArrayList<>();
        if(!Name.equals("")){
            try {
                if (WritableDB == null) {
                    WritableDB = getWritableDatabase();
                }
                String getAllScoreQuery = "SELECT * FROM " + TABLE_NAME + " WHERE "+ COLUMN_USERNAME + " LIKE '%" + Name + "%' ORDER BY "+ COLUMN_SCORE +" DESC";
                Cursor cursor = WritableDB.rawQuery(getAllScoreQuery, null);
                if(cursor.moveToNext()){
                    do{
                        int scoreId = cursor.getInt(0);
                        String usernameId = cursor.getString(1);
                        Integer score = cursor.getInt(2);
                        String date = cursor.getString(3);
                        String time = cursor.getString(4);
                        ScoreObject newScore = new ScoreObject(scoreId, usernameId, score, date, time);
                        myScores.add(newScore);
                        System.out.println("Score was added: "+scoreId);
                    }while(cursor.moveToNext());
                }
                cursor.close();
            } catch (Exception e) {
                System.out.println("Error on findByName(): "+e);
            }
        }
        return myScores;
    }

    public String getBestScore(){
        String myScore = null;
        try {
            if (WritableDB == null) {
                WritableDB = getWritableDatabase();
            }
            String getTopScoreQuery = "SELECT MAX(" + COLUMN_SCORE + ") FROM " + TABLE_NAME;
            Cursor cursor = WritableDB.rawQuery(getTopScoreQuery, null);
            cursor.moveToFirst();
            Integer topScore = cursor.getInt(0);
            if(topScore == 0){
                myScore = "N/A";
            }else {
                myScore = Integer.toString(topScore);
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println("Error on getBestScore(): "+e);
        }
        return myScore;
    }

    public void update(int id, String username){
        try{
            if (WritableDB == null) {
                WritableDB = getWritableDatabase();
            }
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, username);
            WritableDB.update(TABLE_NAME,values,COLUMN_ID + " = ?",new String[]{String.valueOf(id)});
        }catch (Exception e){
            System.out.println("Error on delete: "+e);
        }
    }
}
