package com.example.cart;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText Shipment_name,Shipment_number,Shipment_address,Shipment_city;
    private Button Confirm_final_order_btn;

    private String overTotalPrice = "";
    private ProgressDialog progressbar;
    private String ShippingName,ShippingPhone, ShippingAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        Shipment_name = (EditText) findViewById(R.id.shipment_name);
        Shipment_number = (EditText) findViewById(R.id.shipment_number);
        Shipment_address = (EditText) findViewById(R.id.shipment_address);
        Shipment_city = (EditText) findViewById(R.id.shipment_city);
        Confirm_final_order_btn = (Button) findViewById(R.id.confirm_final_order_btn);

        overTotalPrice = getIntent().getStringExtra("overTotalPrice");

        Confirm_final_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validation();
            }
        });

    }

    private void Validation() {

        progressbar.setTitle("Order Confirm");
        progressbar.setMessage("Please wait ... .");
        progressbar.setCanceledOnTouchOutside(false);
        progressbar.show();

        ShippingName = Shipment_name.getText().toString();
        ShippingPhone = Shipment_number.getText().toString();
        ShippingAddress = Shipment_address.getText().toString();

        if(TextUtils.isEmpty(ShippingName))
        {
            Toast.makeText(this, "Name is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(ShippingPhone))
        {
            Toast.makeText(this, "Phone number is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(ShippingAddress))
        {
            Toast.makeText(this, "Address is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            CnfirmOrder();
        }
    }

    private void CnfirmOrder() {
        
    }

}