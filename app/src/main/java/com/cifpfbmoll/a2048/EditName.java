package com.cifpfbmoll.a2048;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.cifpfbmoll.a2048.Database.DatabaseHelper;

public class EditName extends AppCompatActivity {

    private String savedUsername;
    private Integer id;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_username);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                savedUsername = null;
                id = null;
            } else {
                savedUsername = extras.getString("USERNAME");
                id = extras.getInt("ID");
            }
        } else {
            savedUsername = (String) savedInstanceState.getSerializable("USERNAME");
            id = (Integer) savedInstanceState.getSerializable("ID");
        }
        EditText editText = findViewById(R.id.editingUsername);
        editText.setText(savedUsername);
        databaseHelper = new DatabaseHelper(this);
    }

    public void cancelUpdating(View v){
        finish();
    }

    public void updateData(View v){
        EditText editText = findViewById(R.id.editingUsername);
        String username = editText.getText().toString();
        System.out.println("new text is:"+username);
        System.out.println("ID is: "+id);
        databaseHelper.update(id, username);
        finish();
    }
}
