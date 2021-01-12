package com.example.income_and_expenses;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.serialport.DeviceControl;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.speedata.libutils.ConfigUtils;
import com.speedata.libutils.ReadBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import utils.ApplicationContext;

public class Income_view extends Fragment {
    //private Button addIncome;
    private RecyclerView incomeRecycle;
    private RecyclerView.LayoutManager layoutManager;
    public int total;
    public Intent intent;
    public String card;
    public String cardBarcode;
    public FloatingActionButton fab;
    public Context ctx;
    public Button printbutton;
    public String incomeHistory = "Income History \r\n\r\n";



    //printer
    private DeviceControl deviceControl;
    private ReadBean mRead;
    public boolean mBconnect = false;
    public ApplicationContext context;
    public int state;
    private PrintHelper printerHelper;

    public Context ContextCtx;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.activity_income_view, container, false);

        ctx = view.getContext(); // iyi ifata context of fragment

        // get all the intent or content from scan_Card intent

        printbutton = view.findViewById(R.id.printIncome);

        card = getArguments().getString("card"); // gets card number from main activity
//        addIncome = findViewById(R.id.addincome);



        incomeRecycle = view.findViewById(R.id.incomeRecycleView);
        layoutManager = new LinearLayoutManager(view.getContext());
        incomeRecycle.setLayoutManager(layoutManager);

        fetch_info();
         // printer



        fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // sasa ubu tugiye kohereza card number kuri income form
                Intent incomesIntent = new Intent(view.getContext(), Income_Form.class);
                incomesIntent.putExtra("cardincome",card);

                startActivity(incomesIntent);
                Toast.makeText(view.getContext(), "button clicked", Toast.LENGTH_SHORT).show();

            }
        });


        printbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.setObject();
                connect();
                printerHelper = new PrintHelper(context);

                printerHelper.printPlainText(incomeHistory);
            }
        });
     return view;


    }

    @Override
    public void onAttach(Context ctx){
        super.onAttach(ctx);
        ContextCtx = ctx;
        context = (ApplicationContext) ctx.getApplicationContext();

        // gatuma tubona context ya bottom navigation or activity turimo
        // ctx hols applicationcontext of bottom navigation or certain activity
        // helps us to attach application context of bottom navigation to fragment.
        // mbese tuyikoresh mugihe turi gushaka context yindi activity iyo iri onattach
    }
// fragments are used on bottom navigation, drawer(nkaya gmail), or tabs nkiza whatsapp
    // just to change the content utavuye kuri activity

    @Override
    public void onResume() {
        super.onResume();
        fetch_info();
    }

    void fetch_info() {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url = "http://192.168.43.242/www/project/access_method/income_access.php?category=getALL&cardid="+card;
        Log.d("Req", url);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
//                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        //convert string into JSONArray format
                        try {

                            JSONArray array = new JSONArray(response); // convert STRING INTO jSON ARRAY
                            if (array.length() > 0) {
                               // String[] listData = fetchnames(array);//convert Json Array into string array
                                storeIncomeHistory(array);
//
                                incomeAdapter adapterIncome = new incomeAdapter(ctx, array);
                                incomeRecycle.setAdapter(adapterIncome);
                            }
                        } catch (JSONException ex) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ContextCtx, "error " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


    // get json array data and put data in string array

    String[] storeIncomeHistory(JSONArray arr) {
        String[] data = new String[arr.length() + 1];
        try {
            total = 0;
            for (int i = 0; i < arr.length(); i++) {
                JSONObject object = arr.getJSONObject(i);
                String name = object.getString("names"),
                        amount = object.getString("amount");
                incomeHistory += "From " + name + ": " + amount+"\r\n"; // put selected json array data to string array
                data[i] = "From " + name + "\n Amount: " + amount; // put selected json array data to string array
                total += Integer.parseInt(amount);
                if (i == (arr.length() - 1)) {
                    incomeHistory += "\r\nTotal is " + total+"\r\n";
                    data[(i + 1)] = "Total is " + total;
                }

            }
        } catch (JSONException ex) {
            Log.e("JSONException", ex.getMessage());
        }
        return data;
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
                Toast.makeText(ContextCtx, "DeviceConnectionActivity connected",
                        Toast.LENGTH_SHORT).show();

                mBconnect = true;
                //Intent intent = new Intent(LoginActivity.this, PrintModeActivity.class);
                context.setState(state);
                context.setName("RG-E48");
                //startActivity(intent);
            } else {
                Toast.makeText(ContextCtx, "DeviceConnectionActivity failed to connect",
                        Toast.LENGTH_SHORT).show();
                mBconnect = false;
            }
        }
    }


    private void modelJudgmen() {
        mRead = ConfigUtils.readConfig(ContextCtx.getApplicationContext());
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
