package com.example.tipsplit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText billTotal;
   // private TextView tipPercent;
    private TextView tipAmount;
    private TextView totalWithTip;
    private EditText noOfPeople;
    private Button go_b;
    private TextView totalPerPerson;
    private TextView overage;
    private static final String TAG = "MainActivityTag";
    private double bt;
    private String bts;
    private double tipPer;
    private double tipa;
    private double twtip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        billTotal = findViewById(R.id.btt_et);
        tipAmount = findViewById(R.id.taa_tv);
        totalWithTip = findViewById(R.id.twta_tv);
        noOfPeople = findViewById(R.id.nop_et);
        go_b = findViewById(R.id.go);
        totalPerPerson = findViewById(R.id.tppa_tv);
        overage = findViewById(R.id.oa_tv);



    }
    public void calTaAndTwTip(View v){
         tipa = (tipPer / 100 ) * bt;
         twtip = tipa + bt;

        tipAmount.setText("$" +String.format("%.2f", tipa));
       // totalWithTip.setText("dummy");
        totalWithTip.setText("$" +String.format("%.2f", twtip));

    }
    public void onGoButtonClicked(View v){
        String n = noOfPeople.getText().toString();
        if(n.trim().isEmpty() || n == null){
            String msg = "Number of people cannot be blank";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            return ;

        }

        int nn = Integer.parseInt(n);

        if(nn == 0 ) {
            String msg = "Number of People should be greater than zero";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            return;
        }
        RadioGroup r = findViewById(R.id.radioGroup);
        String bill = billTotal.getText().toString().trim();
        int tipNotSelected = r.getCheckedRadioButtonId();
        if(nn > 0 && (bill.isEmpty() || tipNotSelected == -1)){
            String msg = bill.isEmpty() ? "Enter bill total with tax" : "Select Tip% radio button";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            return;
        } else {
            calTaAndTwTip(v);
          //  Log.d(TAG, "onGoButtonClicked: reached here");
        }
        double ovg = twtip;
        twtip = Math.round(twtip * 10.0) / 10.0;
      //  Log.d(TAG, "onGoButtonClicked: " + twtip);
        double tpP = twtip / nn;
        ovg = (tpP * nn) - ovg;
        overage.setText("$" + String.format("%.2f", ovg));
        totalPerPerson.setText("$" +String.format("%.2f", tpP));

    }
    public void onClearButtonClicked(View v){
            tipAmount.setText("");
            totalWithTip.setText("");
            totalPerPerson.setText("");
            overage.setText("");
            billTotal.setText("");
            noOfPeople.setText("");
            RadioGroup r = findViewById(R.id.radioGroup);
            r.clearCheck();
            bt = 0.00;
            bts = "";
            tipPer = 0.00;
            tipa = 0.00;
            twtip = 0.00;


    }
    public void onRadioButtonClicked(View v) {
        boolean checked = ((RadioButton) v).isChecked();
        String text = billTotal.getText().toString();
        if (checked && !text.trim().isEmpty()) {
             bts = billTotal.getText().toString();
             bt = Double.parseDouble(bts);
            switch (v.getId()){
                case R.id.radioButton1:
                    tipPer = 12;
                    calTaAndTwTip(v);
                    break;
                case R.id.radioButton2:
                    tipPer = 15;
                    calTaAndTwTip(v);
                    break;
                case R.id.radioButton3:
                    tipPer = 18;
                    calTaAndTwTip(v);
                    break;
                case R.id.radioButton4:
                    tipPer = 20;
                    calTaAndTwTip(v);
            }

        } else {
            RadioGroup r = findViewById(R.id.radioGroup);
            r.clearCheck();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("TIP AMOUNT", tipAmount.getText().toString());
        outState.putString("TOTAL WITH TIP",totalWithTip.getText().toString());
        outState.putString("TOTAL PER PERSON",totalPerPerson.getText().toString());
        outState.putString("OVERAGE",overage.getText().toString());
       // Log.d(TAG, "onSaveInstanceState: "+ "tipa:"+ tipa+ " tipPer: "+ tipPer+ " bt:"+ bt+ " twtip:"+ twtip);
        outState.putDouble("TIP AMOUNT ANSWER", tipa);
        outState.putDouble("TIP PERCENT", tipPer);
        outState.putDouble("BILL", bt);
        outState.putDouble("TOTAL WITH TIP ANSWER", twtip);
       // Log.d(TAG, "onSaveInstanceState: "+ "tipa:"+ outState.getDouble("TIP AMOUNT ANSWER")+ " tipPer: "+ outState.getDouble("TIP PERCENT")+ " bt:"+ outState.getDouble("BILL")+ " twtip:"+ outState.getDouble("TIP WITH TIP ANSWER"));
        // Call super last
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // Call super first
        super.onRestoreInstanceState(savedInstanceState);

        tipAmount.setText(savedInstanceState.getString("TIP AMOUNT"));
        totalWithTip.setText(savedInstanceState.getString("TOTAL WITH TIP"));
        totalPerPerson.setText(savedInstanceState.getString("TOTAL PER PERSON"));
        overage.setText(savedInstanceState.getString("OVERAGE"));
        bt = savedInstanceState.getDouble("BILL");
        tipa = savedInstanceState.getDouble("TIP AMOUNT ANSWER");
        tipPer = savedInstanceState.getDouble("TIP PERCENT");
        twtip = savedInstanceState.getDouble("TOTAL WITH TIP ANSWER");
      //  Log.d(TAG, "onSaveInstanceState: "+ "tipa:"+ tipa+ " tipPer: "+ tipPer+ " bt:"+ bt+ " twtip:"+ twtip +" tipamount string: " + tipAmount.getText().toString());
    }



}