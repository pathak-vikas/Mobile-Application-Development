package com.cs442.assignment5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Menu menu;
    private HashMap<String, ArrayList<String>> categoryList=new HashMap<>();
    private HashMap<String,String> SourceMap=new HashMap<>();
    private ArrayList<String> sourceListDisp=new ArrayList<>();
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayAdapter<String> arrayAdapter;
    private String currentSource;
    public static int screenWidth, screenHeight;
    static final String ACTION_MSG_TO_SERVICE="ACTION_MSG_TO_SERVICE";
    static final String ACTION_NEWS_STORY="ACTION_NEWS_STORY";
    static final String ACTION_NEWS_STORY_FAIL="ACTION_NEWS_STORY_FAIL";
    static final String ARTICLE_DATA="ARTICLE_DATA";
    private NewsReceiver newsReceiver;

    private List<Fragment> fragments;
    private MyPageAdapter pageAdapter;
    private ViewPager pager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        if(!doNetCheck()){
            showNetworkDialog();
        }

        Intent intent = new Intent(MainActivity.this, NewsService.class);
        startService(intent);

        //Created filter to listen to news articles and registered the newsReceiver
        IntentFilter filter1 = new IntentFilter(ACTION_NEWS_STORY);
        IntentFilter filter2 = new IntentFilter(ACTION_NEWS_STORY_FAIL);
        newsReceiver = new NewsReceiver(this);
        registerReceiver(newsReceiver, filter1);
        registerReceiver(newsReceiver, filter2);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);


        fragments = new ArrayList<>();

        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);

        // Set up the drawer item click callback method
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                      selectItem(position);
                    }
                }
        );

        // Create the drawer toggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );


        // Load the data
        if (categoryList.isEmpty()) {
            new Thread(new SourceLoader(this)).start();
        }

    }


    public void initSetup( HashMap<String, ArrayList<String>> clist ,HashMap<String,String> smap){
        categoryList.clear();
        SourceMap.clear();
        categoryList.putAll(clist);
        SourceMap.putAll(smap);
        sourceListDisp.clear();

        ArrayList<String> tempList = new ArrayList<>(categoryList.keySet());
        ArrayList<String> stempList = new ArrayList<>(smap.keySet());
        Collections.sort(stempList);
        sourceListDisp.addAll(stempList);


        //todo: handle all list
        tempList.add(0,"all");
        Collections.sort(tempList);

        for (String s : tempList)
            menu.add(s);
        Log.d(TAG, "initSetup: setting arrayadapter"+sourceListDisp.toString());
        //todo: handle drawer list
        arrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_item, sourceListDisp);
        mDrawerList.setAdapter(arrayAdapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

    }

    public void selectItem(int position){
        pager.setBackground(null);
        currentSource=sourceListDisp.get(position);

        mDrawerLayout.closeDrawer(mDrawerList);
        Intent intent = new Intent();
        intent.setAction(MainActivity.ACTION_MSG_TO_SERVICE);
        intent.putExtra("SOURCE_ID", SourceMap.get(currentSource));
        sendBroadcast(intent);
    }

    public void setArticles(ArrayList<Article> articleList) {
        setTitle(currentSource);
        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);

        fragments.clear();

        for (int i = 0; i < articleList.size(); i++) {
            fragments.add(
                    ArticleFragment.newInstance(articleList.get(i), i+1, articleList.size()));
        }

        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Important!
        if (mDrawerToggle.onOptionsItemSelected(item)) {

            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }

        sourceListDisp.clear();

            ArrayList<String> lst= categoryList.get(item.getTitle().toString());
        if(item.getTitle().toString().equalsIgnoreCase("all")){
            lst=new ArrayList<>(SourceMap.keySet());
            Collections.sort(lst);
        }
        if (lst != null) {
            sourceListDisp.addAll(lst);
        }
        arrayAdapter.notifyDataSetChanged();

        return super.onOptionsItemSelected(item);
    }

    private void stopService() {
        Intent intent = new Intent(MainActivity.this, NewsService.class);
        stopService(intent);
    }

    @Override
    protected void onDestroy() {
        // Unregister the receiver!
        unregisterReceiver(newsReceiver);

        // Stop the service
        stopService();

        super.onDestroy();
    }

    private boolean doNetCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


    public void showNetworkDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setMessage("Data cannot be loaded/accessed without an internet connection");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        MyPageAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         * @param n number of items which have been changed
         */
        void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }

    }
}
