
package com.cs442.assignment3;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;


    private static final String DATABASE_NAME = "StockAppDB";
    private static final String TABLE_NAME = "StockWatchTable";
    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";

    // DB Table Create Code
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique," +
                    COMPANY + " TEXT not null)";

    private SQLiteDatabase database;


    DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase(); // Inherited from SQLiteOpenHelper
        Log.d(TAG, "DatabaseHandler: C'tor DONE");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // onCreate is only called is the DB does not exist
        Log.d(TAG, "onCreate: Making New DB");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

   public ArrayList<String[]> loadStocks() {
       ArrayList<String[]> stocks = new ArrayList<>();
       Cursor cursor = database.query( TABLE_NAME, // The table to query
               new String[]{SYMBOL, COMPANY},// The columns to return
               null, // The columns for the WHERE clause
               null, // The values for the WHERE clause
               null, // don't group the rows
               null, // don't filter by row groups
                SYMBOL+" ASC"); // The sort order
       if (cursor != null) {
           cursor.moveToFirst();
           for (int i = 0; i < cursor.getCount(); i++)
           {
               String symbol = cursor.getString(0);
               String company = cursor.getString(1);
               stocks.add(new String[]{symbol, company});
               cursor.moveToNext(); }
           cursor.close();
       }
       return stocks;
   }

    public void addStock(String symbol,String company) {
       Log.d(TAG, "addStock: Adding " + symbol);
     ContentValues values = new ContentValues();
     values.put(SYMBOL, symbol);
     values.put(COMPANY, company);
     database.insert(TABLE_NAME, null, values);
     Log.d(TAG, "addStock: Add Complete");
   }

    public void deleteStock(String symbol) {

        Log.d(TAG, "deleteStock: Deleting Stock " + symbol);
        int cnt = database.delete( TABLE_NAME, SYMBOL+" = ?", new String[] { symbol });
        Log.d(TAG, "deleteStock: " + cnt);
    }


    //todo:write dumplog
   /* void dumpDbToLog() {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();

            Log.d(TAG, "dumpDbToLog: vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
            for (int i = 0; i < cursor.getCount(); i++) {
                String country = cursor.getString(0);
                String region = cursor.getString(1);
                String subRegion = cursor.getString(2);
                String capital = cursor.getString(3);
                int population = cursor.getInt(4);
                Log.d(TAG, "dumpDbToLog: " +
                        String.format("%s %-18s", COUNTRY + ":", country) +
                        String.format("%s %-18s", REGION + ":", region) +
                        String.format("%s %-18s", SUBREGION + ":", subRegion) +
                        String.format("%s %-18s", CAPITAL + ":", capital) +
                        String.format("%s %-18s", POPULATION + ":", population));
                cursor.moveToNext();
            }
            cursor.close();
        }

        Log.d(TAG, "dumpDbToLog: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }*/

    void shutDown() {
        database.close();
    }
}
