package com.example.income_and_expenses;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;

public class ScanCardBarcode extends AppCompatActivity {

    private static final int RC_BARCODE_CAPTURE = 2;
    public Button cardBarcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_card_barcode);

        cardBarcode = findViewById(R.id.scan);

        cardBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"button clicked ",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ScanCardBarcode.this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, false);

                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent obj) {
        super.onActivityResult(requestCode, resultCode, obj);
        if (obj != null) {
            if (requestCode == RC_BARCODE_CAPTURE) {
                if (resultCode == CommonStatusCodes.SUCCESS) {
                    Intent intent = obj;
                    String text = intent.getStringExtra("Barcode");
                    // intent for scanned card
                    Intent intent1= new Intent(ScanCardBarcode.this,Bottom_Navigation.class);
                    intent1.putExtra("card",text); // pass scanned card content to bottom navigation

                    startActivity(intent1);
                    Toast.makeText(getApplicationContext(), "Scanned " + obj.getStringExtra("Barcode"), Toast.LENGTH_LONG).show();
                }
            }

        }
    }
}
