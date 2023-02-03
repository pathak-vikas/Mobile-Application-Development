package com.cs442.assignment5;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ArticleFragment extends Fragment {


    private static final String TAG = "ArticleFragment";
    public ArticleFragment() {
    }

    static ArticleFragment newInstance(Article article, int index, int max)
    {
        ArticleFragment f = new ArticleFragment();
        Bundle bdl = new Bundle(1);
        bdl.putSerializable("ARTICLE_DATA", article);
        bdl.putSerializable("INDEX", index);
        bdl.putSerializable("TOTAL_COUNT", max);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment_layout = inflater.inflate(R.layout.fragment_main, container, false);

        Bundle args = getArguments();
        if (args != null) {
            final Article currentArticle = (Article) args.getSerializable("ARTICLE_DATA");
            if (currentArticle == null) {
                return null;
            }
            int index = args.getInt("INDEX");
            int total = args.getInt("TOTAL_COUNT");

            TextView titletv = fragment_layout.findViewById(R.id.titletv);

            if(!currentArticle.getTitle().trim().equalsIgnoreCase("null")&&!currentArticle.getTitle().isEmpty()) {
                titletv.setText(currentArticle.getTitle());
                titletv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click(currentArticle.getUrl());
                    }
                });
            }
            else {
                makeItInvisible(titletv);
            }

            TextView authortv = fragment_layout.findViewById(R.id.authourtv);
            if(!currentArticle.getAuthor().trim().equalsIgnoreCase("null")&&!currentArticle.getAuthor().isEmpty()) {
                authortv.setText(currentArticle.getAuthor());
            }
            else {
                makeItInvisible(authortv);
            }


            TextView publishtv = fragment_layout.findViewById(R.id.publishtv);
            String publishedDate =currentArticle.getDate();
            Log.d(TAG, "onCreateView: publishedDate: "+publishedDate);
            SimpleDateFormat givenFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            SimpleDateFormat reqFormat = new SimpleDateFormat("E MMM dd, HH:mm a", Locale.US);
            String goodPublishedDate="";
            try {

                 goodPublishedDate = reqFormat.format(givenFormat.parse(publishedDate));
                Log.d(TAG, "onCreateView: goodDate: "+goodPublishedDate);
                publishtv.setText(goodPublishedDate);
            } catch (ParseException e) {
                givenFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                try {
                    goodPublishedDate = reqFormat.format(givenFormat.parse(publishedDate));
                } catch (ParseException parseException) {
                    parseException.printStackTrace();

                }
                Log.d(TAG, "onCreateView: goodDate: ignore unparsable date error, already fixed in first catch block");

                publishtv.setText(goodPublishedDate);
                e.printStackTrace();
            }



            TextView desctv = fragment_layout.findViewById(R.id.desctv);
            if(!currentArticle.getDescription().trim().equalsIgnoreCase("null")&&!currentArticle.getDescription().isEmpty()) {
                desctv.setText(currentArticle.getDescription());
                desctv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click(currentArticle.getUrl());
                    }
                });
            }
            else {
                makeItInvisible(desctv);
            }
            TextView pageNum = fragment_layout.findViewById(R.id.page_num);
            pageNum.setText(String.format(Locale.US, "%d of %d", index, total));

            ImageView imageView = fragment_layout.findViewById(R.id.photoiv);

            if(!currentArticle.getPhoto().trim().equalsIgnoreCase("null")&&!currentArticle.getPhoto().isEmpty()) {
                Picasso.get().load(currentArticle.getPhoto())
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click(currentArticle.getUrl());
                    }
                });

            }else
            {
                makeItInvisible(imageView);
            }



            return fragment_layout;
        } else {
            return null;
        }
    }

    private void click(String uri) {

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(uri));
        startActivity(i);

    }

    private void makeItInvisible(ImageView channel) {
        channel.setVisibility(View.GONE);
    }

    private void makeItInvisible(TextView tv) {
        tv.setVisibility(View.GONE);
    }


}
