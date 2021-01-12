package com.example.income_and_expenses;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.speedata.r6lib.IMifareManager;
import com.speedata.r6lib.R6Manager;

import java.util.Timer;
import java.util.TimerTask;

import static com.speedata.r6lib.R6Manager.CardType.MIFARE;

public class Scan_Card extends AppCompatActivity {
    private static final int RC_BARCODE_CAPTURE = 1;
    private IMifareManager dev;
    Timer timer;
    TimerTask timerTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan__card);

        dev = R6Manager.getMifareInstance(MIFARE); //set connection to our card reader
        if (dev.InitDev() != 0) {
              startActivity(new Intent(Scan_Card.this,ScanCardBarcode.class));
              finish();
            Toast.makeText(this,"Open device error!",Toast.LENGTH_LONG).show();
            return;
        }
        Log.e("card", "init ok");
        timer = new Timer();
        startTimer();






    }


    private void startTimer(){

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                String id = msg.getData().getString("id").toString();

//                setBlockValue();//load default amount

                // return card Id/data to mainActivity class and other classes
                Intent card_content = new Intent(Scan_Card.this,Bottom_Navigation.class);
                card_content.putExtra("card",id);
                startActivity(card_content);
//                setResult(Activity.RESULT_OK,card_content);
                finish();

                Toast.makeText(getApplicationContext(),"Card ID "+id,Toast.LENGTH_LONG).show();
                return false;
            }
        });

         // read card Id
        timerTask = new TimerTask() {
            @Override
            public void run() {
                byte[] ID = dev.SearchCard();
                if (ID == null) {
                    //Log.e("CARD SEARCH", "NO CARD FOUND");
                    return;
                }
                String IDString = new String("");
                for (byte a : ID) {
                    IDString += String.format("%02X", a);
                }
                Message msg = new Message(); // calls handler whenever it gets card
                Bundle data = new Bundle(); // send data to the activity
                msg.setData(data);
                Log.d("Card ID ",IDString); //these two lines?????????????????????????
                data.putString("id",IDString);
                handler.sendMessage(msg);
            }
        };
        timer.schedule(timerTask,0,1000);
    }
    private void stopTimer(){
        timer = new Timer();
        timer.purge();
        timer.cancel();
    }
    @Override
    protected void onResume(){
        super.onResume();
        timer = new Timer();
        startTimer();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        stopTimer();
    }
}
