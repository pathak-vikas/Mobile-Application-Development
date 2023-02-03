package com.megha.tatti.knowyourgovernment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.mahesh.knowyourgovt.R;
import com.squareup.picasso.Picasso;


public class PhotoActivity extends AppCompatActivity {
    TextView about;
    ImageView imageView;
    ConstraintLayout constraintLayout;
    MyGovernment government;
    TextView locTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        about = (TextView) findViewById(R.id.aboutTv);
        imageView = (ImageView) findViewById(R.id.imageView);
        constraintLayout = (ConstraintLayout) findViewById(R.id.constraint);
        locTv = (TextView) findViewById(R.id.locTv);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Intent ofcActIntent = getIntent();
        government = (MyGovernment) ofcActIntent.getSerializableExtra(getString(R.string.SerializeGovernmentObject));
        Address localAddress = government.getLocalAddress();
        about.setText(getAboutOfficialString(government));
        locTv.setText(localAddress.getCity()+","+localAddress.getState()+","+localAddress.getZip());
        loadImage(government);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent noteIntent = new Intent();
                setResult(RESULT_OK, noteIntent);
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

    public String getAboutOfficialString(MyGovernment government){
        String aboutString = "";
        aboutString = government.getOfficeName()
                + "\n"+government.getOfficial().getName();
        if(government.getOfficial().getPartyName()!=null && government.getOfficial().getPartyName().length() > 0){
            aboutString =aboutString + "\n("+government.getOfficial().getPartyName()+")";
            if(government.getOfficial().getPartyName().equals(OfficialActivity.PartyConstant.DEMOCRATIC)){
                int myColor = getResources().getColor(R.color.blue);
                constraintLayout.setBackgroundColor(myColor);
            }else if (government.getOfficial().getPartyName().equals(OfficialActivity.PartyConstant.REPUBLICAN)){
                int myColor = getResources().getColor(R.color.red);
                constraintLayout.setBackgroundColor(myColor);
            }
            else{
                int myColor = getResources().getColor(R.color.black);
                constraintLayout.setBackgroundColor(myColor);
            }
        }
        return aboutString;
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
                            .into(imageView);
                }
            }).build();

            picasso.load(photoUrl)
                    .fit()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);

        } else {
            Picasso.with(this).load(photoUrl)
                    .fit()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
        }
    }
}
