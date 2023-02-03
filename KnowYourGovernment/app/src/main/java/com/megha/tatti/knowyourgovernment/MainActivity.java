package com.megha.tatti.knowyourgovernment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mahesh.knowyourgovt.R;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Callback<String>,View.OnClickListener {

    final String APT_KEY = "AIzaSyA0XGPKaEvTypHVx1r-GqolfK88xU-NhiM";
    final String SEARCH_TEXT = "&address=";
    String search_url ="https://www.googleapis.com/civicinfo/v2/representatives?key=";
    Downloader downloader;
    private static final String TAG="MainActivity";
    List<MyGovernment> governmentList;

    RecyclerView governmentRecycleView;
    Govt_Adapter governmentAdaptor;
    CardView governmentCardView;
    String curState;
    static final int ACTION_OFFICIAL_ACT_SHOW = 0;
    static final int NO_ITEM_CLICKED = -1;
    private static final String NO_RESP_REC="No response received.";

    private Location locator;
    static String address;
    TextView locAddressTextView;
    Address localAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downloader = new Downloader(this);
        governmentCardView = (CardView)findViewById(R.id.cardViewOfficialList);
        locAddressTextView = (TextView) findViewById(R.id.textView2);
        setCurState(State.ON_CREATE);


        if(isOnline()) {
            locator = new Location(this);
            String api_search_url = search_url + APT_KEY +SEARCH_TEXT+address;
            Log.d(TAG,"Address "+address);
            downloader.execute(api_search_url);
        }else{
            setAlert("No Network Connection", "Data cannot be fetched\n without network connection");
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        Log.d("MainActivity","Menu inflater called ");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {

            case R.id.about :
                intent = new Intent(this, App_Author.class);
                startActivity(intent);
                return true;

            case R.id.addNewStock :
                if(isOnline()) {
                    searchDialog();
                }
                else {
                    setAlert("No Network Connection", "Data cannot be accessed/loaded without an internet connection");
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void update(String result) {
        if(result==null || result.length()<50 || result.equals(NO_RESP_REC)){
            setAlert("No Result","Cannot fetch data");
            return;
        }else {
            governmentList = readData(result);
            localAddress = readLocationJsonData(result);
            Log.d(TAG," Local Address "+ localAddress);
            locAddressTextView.setText(localAddress.getCity()+","+ localAddress.getState()+","+ localAddress.getZip());
            showGovListData(governmentList);
            showGovernmentDetails();

        }
    }

    @Override
    public NetworkInfo getNetworkInfo() {
        final String DEBUG_TAG = "NetworkStatusExample";
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isWifiConn = networkInfo.isConnected();
        boolean isMobileConn = networkInfo.isConnected();
        Log.d(DEBUG_TAG, "Wifi connected: " + isWifiConn);
        Log.d(DEBUG_TAG, "Mobile connected: " + isMobileConn);

        return networkInfo;
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private List<MyGovernment> readData(String jsonData){
        List<MyGovernment> govList=null;
        try {
            InputStream inputStream = new ByteArrayInputStream(jsonData.getBytes("UTF-8"));
            Handler jasonFileHandler= new Handler(this);
            govList =  jasonFileHandler.readData(inputStream);
        }catch(Exception e){
            e.printStackTrace();
        }
        return govList;
    }

    private Address readLocationJsonData(String jsonData){
        Address address=null;
        try {
            InputStream inputStream = new ByteArrayInputStream(jsonData.getBytes("UTF-8"));
            Handler jasonFileHandler= new Handler(this);
            address =  jasonFileHandler.readAddress(inputStream);
        }catch(Exception e){
            e.printStackTrace();
        }
        return address;
    }

    public void searchDialog(){
        final Downloader downloader;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter ZipCode, City or State");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        builder.setView(input);
        locator = new Location(this);
        downloader = new Downloader(this);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String inputText = input.getText().toString();
                Log.d("MainActivity","test := "+inputText);
                setCurState(State.NEW);
                if(isOnline()) {
                    locator.setLocationManager();
                    locator.getLocation();
                    String api_search_url = search_url + APT_KEY +SEARCH_TEXT+inputText;
                    downloader.execute(api_search_url);
                }else{
                    setAlert("No Network Connection", "Data can not be fetched\n without network connection");
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void setAlert(String alertTitle, String alertMessage){
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.activity_alert_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final TextView tittle = (TextView) promptView.findViewById(R.id.alertTitle);
        final TextView message = (TextView) promptView.findViewById(R.id.alertMessage);

        tittle.setText(alertTitle);
        message.setText(alertMessage);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    void showGovListData(List<MyGovernment> governmentList){
        Iterator<MyGovernment> iterator = governmentList.iterator();
        while (iterator.hasNext()){
            Log.d(TAG," MyGovernment Data "+iterator.next());
        }
    }

    @Override
    public void onClick(View view) {
        startActivity(ACTION_OFFICIAL_ACT_SHOW,governmentRecycleView.getChildLayoutPosition(view));
    }

    public void setCurState(String curState) {
        this.curState = curState;
    }

    public void showGovernmentDetails(){
        governmentRecycleView = (RecyclerView) findViewById(R.id.GovernmentRecy);
        governmentAdaptor = new Govt_Adapter(this, governmentList);
        governmentRecycleView.setAdapter(governmentAdaptor);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        governmentRecycleView.setLayoutManager(layoutManager);
        governmentAdaptor.notifyDataSetChanged();
    }

    public void startActivity(int actionCode, int itemClickedPos){
        if(actionCode == ACTION_OFFICIAL_ACT_SHOW) {
            MyGovernment government;
            if(itemClickedPos!=NO_ITEM_CLICKED){
                government = governmentList.get(itemClickedPos);
                government.setLocalAddress(localAddress);
                Intent intent = new Intent(this, OfficialActivity.class);
                intent.putExtra(getString(R.string.SerializeGovernmentObject),government);
                startActivityForResult(intent,ACTION_OFFICIAL_ACT_SHOW);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ACTION_OFFICIAL_ACT_SHOW) {

            if (resultCode == RESULT_OK) {

            }
        }

    }
    interface State{
        String ON_CREATE="ONCREATE";
        String NEW = "NEW";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 5) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        locator.setLocationManager();
                        locator.getLocation();
                    } else {
                        Toast.makeText(this, "Location permission was denied - cannot determine address", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        locator.destroy();
        super.onDestroy();
    }
}
