package com.app.android_notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

public class ActivityEdit extends AppCompatActivity {

    EditText heading,content;
    private NotesClass notesClass;
    private int position;
    private int templocalvariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        heading = findViewById(R.id.heading);
        content = findViewById(R.id.content);
        notesClass = new NotesClass();
        position = -1;
        templocalvariable = 0;

        heading.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                templocalvariable++;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                templocalvariable++;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        
        getPreviousData();
    }
    public void closeActivityOnBackPress(){
        super.onBackPressed();
    }
    public boolean isNoteChangedOrNew(){
        if(notesClass!=null){
            if(!(notesClass.getStrtitle() == null ) && notesClass.getStrtitle().equals(heading.getText().toString()) && !(notesClass.getStrcontent() == null) && notesClass.getStrcontent().equals(content.getText().toString())){
                return  false;
            }
        }
        return true;
    }


    public void getPreviousData(){
        Intent intent = getIntent();
        if (intent.hasExtra("ANDROIDNOTES")) {
            notesClass = (NotesClass) intent.getSerializableExtra("ANDROIDNOTES");
            position = intent.getIntExtra("position",-1);
            if (notesClass != null)
                heading.setText(notesClass.getStrtitle());
            assert notesClass != null;
            content.setText(notesClass.getStrcontent());
        } else {
            heading.setText("");
            content.setText("");
        }
    }

    public void returndatatoactivity(){
        notesClass.setStrcontent(content.getText().toString());
        notesClass.setStrtitle(heading.getText().toString());
        notesClass.setSavetimelast(""+new Date());
        Intent intent = new Intent();
        intent.putExtra("ANDROIDNOTES", notesClass);
        intent.putExtra("position",position);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void savedataDialog(){
        if(TextUtils.isEmpty(heading.getText().toString().trim())){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setPositiveButton("YES", (dialog, which) -> {
                dialog.dismiss();
                ActivityEdit.super.onBackPressed();
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast.makeText(ActivityEdit.this,"Changes to note were not saved",Toast.LENGTH_LONG).show();
                    closeActivityOnBackPress();
                }
            });

            builder.setTitle("Your Cannot save a note without Title!\n Are you sure you want to exit?");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        returndatatoactivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editactivity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String titleString = heading.getText().toString().trim();
        String contentString = content.getText().toString().trim();
        if (item.getItemId() == R.id.save) {

            if (!titleString.equals("")) {
                if (isNoteChangedOrNew()) {
                    savedataDialog();
                    return true;

                } else {
                    Toast.makeText(this, "There were no changes to note", Toast.LENGTH_LONG).show();
                    returndatatoactivity();
                    return true;
                }
            } else if(!contentString.equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setPositiveButton("YES", (dialog, which) -> {
                    dialog.dismiss();
                    ActivityEdit.super.onBackPressed();
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                    }
                });

                builder.setTitle("Your Cannot save a note without Title!\n Are you sure you want to exit?");
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            }
            else {
                Toast.makeText(this, "Un-titled note was not saved", Toast.LENGTH_LONG).show();
                ActivityEdit.super.onBackPressed();
                return true;
            }
        }
            return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        String titleString= heading.getText().toString().trim();
        if(!titleString.equals("")) {
            if (isNoteChangedOrNew()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    savedataDialog();
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(ActivityEdit.this, "Changes to note were not saved", Toast.LENGTH_LONG).show();
                        closeActivityOnBackPress();
                    }
                });

                builder.setTitle("Your Note is not saved!\nSave Note '" + heading.getText().toString() + "'?");
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {

                ActivityEdit.super.onBackPressed();
                Toast.makeText(this, "There were no changes to note", Toast.LENGTH_LONG).show();
            }
        } else{
            super.onBackPressed();
            Toast.makeText(this, "Un-titled note was not saved", Toast.LENGTH_LONG).show();
        }

    }


}