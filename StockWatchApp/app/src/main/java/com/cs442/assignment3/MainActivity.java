package com.cs442.assignment3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static android.graphics.Color.BLACK;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private HashMap<String,String> allStocks=new HashMap<>();
    private ArrayList<String[]> selectedStockSymbol=new ArrayList<>();
    private final ArrayList<Stock> stocks=new ArrayList<>();


    private RecyclerView recyclerView;
    StockAdapter mAdapter;
    Stock stock;
    int pos;
    private DatabaseHandler databaseHandler;

    private SwipeRefreshLayout swiper; // The SwipeRefreshLayout

    //test download
    // TextView testText;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();

        if (bar != null)
            bar.setBackgroundDrawable(new ColorDrawable(BLACK));

       setTitleColor(Color.WHITE);
     //   testText=findViewById(R.id.textView);
        databaseHandler = new DatabaseHandler(this);

        new Thread(new NameDownloader(this,0)).start();//calls setAllStocks method
        Log.d(TAG, "onCreate: "+stocks.toString());
        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });
        recyclerView = findViewById(R.id.recycler);
        // Data to recyclerview adapter
        mAdapter = new StockAdapter(stocks, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d(TAG, "onCreate: "+stocks.toString());

    }

    public void updateAllStocks(int code){
        new Thread(new NameDownloader(this,code)).start();
    }
    public void isDuplicate(String symbol){
        boolean isDuplicate=false;
        selectedStockSymbol.clear();
        selectedStockSymbol.addAll(databaseHandler.loadStocks());
        for(String[] stock: selectedStockSymbol ){
            if(stock[0].equalsIgnoreCase(symbol)){
                isDuplicate=true;
                break;
            }
        }

        if(!isDuplicate){
            insertDB(symbol);
            downloadStock(symbol);
        }
        else {
            showDupDialog(symbol);
        }

    }

    public void insertDB(String symbol){
        databaseHandler.addStock(symbol,allStocks.get(symbol));
    }

    private void doRefresh() {
        if(doNetCheck()){
        recycleStocks(1);}
        else {
             showNetworkDialog(2);
        }
        swiper.setRefreshing(false);
     //   Toast.makeText(this, "List content refreshed", Toast.LENGTH_SHORT).show();
    }

    public void addToStocks(Stock stock){
        if(stock!=null)
            if(stock.getCompanyName().isEmpty())
        stock.setCompanyName(allStocks.get(stock.getSymbol()));
            //todo:check dulicate,then only execute below codes else show duplicate dialog
        stocks.add(stock);
        //todo: sort the stock list
        Collections.sort(stocks);
        mAdapter.notifyDataSetChanged();

    }

    public ArrayList<String[]> loadStocksFromDB(){
        //TODO: return stocks from DB to selectedStockSymbol
        return databaseHandler.loadStocks();
    }

    public void recycleStocks(int i){
        //TODO: check network and bifurcate the flow
        if(doNetCheck()){
            //todo: go with stockdownloader flow
            //TODO: Load from DB and store in temporary list
            selectedStockSymbol.clear();
            selectedStockSymbol.addAll(loadStocksFromDB());
            stocks.clear();
             for (String[] stk:selectedStockSymbol){

                 //updates stocks arraylist
                 new Thread(new StockDownloader(this,stk[0])).start();

             }
        }else {
            selectedStockSymbol.clear();
            selectedStockSymbol.addAll(loadStocksFromDB());
            stocks.clear();
            for (String[] stk:selectedStockSymbol){
                    stocks.add(new Stock(stk[0],stk[1],0.00,0.00,0.00));
            }

        }

    }


    public void setAllStocks(HashMap<String, String> allStocks) {
        Log.d(TAG, "setAllStocks: called");
        this.allStocks = allStocks;
        //todo:remove test code
        //testText.setText(allStocks.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //TODO: Show  dialog-box to add symbols to recycler.Create various dialog boxes
        //TODO: check if network conn is there-->if yes show stock selection dialog box else show network dialog
        if(!doNetCheck()){
            showNetworkDialog(1);
        }
        else {
            //todo: show selection dialog
            if(allStocks.isEmpty()){
                updateAllStocks(1);
            }
            else {
                showSelectionDialog();
            }
        }


        return true;
    }

    //TODO: All the dialogs

    //returns false if network check fails or not connected to internet
    private boolean doNetCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
           return true;
        } else {
            return false;
        }
    }

    //TODO: 1:called from menu 2: called from swipe
    public void showNetworkDialog(int id){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");

        switch (id)
        {
            case 1:
                builder.setMessage("Stocks cannot be added without a Network Connection");
                break;
            case 2:
                builder.setMessage("Stocks cannot be updated without a Network Connection");
                break;
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public  void  showNotFoundDialog(String symbol){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Symbol Not Found: "+symbol);
        builder.setMessage("No data for Stock Symbol/Name");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public  void  showDupDialog(String symbol){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Duplicate Stock");
        builder.setIcon(R.drawable.baseline_warning_black_48);
        builder.setMessage("Stock Symbol "+symbol+" is already \ndisplayed");
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void searchStock(String keyword){
        new Thread(new StockSearcher(allStocks,this,keyword)).start();
    }

    public void showSelectionDialog(){
        // Single input value dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create an edittext and set it to be the builder's view
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            String search=et.getText().toString();
                if(!search.isEmpty()){
                 //TODO: call search logic function
                    searchStock(search);
            }
                else {
                   /* Toast.makeText(MainActivity.this, "Please enter a value", Toast.LENGTH_LONG).show();
                    showSelectionDialog();*/
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.setMessage("Please enter a Stock Symbol:");
        builder.setTitle("Stock selection");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void downloadStock(String symbol){
        new Thread(new StockDownloader(this,symbol)).start();
    }

    public void showListSelectionDialog( TreeMap<String,String> list){

        // List selection dialog
        Log.d(TAG, "showListSelectionDialog: preparing list");
        Log.d(TAG, "showListSelectionDialog: preparing list :"+ list.toString());
        //ake an array of strings
        final CharSequence[] sArray = new CharSequence[list.size()];
        int i=0;
        for(Map.Entry<String,String> entry: list.entrySet()){
            sArray[i]=entry.getKey()+" - "+entry.getValue();
            i++;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a selection");

        // Set the builder to display the string array as a selectable
        // list, and add the "onClick" for when a selection is made
        builder.setItems(sArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                isDuplicate(sArray[which].toString().split(" -")[0]);
            }
        });

        builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();

    }

    public void showDeleteDialog(final int position){
        // Simple Ok & Cancel dialog - no view used.

        final String symbol=stocks.get(position).getSymbol();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.baseline_delete_black_48);

        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //todo: remove from db and list
                databaseHandler.deleteStock(symbol);
                stocks.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //todo: dismiss
            }
        });

        builder.setMessage("Delete Stock Symbol "+symbol+"?");
        builder.setTitle("Delete Stock");

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    @Override
    public void onClick(View v) {
        pos = recyclerView.getChildLayoutPosition(v);
        String symbol=stocks.get(pos).getSymbol();
        String url = "https://www.marketwatch.com/investing/stock/" + symbol;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        databaseHandler.shutDown();
        super.onDestroy();
    }

    @Override
    public boolean onLongClick(View v) {
        pos = recyclerView.getChildLayoutPosition(v);
        showDeleteDialog(pos);
        return true;
    }
}