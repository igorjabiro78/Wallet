package com.example.income_and_expenses;

import android.content.Intent;
import android.os.Bundle;
import android.serialport.DeviceControl;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.speedata.libutils.ConfigUtils;
import com.speedata.libutils.ReadBean;

import java.io.IOException;
import java.util.List;

import utils.ApplicationContext;


public class Bottom_Navigation extends AppCompatActivity {
    public String readcardnum;
    public String ScannedCard;
    public Bundle passdata;

    private DeviceControl deviceControl;
    private ReadBean mRead;
    public boolean mBconnect = false;
    public ApplicationContext context;
    public int state;
    private PrintHelper printerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigator);

        Intent readcardnumber = getIntent(); // get card number from scan card
       readcardnum= readcardnumber.getStringExtra("card");





        passdata = new Bundle(); // creating bundle objects

        passdata.putString("card",readcardnum);


        BottomNavigationView bottomNavigator = findViewById(R.id.navigator);
        bottomNavigator.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()){
                    case R.id.mywallet:
                        selectedFragment = new MainActivity();
                        break;
                    case R.id.myincome:
                        selectedFragment = new Income_view(); // jya kuri income view activity
                        break;
                    case R.id.myexpense:
                        selectedFragment = new Expense_view();
                        break;

                }

                selectedFragment.setArguments(passdata); // pass data from activity to fragments

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ftransaction = fm.beginTransaction();
                ftransaction.replace(R.id.framelayout,selectedFragment).commit();
                return true;
            }
        });
        setDefaultFragment();
    }
    public void setDefaultFragment(){ // this sets default fragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ftransaction = fm.beginTransaction();
        Fragment defaultFrgment = new MainActivity();
        defaultFrgment.setArguments(passdata); // pass data from activity to fragments

        ftransaction.replace(R.id.framelayout,defaultFrgment).commit(); // sets default to first
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        startActivity(new Intent(Bottom_Navigation.this,Scan_Card.class));
    }
    public void connect() {

        // 读取配置文件
        modelJudgmen();
        if (mBconnect) {
            context.getObject().CON_CloseDevices(context.getState());

            //con.setText(R.string.button_btcon);// "连接"
            mBconnect = false;
        } else {

            System.out.println("----RG---CON_ConnectDevices");

            if (state > 0) {
                Toast.makeText(getApplicationContext(), "DeviceConnectionActivity connected",
                        Toast.LENGTH_SHORT).show();

                mBconnect = true;
                //Intent intent = new Intent(LoginActivity.this, PrintModeActivity.class);
                context.setState(state);
                context.setName("RG-E48");
                //startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "DeviceConnectionActivity failed to connect",
                        Toast.LENGTH_SHORT).show();
                mBconnect = false;
            }
        }
    }


    private void modelJudgmen() {
        mRead = ConfigUtils.readConfig(getApplicationContext());
        ReadBean.PrintBean print = mRead.getPrint();
        String powerType = print.getPowerType();
        int braut = print.getBraut();
        List<Integer> gpio = print.getGpio();
        String serialPort = print.getSerialPort();
        state = context.getObject().CON_ConnectDevices("RG-E487",
                serialPort + ":" + braut + ":1:1", 200);
        int[] intArray = new int[gpio.size()];
        for (int i = 0; i < gpio.size(); i++) {
            intArray[i] = gpio.get(i);
        }
        try {
            deviceControl = new DeviceControl(powerType,intArray);
            deviceControl.PowerOnDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
    //notes on intent
//Intent ifasha guhererekanya content kuma activity.

// from fragment to activity we use Intent;
// from activity to Fragment we use Arguments. we use setArgument on activity , and getArgument iyo ugeze kuri fragment
// if i'm on an activity i use setArgument then ninjya kuri fragment i will use getArgument. mugihe nshaka gu sharing content
//if current page is an activity we use getIntent()
// "   "      "   "   fragment we use getArgument() to get data entries ..
//
// when we are sending data to fragment, we use setArgument on fragments and intent() to send data to activity

// frag to activi we use intent
// frag to frag we use arguments
// activity to frag argument



            //bundle notes
// bundle holds data