package com.example.income_and_expenses;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Fragment {

    private TextView remaining;
    private EditText card;
    public Context ctx ;
    public String readCard;

    public String scaned;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_main, container, false);


        remaining = view.findViewById(R.id.balance);
        card = view.findViewById(R.id.card);

         ctx = view.getContext();
         readCard = getArguments().getString("card");
         card.setText(readCard);


//        card.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                startActivityForResult(new Intent(getApplicationContext(), Scan_Card.class), 1);
////            }
////        });

        card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                fetch_wallet(card.getText().toString().trim());
                return true;
            }
        });

//        inc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(view.getContext(),Income_view.class);
//                intent.putExtra("card", card.getText().toString());
//
//                startActivity(intent); // aha si ukujyana content ziri muri editText ya card kuri income vieew
//
//                Toast.makeText(view.getContext(), "you clicked " + inc.getText().toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        expenses.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // copy content of card EditView to expense_view whenever he clicks expense button
//                Intent expensesIntent = new Intent(view.getContext(),Expense_view.class);
//                expensesIntent.putExtra("card",card.getText().toString());
//                startActivity(expensesIntent);
//                Toast.makeText(view.getContext(), "you clicked " + expenses.getText().toString(), Toast.LENGTH_SHORT).show();
//
//            }
//        }
//        );
    return view;
    }


    public void onResume() {
        super.onResume();
        fetch_wallet(card.getText().toString());
    }

    void fetch_wallet(String cardid) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url = "http://192.168.43.242/www/project/access_method/wallet_access.php?category=getWallet&id=" + cardid;
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
                            JSONArray wallet = new JSONArray(response);
                            if (wallet.length() == 0)
                                remaining.setText("0 rwf");//if is empty array set to 0
                            JSONObject walletobj = wallet.getJSONObject(0);
                            int current_balance = walletobj.getInt("balance");
                            remaining.setText(current_balance + "rwf");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ctx, "error " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


}
