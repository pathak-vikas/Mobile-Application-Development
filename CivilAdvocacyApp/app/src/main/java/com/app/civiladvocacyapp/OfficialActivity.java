package com.app.civiladvocacyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {

    private TextView locationTextView;
    private TextView officeTextView;
    private TextView nameTextView;
    private TextView partyTextView;
    private TextView addressTextView;
    private TextView phoneTextView;
    private TextView emailTextView;
    private TextView webTextView;
    private ImageButton photoImageView;
    private ImageButton facebookImageView;
    private ImageButton twitterImageView;
    private ImageButton youtubeImageView;
    private Intent intent;
    private Official official;
    private LinearLayout l1,l2,l21,l3;
    private ImageView partyicon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        locationTextView = (TextView) findViewById(R.id.oa_location);
        officeTextView = (TextView) findViewById(R.id.oa_office);
        nameTextView = (TextView) findViewById(R.id.oa_name);
        partyTextView = (TextView) findViewById(R.id.oa_party);
        addressTextView = (TextView) findViewById(R.id.oa_address);
        phoneTextView = (TextView) findViewById(R.id.oa_phone);
        emailTextView = (TextView) findViewById(R.id.oa_email);
        webTextView = (TextView) findViewById(R.id.oa_website);
        photoImageView = (ImageButton) findViewById(R.id.oa_photo);
        partyicon =  findViewById(R.id.partyicon);
        l1 =  findViewById(R.id.l1);
        l2 =  findViewById(R.id.l2);
        l21 =  findViewById(R.id.l21);
        l3 =  findViewById(R.id.l3);

        facebookImageView = (ImageButton) findViewById(R.id.facebook);
        twitterImageView = (ImageButton) findViewById(R.id.twitter);
        youtubeImageView = (ImageButton) findViewById(R.id.youtube);

        intent = getIntent();
        locationTextView.setText(intent.getCharSequenceExtra("location"));
        official = (Official) intent.getSerializableExtra("official");
        officeTextView.setText(official.getOfficeName());
        nameTextView.setText(official.getName());
        if (official.getParty() != null) {
            if (official.getParty().equals("Republican Party")){
                getWindow().getDecorView().setBackgroundColor(Color.RED);
                partyTextView.setText('('+official.getParty()+')');
                partyicon.setImageResource(R.drawable.rep_logo);
            }
            else if (official.getParty().equals("Democratic Party")) {
                partyTextView.setText('('+official.getParty()+')');
                getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                partyicon.setImageResource(R.drawable.dem_logo);
            }
            else getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        } else getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        if (official.getAddress() != null) addressTextView.setText(official.getAddress());
        else
            l1.setVisibility(View.GONE);

        if (official.getPhone() != null) phoneTextView.setText(official.getPhone());
        else
            l2.setVisibility(View.GONE);
        if (official.getEmail() != null) emailTextView.setText(official.getEmail());
        else
            l21.setVisibility(View.GONE);
        if (official.getUrl() != null) webTextView.setText(official.getUrl());
        else
            l3.setVisibility(View.GONE);

        if (official.getPhotoUrl() != null){
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                    final String changedUrl = official.getPhotoUrl().replace("http:", "https:");
                    picasso.load(changedUrl) .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder) .into(photoImageView);
                }
            }).build();

            picasso.load(official.getPhotoUrl()) .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder) .into(photoImageView);
        } else {
            Picasso.get().load(official.getPhotoUrl()).error(R.drawable.brokenimage).placeholder(R.drawable.missing).into(photoImageView);
        }

        if (official.getChannel() == null) {
            facebookImageView.setVisibility(View.INVISIBLE);
            youtubeImageView.setVisibility(View.INVISIBLE);
            twitterImageView.setVisibility(View.INVISIBLE);
        }else{
            if (official.getChannel().getFacebookId() == null)
                facebookImageView.setVisibility(View.INVISIBLE);
            if (official.getChannel().getYoutubeId() == null)
                youtubeImageView.setVisibility(View.INVISIBLE);
            if (official.getChannel().getTwitterId() == null)
                twitterImageView.setVisibility(View.INVISIBLE);
        }
    }

    public void photoClick(View v){
        if (official.getPhotoUrl() == null){
            return;
        }
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("official", official);
        intent.putExtra("location", locationTextView.getText());
        Log.d("before go to pa", locationTextView.getText().toString());
        startActivityForResult(intent, 1);
    }

    public void youtubeClick(View v) {
        String name = official.getChannel().getYoutubeId();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + name)));
        }
    }

    public void facebookClick(View v){
        String FACEBOOK_URL = "https://www.facebook.com/" + official.getChannel().getFacebookId();
        String urlToUse;
        PackageManager packageManager = getPackageManager(); try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode; if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL; } else { //older versions of fb app
                urlToUse = "fb://page/" + official.getChannel().getFacebookId(); }
        } catch (PackageManager.NameNotFoundException e) { urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW); facebookIntent.setData(Uri.parse(urlToUse)); startActivity(facebookIntent);
    }

    public void twitterClick(View v){
        Intent intent = null;
        String name = official.getChannel().getTwitterId();
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name)); intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
// no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name)); }
        startActivity(intent);
    }
}
