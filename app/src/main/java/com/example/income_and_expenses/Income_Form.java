package com.example.income_and_expenses;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;


public class Income_Form extends AppCompatActivity {
    private Button saveIncome;
    private EditText cardincome;
    private Intent income_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income__form);
        final EditText from, Amnt;

        cardincome = findViewById(R.id.cardincome);
        from = findViewById(R.id.name);
        Amnt = findViewById(R.id.money);
        saveIncome = findViewById(R.id.save);

        income_intent = getIntent();

        cardincome.setText(income_intent.getStringExtra("cardincome")); // here gets the card number from income_view


        cardincome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(),Scan_Card.class),1);
            }
        });


        saveIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from.getText().toString().trim().isEmpty()|| Integer.parseInt(Amnt.getText().toString()) == 0) {
                    Toast.makeText(getApplicationContext(), "all inputs are required", Toast.LENGTH_LONG).show();
                } else {
                    saveIncome(cardincome.getText().toString(),from.getText().toString(), Integer.parseInt(Amnt.getText().toString()));

                    from.setText("");
                    Amnt.setText("");
                    cardincome.setText("");
                }
            }
        });


    }
    // get data from Scan_Card
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent card_intent){

        super.onActivityResult(requestCode,resultCode,card_intent);

        if(requestCode == 1){ // 1 is the request code in line 46
            if(resultCode == Activity.RESULT_OK){
                String result = card_intent.getStringExtra("card"); // get data from intent in Scan_Card
                cardincome.setText(result); // sttore value of card in card Id edit text


            }

        }
    }

    void saveIncome(String card,String names, int amount) {

        RequestQueue queue = Volley.newRequestQueue(Income_Form.this);
        String url = "http://192.168.43.242/www/project/access_method/income_access.php?category=insertIncome&cardid=" + card +"&name=" + names + "&amount=" + amount;
        Log.d("Req", url);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Snackbar.make(saveIncome, response, Snackbar.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "error " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);


    }

}































