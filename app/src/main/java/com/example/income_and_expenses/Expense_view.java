package com.example.income_and_expenses;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.serialport.DeviceControl;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
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

public class Expense_view extends Fragment {
    private FloatingActionButton fabexpense;
    private Button btnExpPrint;
    private RecyclerView ExpenseRecycle;
    private RecyclerView.LayoutManager layoutManager;
    public int totalExp;
    public Intent expenseintent;
    public String cardexp;
    public Context ContextCtx;
    public String expenseHistory ="Expenses history\r\n\r\n";

    //for scanned card
    public String scaned;

    //printer


    // for printer
     private DeviceControl deviceControl;
    private ReadBean mRead;
    public boolean mBconnect = false;
    public ApplicationContext context;
    public int state;
    public PrintHelper printerHelper;

    public Context ctx;
    @Override
    public   View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.activity_expense_view, container, false);
//        ContextCtx = view.getContext();

        fabexpense = view.findViewById(R.id.fabexpense);


        cardexp = getArguments().getString("card");

        btnExpPrint = view.findViewById(R.id.btnExpPrint);

        btnExpPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //initialise printer connection
                context.setObject();
                printerHelper = new PrintHelper(context);
                connect();
//                Toast.makeText(context,"Button "+expenseHistory,Toast.LENGTH_LONG).show();
                printerHelper.printPlainText(expenseHistory);
            }
        });




        //for recycle view
        ExpenseRecycle = view.findViewById(R.id.expenseRecycleView);
        layoutManager = new LinearLayoutManager(view.getContext());
        ExpenseRecycle.setLayoutManager(layoutManager);

        fetch_Data();
        //init printer
        context = (ApplicationContext) ContextCtx.getApplicationContext();
        context.setObject();
        printerHelper = new PrintHelper(context);

        fabexpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent addexpensebutton = new Intent(view.getContext(), Expenses_form.class);
                addexpensebutton.putExtra("card1", cardexp);


                startActivity(addexpensebutton); //simpunva nezac

//                startActivity(new Intent(Expense_view.this, Expenses_form.class));
//                Toast.makeText(getApplicationContext(), "button clicked", Toast.LENGTH_SHORT).show();


            }
        });
        fetch_Data();
    return view;
    }

    @Override // we use onAttach ikoreshwa kugirango dufate context yokuri activity tuvuyeho( activity fragment iriho)
    public void onAttach(Context ctx){ // holds context of activity ->ctx  ya fragment(murikigihe ni expense_view)
     super.onAttach(ctx);
         ContextCtx = ctx; // we store ctx in Contextctx so that we can use it in other activities
        context = (ApplicationContext) ContextCtx.getApplicationContext(); //override or transform Context to ApplicationContext of printer sdk


    }

    @Override
    public void onResume() {
        super.onResume();
        fetch_Data();
    }
    // fetch info

    void fetch_Data() {
        RequestQueue queue = Volley.newRequestQueue(ContextCtx);
        String url = "http://192.168.43.242/www/project/access_method/expenses_access.php?category=getData&cardid="+cardexp;
        Log.d("Req", url);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
//                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                        try {

                            JSONArray array = new JSONArray(response); // copnvert string to json array
                            Log.d("Resp",array.toString());
                            if (array.length() > 0) {
//                                String[] listExpenses = convertData(array); // convert from json to string array
                                //hold data to print
                                storeExpenseHistory(array);
                                expenseAdapter adaptExpenses = new expenseAdapter(ContextCtx, array);

                                ExpenseRecycle.setAdapter(adaptExpenses);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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

    public void storeExpenseHistory(JSONArray arrExp){
        String[] dataExpenses = new String[(arrExp.length() + 1)];
        totalExp = 0;

        try {
            for (int i = 0; i < arrExp.length(); i++) {
                JSONObject obj = arrExp.getJSONObject(i);

                String name = obj.getString("name");
                int quantity = obj.getInt("quantity");
                int Price = obj.getInt("price");

                totalExp += (Price * quantity);

                expenseHistory +=  name + ": " + quantity + " * " + Price + "rwf = " + (Price * quantity)+" rwf\r\n";
                if (i == arrExp.length() - 1)
                    dataExpenses[(i + 1)] = "\r\n Total Expenses = " + totalExp;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // convert data Expenses from json to String array
    String[] convertData(JSONArray arrExp) {
        String[] dataExpenses = new String[(arrExp.length() + 1)];
        totalExp = 0;

        try {
            for (int i = 0; i < arrExp.length(); i++) {
                JSONObject obj = arrExp.getJSONObject(i);

                String name = obj.getString("name");
                int quantity = obj.getInt("quantity");
                int Price = obj.getInt("price");

                totalExp += (Price * quantity);

                dataExpenses[i] = "name:" + name + " , quantity " + quantity + " ,PU " + Price + " total= " + (Price * quantity);
                if (i == arrExp.length() - 1)
                    dataExpenses[(i + 1)] = "Total Expenses = " + totalExp;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataExpenses;

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
                Toast.makeText(ContextCtx, "DeviceConnectionActivity connected",Toast.LENGTH_SHORT).show();

                mBconnect = true;
                //Intent intent = new Intent(LoginActivity.this, PrintModeActivity.class);
                context.setState(state);
                context.setName("RG-E48");
                //startActivity(intent);
            } else {
                Toast.makeText(ContextCtx, "DeviceConnectionActivity failed to connect",Toast.LENGTH_SHORT).show();
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
