package com.megha.tatti.knowyourgovernment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mahesh.knowyourgovt.R;
import com.squareup.picasso.Picasso;


public class OfficialActivity extends AppCompatActivity implements View.OnClickListener {
    TextView locTv;
    TextView about;
    TextView address;
    TextView phoneNumbers;
    TextView emailIds;
    TextView webSites;
    ImageView officialImage;
    ImageView google;
    ImageView twitter;
    ImageView facebook;
    ImageView youtube;
    ConstraintLayout constraintLayout;
    MyGovernment myGovernment;
    final static String NO_DATA_PROVIDED="No Data Provided";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);
        locTv = (TextView) findViewById(R.id.officeLoc);
        about = (TextView) findViewById(R.id.officeInfo);
        address = (TextView) findViewById(R.id.officeActAddress);
        phoneNumbers = (TextView) findViewById(R.id.officePhneNumber);
        emailIds = (TextView) findViewById(R.id.officeActEmail);
        webSites = (TextView) findViewById(R.id.officeActWebsite);
        officialImage = (ImageView) findViewById(R.id.officeImage);
        google = (ImageView)findViewById(R.id.off_google);
        twitter = (ImageView) findViewById(R.id.off_twitter);
        facebook = (ImageView) findViewById(R.id.off_facebook);
        youtube = (ImageView) findViewById(R.id.off_youtube);
        constraintLayout = (ConstraintLayout) findViewById(R.id.officeConstraintLayout);
        officialImage.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Intent ofcActIntent = getIntent();
        this.myGovernment = (MyGovernment) ofcActIntent.getSerializableExtra(getString(R.string.SerializeGovernmentObject));
        PersonalDetails personalDetails = myGovernment.getOfficial();
        Web_Channel officialWebChannel = personalDetails.getWebChannel();
        Address localAddress  = myGovernment.getLocalAddress();

        String phoneNumbers= "";
        String emailIds="";
        String websites="";
        if(personalDetails.getPhoneNumbers() !=null &&personalDetails.getPhoneNumbers().size()>0){
            phoneNumbers = personalDetails.getPhoneNumbers().get(0);
        }else{
            phoneNumbers = NO_DATA_PROVIDED;
        }

        if(personalDetails.getEmailIds() != null && personalDetails.getEmailIds().size() > 0){
            emailIds = personalDetails.getEmailIds().get(0);
        }else{
            emailIds = NO_DATA_PROVIDED;
        }
        if(personalDetails.getUrls() != null && personalDetails.getUrls().size() > 0){
            websites = personalDetails.getUrls().get(0);
        }else{
            websites = NO_DATA_PROVIDED;
        }
        locTv.setText(localAddress.getCity()+","+localAddress.getState()+","+localAddress.getZip());

        about.setText(getAboutOfficialString(myGovernment));
        address.setText(getFormattedAddress(personalDetails.getAddress()));
        this.phoneNumbers.setText(phoneNumbers);
        this.emailIds.setText(emailIds);
        webSites.setText(websites);

        Linkify.addLinks(address, Linkify.MAP_ADDRESSES);
        Linkify.addLinks(this.phoneNumbers,Linkify.PHONE_NUMBERS);
        Linkify.addLinks(this.emailIds,Linkify.EMAIL_ADDRESSES);
        Linkify.addLinks(webSites,Linkify.WEB_URLS);

        address.setLinkTextColor(Color.parseColor("#ffffff"));
        this.phoneNumbers.setLinkTextColor(Color.parseColor("#ffffff"));
        this.emailIds.setLinkTextColor(Color.parseColor("#ffffff"));
        webSites.setLinkTextColor(Color.parseColor("#ffffff"));
        setChannel(officialWebChannel);
        loadImage(myGovernment);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookClicked(v, myGovernment);
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twitterClicked(v, myGovernment);
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googlePlusClicked(v, myGovernment);
            }
        });

        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youTubeClicked(v, myGovernment);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent clickedNoteIntent = new Intent();
                setResult(RESULT_OK, clickedNoteIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        Intent clickedNoteIntent = new Intent();
        setResult(RESULT_OK, clickedNoteIntent);
        finish();
    }
    String getFormattedAddress(Address officialAddress){
        String addressString="";
        if(officialAddress !=null){
            if(officialAddress.getLine1() != null){
                addressString = officialAddress.getLine1()+"\n";
            }
            if(officialAddress.getLine2() != null){
                addressString +=  officialAddress.getLine2()+"\n";
            }
            if(officialAddress.getLine3() != null){
                addressString += officialAddress.getLine3()+"\n";
            }
            if(officialAddress.getCity() != null){
                addressString += officialAddress.getCity()+",";
            }
            if(officialAddress.getState() !=null){
                addressString += officialAddress.getState()+" ";
            }
            if(officialAddress.getZip() !=null){
                addressString +=officialAddress.getZip();
            }
        }
        return addressString;
    }

    void setChannel(Web_Channel webChannel){
        if(webChannel !=null) {
            if (webChannel.getGoogleID() == null) {
                google.setVisibility(View.INVISIBLE);
            }
            if (webChannel.getFacebookID() == null) {
                facebook.setVisibility(View.INVISIBLE);
            }
            if (webChannel.getTwitterID() == null) {
                twitter.setVisibility(View.INVISIBLE);
            }
            if (webChannel.getYoutubeID() == null) {
                youtube.setVisibility(View.INVISIBLE);
            }
        } else{
            google.setVisibility(View.INVISIBLE);
            facebook.setVisibility(View.INVISIBLE);
            twitter.setVisibility(View.INVISIBLE);
            youtube.setVisibility(View.INVISIBLE);
        }
    }

    String getAboutOfficialString(MyGovernment government){
        String aboutString = "";
        aboutString = government.getOfficeName()
                + "\n"+government.getOfficial().getName();
        if(government.getOfficial().getPartyName()!=null && government.getOfficial().getPartyName().length() > 0){
            aboutString =aboutString + "\n("+government.getOfficial().getPartyName()+")";

            if(government.getOfficial().getPartyName().equals(PartyConstant.DEMOCRATIC)){
                int myColor = getResources().getColor(R.color.blue);
                constraintLayout.setBackgroundColor(myColor);
            }else if (government.getOfficial().getPartyName().equals(PartyConstant.REPUBLICAN)){
                int myColor = getResources().getColor(R.color.red);
                constraintLayout.setBackgroundColor(myColor);
            } else {
                int myColor = getResources().getColor(R.color.black);
                constraintLayout.setBackgroundColor(myColor);
            }
        }
        return aboutString;
    }
    @Override
    public void onClick(View v) {
        startPhotoActivity(myGovernment);
    }

    public void startPhotoActivity(MyGovernment government){
        if(government.getOfficial().getPhotoUrl()!=null){
            Intent intent = new Intent(this, PhotoActivity.class);
            intent.putExtra(getString(R.string.SerializeGovernmentObject),government);
            startActivityForResult(intent,0);}
    }

    interface PartyConstant{
        String DEMOCRATIC= "Democratic";
        String REPUBLICAN= "Republican";
    }

    public void facebookClicked(View v,MyGovernment myGovernment) {
        String FACEBOOK_URL = "https://www.facebook.com/" + myGovernment.getOfficial().getWebChannel().getFacebookID();
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                urlToUse = "facebook://facewebmodal/f?href=" + FACEBOOK_URL;
            }else{
                urlToUse = "facebook://page/" + myGovernment.getOfficial().getWebChannel().getFacebookID();;
            }
        }catch (PackageManager.NameNotFoundException e){
            urlToUse = FACEBOOK_URL;
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void twitterClicked(View v,MyGovernment government) {
        Intent intent = null;
        String name =  government.getOfficial().getWebChannel().getTwitterID();
        try {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) { // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    public void googlePlusClicked(View v, MyGovernment government) {
        Intent intent = null;
        String name = government.getOfficial().getWebChannel().getGoogleID();
        Log.e("OfficalActivity",name);
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.apps.plus");
            intent.setData(Uri.parse("com.google.android.googleplus"));

            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + name)));
        }
    }

    public void youTubeClicked(View v, MyGovernment government) {
        String name = government.getOfficial().getWebChannel().getYoutubeID();
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

    private void loadImage(final MyGovernment government){
        final String photoUrl = government.getOfficial().getPhotoUrl();
        if ( photoUrl != null) {
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    final String changedUrl = photoUrl.replace("http:", "https:");

                    picasso.load(changedUrl)
                            .fit()
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(officialImage);
                }
            }).build();

            picasso.load(photoUrl)
                    .fit()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(officialImage);

        } else {
            Picasso.with(this).load(photoUrl)
                    .fit()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.missingimage)
                    .into(officialImage);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {

            }
        }

    }
}
