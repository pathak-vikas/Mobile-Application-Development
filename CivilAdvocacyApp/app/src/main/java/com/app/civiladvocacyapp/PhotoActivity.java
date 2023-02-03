package com.app.civiladvocacyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {

    private TextView locationTextView;
    private TextView officeTextView;
    private TextView nameTextView;
    private ImageView photoView;
    private Intent intent;
    private Official official;
    private ImageView partyicon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        locationTextView = (TextView) findViewById(R.id.pa_location);
        officeTextView = (TextView) findViewById(R.id.pa_office);
        nameTextView = (TextView) findViewById(R.id.pa_name);
        photoView = (ImageView) findViewById(R.id.pa_photo);
        partyicon =  findViewById(R.id.partyicon);

        intent = getIntent();
        official = (Official) intent.getSerializableExtra("official");
        Log.d("after go to pa", locationTextView.getText().toString());
        CharSequence ch = intent.getCharSequenceExtra("location");

        if (ch == null) Log.d("ch", "null");
        else Log.d("ch", ch.toString());
        locationTextView.setText(intent.getCharSequenceExtra("location"));

        officeTextView.setText(official.getOfficeName());
        nameTextView.setText(official.getName());

        if (official.getParty() != null) {
            if (official.getParty().equals("Republican Party")) {
                getWindow().getDecorView().setBackgroundColor(Color.RED);
                partyicon.setImageResource(R.drawable.rep_logo);
            }

            else if (official.getParty().equals("Democratic Party")) {
                getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                partyicon.setImageResource(R.drawable.dem_logo);
            }
            else getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        } else getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        if (official.getPhotoUrl() != null) {
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                    final String changedUrl = official.getPhotoUrl().replace("http:", "https:");
                    picasso.load(changedUrl).error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder).into(photoView);
                }
            }).build();

            picasso.load(official.getPhotoUrl()).error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder).into(photoView);
        } else {
            Picasso.get().load(official.getPhotoUrl()).error(R.drawable.brokenimage).placeholder(R.drawable.missing).into(photoView);
        }
    }
}
