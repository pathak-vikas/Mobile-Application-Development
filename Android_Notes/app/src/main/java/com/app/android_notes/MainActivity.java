package com.app.android_notes;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener {

    RecyclerView recyclerview;
    private final List<NotesClass> notesClassList = new ArrayList<>();
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerview = findViewById(R.id.recyclerview);
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResult);
        
        getdatafromfile();
    }

    public void getdatafromfile(){
        try {
            InputStream is = getApplicationContext().openFileInput(getString(R.string.file_name));
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONArray jsonArray = new JSONArray(sb.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString("strtitle");
                String savetime = jsonObject.getString("savetimelast");
                String content = jsonObject.getString("strcontent");
                NotesClass notesClass = new NotesClass(title, savetime,content);
                notesClassList.add(notesClass);
                setDataonscreen();
            }

        } catch (FileNotFoundException e) {
            Log.d("Exception","File Not Found");
            notesClassList.clear();
            setDataonscreen();
        } catch (Exception e) {
            e.printStackTrace();
            notesClassList.clear();
            setDataonscreen();
        }
    }

    public void saveDataToFile(){
        try {
            FileOutputStream fos = getApplicationContext().
                    openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);

            PrintWriter printWriter = new PrintWriter(fos);
            printWriter.print(notesClassList);
            printWriter.close();
            fos.close();


        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void setDataonscreen(){
        setTitle("AndroidNotes ("+ notesClassList.size()+")");
        Collections.sort(notesClassList, (o1, o2) -> {
            if (o1.getSavetimelast() == null || o2.getSavetimelast() == null)
                return 0;
            return o1.getSavetimelast().compareTo(o2.getSavetimelast());
        });
        Collections.reverse(notesClassList);
        AdapterNotes mAdapter = new AdapterNotes(notesClassList, this);
        recyclerview.setAdapter(mAdapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
    }

    public void handleResult(ActivityResult result) {

        if (result == null || result.getData() == null) {
            return;
        }

        Intent data = result.getData();
        NotesClass notesClass = new NotesClass();
        if (result.getResultCode() == RESULT_OK) {
            notesClass = (NotesClass) data.getSerializableExtra("ANDROIDNOTES");
            int position = data.getIntExtra("position",-1);
            if (notesClass != null) {
                if(position == -1) {
                    notesClassList.add(new NotesClass(notesClass.getStrtitle(), notesClass.getSavetimelast(), notesClass.getStrcontent()));
                    saveDataToFile();
                    setDataonscreen();
                }
                else{
                    notesClassList.set(position,new NotesClass(notesClass.getStrtitle(), notesClass.getSavetimelast(), notesClass.getStrcontent()));
                    saveDataToFile();
                    setDataonscreen();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.info) {
            Intent intent = new Intent(this, ActivityAbout.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.plus) {
            Intent intent = new Intent(this, ActivityEdit.class);
            activityResultLauncher.launch(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerview.getChildLayoutPosition(v);
        NotesClass notesClass = notesClassList.get(pos);
        Intent intent = new Intent(this, ActivityEdit.class);
        intent.putExtra("ANDROIDNOTES", notesClass);
        intent.putExtra("position", pos);
        activityResultLauncher.launch(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        int pos = recyclerview.getChildLayoutPosition(v);
        NotesClass notesClass = notesClassList.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("YES", (dialog, which) -> {
            dialog.dismiss();
            notesClassList.remove(pos);
            saveDataToFile();
            setDataonscreen();
        });

        builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());

        builder.setTitle("Delete Note '"+ notesClass.getStrtitle() +"'?");
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

}