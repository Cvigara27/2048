package com.cifpfbmoll.a2048;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cifpfbmoll.a2048.Database.DatabaseHelper;
import com.cifpfbmoll.a2048.Database.ScoreObject;

import java.util.ArrayList;

class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder>  {

    //Member variables
    private DatabaseHelper databaseHelper;
    private Context mContext;
    private ArrayList<ScoreObject> scoreData;


    /**
     * Constructor that passes in the sports data and the context
     * @param context Context of the application
     */
    ScoreAdapter(Context context, DatabaseHelper dbHelper , ArrayList<ScoreObject> myScores) {
        this.databaseHelper = dbHelper;
        this.mContext = context;
        this.scoreData = myScores;
        loadInfo();
    }


    private void loadInfo(){
        ArrayList<ScoreObject> scoresFromDB = (ArrayList<ScoreObject>) databaseHelper.getAllScores();
        for (int i = 0; i < scoresFromDB.size(); i++) {
            this.scoreData.add(scoresFromDB.get(i));
        }
    }
    /**
     * Required method for creating the viewholder objects.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return The newly create ViewHolder.
     */
    @Override
    public ScoreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.score_item, parent, false));
    }

    /**
     * Required method that binds the data to the viewholder.
     * @param holder The viewholder into which the data should be put.
     * @param position The adapter position.
     */
    @Override
    public void onBindViewHolder(ScoreAdapter.ViewHolder holder, int position) {
        ScoreObject currentScore = this.scoreData.get(position);
        holder.bindTo(currentScore);
    }


    /**
     * Required method for determining the size of the data set.
     * @return Size of the data set.
     */
    @Override
    public int getItemCount() {
        return this.scoreData.size();
    }


    /**
     * ViewHolder class that represents each row of data in the RecyclerView
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //Member Variables for the TextViews
        private TextView username;
        private TextView score;
        private TextView date;
        private TextView time;
        private Button id;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         * @param itemView The rootview of the list_item.xml layout file
         */
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            //Initialize the views
            id = itemView.findViewById(R.id.myId);
            username = itemView.findViewById(R.id.username);
            score = itemView.findViewById(R.id.myScore);
            date = itemView.findViewById(R.id.myDate);
            time = itemView.findViewById(R.id.myTime);
        }


        public void bindTo(ScoreObject currentScore){
            //Populate the textviews with data
            id.setText(String.valueOf(currentScore.getId()));
            username.setText(currentScore.getUsername());
            score.setText(String.valueOf(currentScore.getScore()));
            date.setText(String.valueOf(currentScore.getDate()));
            time.setText(String.valueOf(currentScore.getTime()));
        }

        @Override
        public void onClick(View view) {
            ScoreObject currentScore = scoreData.get(getAdapterPosition());
            Intent intent = new Intent(mContext, EditName.class);
            intent.putExtra("USERNAME", currentScore.getUsername());
            intent.putExtra("ID", currentScore.getId());
            mContext.startActivity(intent);
        }
    }
}
