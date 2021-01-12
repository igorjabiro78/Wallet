package com.example.income_and_expenses;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

public class Expenses_form extends AppCompatActivity {
    private EditText qty, pri;
    private Spinner exp;
    private Button saveExpense;
    private EditText expensecard;
    private Intent intent_exp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_form);

        exp = findViewById(R.id.spinner);
        qty = findViewById(R.id.qty);
        pri = findViewById(R.id.p);
        expensecard = findViewById(R.id.expensecard);
        saveExpense = findViewById(R.id.SaveExpenses);

        intent_exp = getIntent();

        expensecard.setText(intent_exp.getStringExtra("card1"));

        expensecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(),Scan_Card.class),1);
            }
        });

        saveExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qty.getText().toString().trim().isEmpty() || pri.getText().toString().trim().isEmpty() || Integer.parseInt(qty.getText().toString()) == 0 || Integer.parseInt(pri.getText().toString()) == 0) {
                    Toast.makeText(getApplicationContext(), "all inputs are required", Toast.LENGTH_LONG).show();
                } else {
                    saveExp(expensecard.getText().toString(),exp.getSelectedItem().toString(), qty.getText().toString(), Integer.parseInt(pri.getText().toString()));


                    qty.setText("");
                    pri.setText("");
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
                expensecard.setText(result); // sttore value of card in card Id edit text


            }

        }
    }


    void saveExp(String card ,String sp, String quantity, int pric) {

        RequestQueue queue = Volley.newRequestQueue(Expenses_form.this);
        String url = "http://192.168.43.242/www/project/access_method/expenses_access.php?category=insertExpenses&cardid=" + card +"&name=" + sp + "&quantity=" + quantity + "&price=" + pric;
        Log.d("Req", url);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Snackbar.make(saveExpense, response, Snackbar.LENGTH_SHORT).show();
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
