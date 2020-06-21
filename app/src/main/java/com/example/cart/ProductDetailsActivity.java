package com.example.cart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cart.Models.Products;
import com.example.cart.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private Button Count_btn_inst,Count_btn_desc;
    private TextView Product_details_count;
    private int ProductCount = 1;

    private FloatingActionButton Add_product_to_cart;
    private ImageView Product_details_image;
    private TextView Product_details_price,Product_details_name,Product_details_description;
    private  String productId = "",SaveCurrentDate,SaveCurrentTime,ProductName,ProductPrice,ProductDescription;
    private  DatabaseReference cartListRef;
    private ProgressDialog progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Product_details_price = (TextView) findViewById(R.id.product_details_price);
        Product_details_name = (TextView) findViewById(R.id.product_details_name);
        Product_details_description = (TextView) findViewById(R.id.product_details_description);
        Product_details_image = (ImageView) findViewById(R.id.product_details_image);
        Add_product_to_cart = (FloatingActionButton) findViewById(R.id.add_product_to_cart);

        Count_btn_inst = (Button) findViewById(R.id.count_btn_inst);
        Count_btn_desc = (Button) findViewById(R.id.count_btn_desc);
        Product_details_count = (TextView) findViewById(R.id.product_details_count);
        ProductCount = Integer.parseInt(Product_details_count.getText().toString());
        ProductCount();



        productId = getIntent().getStringExtra("pid");
        progressbar = new ProgressDialog(this);

        getProductDetails(productId);

        cartListRef = FirebaseDatabase.getInstance().getReference().child("CartLists");

        Add_product_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddingToCartList();
            }
        });

    }



    private void ProductCount() {
        Count_btn_inst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductCount = Integer.parseInt(Product_details_count.getText().toString());
                if(ProductCount < 10)
                {
                    ProductCount = ProductCount + 1;
                    Product_details_count.setText(String.valueOf(ProductCount));
                }
            }
        });

        Count_btn_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ProductCount >= 1)
                {
                    ProductCount = ProductCount - 1;
                    Product_details_count.setText(String.valueOf(ProductCount));
                }

            }
        });

    }

    private void getProductDetails(String productId)
    {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Products products = snapshot.getValue(Products.class);

                    ProductName = products.getPname();
                    ProductDescription = products.getDescription();
                    ProductPrice = products.getPrice();

                    Product_details_name.setText(ProductName);
                    Product_details_description.setText(ProductDescription);
                    Product_details_price.setText("Price : " + ProductPrice + " Rs");
                    Picasso.get().load(products.getImage()).into(Product_details_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void AddingToCartList()
    {
        progressbar.setTitle("Add product to Cart");
        progressbar.setMessage("Please wait,We are adding product to cart.");
        progressbar.setCanceledOnTouchOutside(false);
        progressbar.show();

        Calendar calender = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd yyyy");
        SaveCurrentDate = currentDate.format(calender.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        SaveCurrentTime = currentTime.format(calender.getTime());


        final HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("pid", productId);
        hashMap.put("pname", ProductName);
        hashMap.put("price", ProductPrice);
        hashMap.put("date", SaveCurrentDate);
        hashMap.put("time", SaveCurrentTime);
        hashMap.put("quantity", Product_details_count.getText().toString());
        hashMap.put("discount", "");

        cartListRef.child("UserView").child(Prevalent.CurrentOnlineUser.getPhone())
                .child("Products")
                .child(productId)
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {

                            cartListRef.child("AdminView").child(Prevalent.CurrentOnlineUser.getPhone())
                                    .child("Products")
                                    .child(productId)
                                    .updateChildren(hashMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {

                                                progressbar.dismiss();
                                                Toast.makeText(ProductDetailsActivity.this, "Product is added to Cart successfully.", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                            }
                                            else
                                            {
                                                String message = task.getException().toString();
                                                progressbar.dismiss();
                                                Toast.makeText(ProductDetailsActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();


                                            }
                                        }
                                    });
                        }
                        else
                        {
                            String message = task.getException().toString();
                            progressbar.dismiss();
                            Toast.makeText(ProductDetailsActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

}