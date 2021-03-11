package com.cifpfbmoll.a2048;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cifpfbmoll.a2048.Database.DatabaseHelper;
import com.cifpfbmoll.a2048.Database.ScoreObject;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ScoreBoard extends AppCompatActivity {

    private ScoreAdapter scoreAdapter;
    private DatabaseHelper databaseHelper;
    private ArrayList<ScoreObject> myScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_board);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myScores = new ArrayList<>();

        databaseHelper = new DatabaseHelper(this);

        scoreAdapter = new ScoreAdapter(this, databaseHelper, myScores);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) { //Type of movement, DIRECTION
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                databaseHelper.deleteScoreByID(myScores.get(viewHolder.getAdapterPosition()).getId());
                myScores.remove(viewHolder.getAdapterPosition());
                scoreAdapter.notifyDataSetChanged();
            }
        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(scoreAdapter);


        EditText editText = findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {
                applyFilter();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearSearcher(findViewById(R.id.clearSearch));
        scoreAdapter.notifyDataSetChanged();
    }

    public void applyFilter(){
        TextView searchingBy = findViewById(R.id.searchFilter);
        String filter = (String) searchingBy.getText();
        EditText editText = findViewById(R.id.editText);
        String criteria = editText.getText().toString();
        ArrayList<ScoreObject> scoresFromDB;
        TextView noResults = findViewById(R.id.noResults);
        switch (filter){
            case "Searching By NAME":
                myScores.clear();
                scoresFromDB = (ArrayList<ScoreObject>) databaseHelper.findByName(criteria);
                for (int i = 0; i < scoresFromDB.size(); i++) {
                    myScores.add(scoresFromDB.get(i));
                }
                if(scoresFromDB.size() == 0){
                    noResults.setVisibility(View.VISIBLE);
                }else{
                    noResults.setVisibility(View.GONE);
                }
                scoreAdapter.notifyDataSetChanged();break;

            case "Searching By ID":
                myScores.clear();
                if(databaseHelper.findByID(criteria) != null){
                    myScores.add(databaseHelper.findByID(criteria));
                    noResults.setVisibility(View.GONE);
                }else {
                    noResults.setVisibility(View.VISIBLE);
                }
                scoreAdapter.notifyDataSetChanged();
        }
    }

    public void setSearcherVisible(View v){
        LinearLayout searcher = findViewById(R.id.searcherLayout);
        searcher.setVisibility(View.VISIBLE);
        TextView textView = findViewById(R.id.searchFilter);
        textView.setText("Searching By "+((TextView) v).getText());
        EditText editText = findViewById(R.id.editText);
        editText.setText("");
        editText.setHint("Enter the criteria");
        if(((TextView) v).getText().equals("ID")){
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }else{
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        TextView textView2 = findViewById(R.id.noResults);
        textView2.setVisibility(View.GONE);
    }

    public void clearSearcher(View v){
        LinearLayout searcher = findViewById(R.id.searcherLayout);
        searcher.setVisibility(View.GONE);
        EditText editText = findViewById(R.id.editText);
        editText.setText("");
        editText.setHint("Enter the criteria");

        TextView textView = findViewById(R.id.noResults);
        textView.setVisibility(View.GONE);

        myScores.clear();
        ArrayList<ScoreObject> scoresFromDB = (ArrayList<ScoreObject>) databaseHelper.getAllScores();
        for (int i = 0; i < scoresFromDB.size(); i++) {
            myScores.add(scoresFromDB.get(i));
        }
    }

}
