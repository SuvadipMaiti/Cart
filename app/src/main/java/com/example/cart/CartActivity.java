package com.example.cart;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cart.Models.Carts;
import com.example.cart.Prevalent.Prevalent;
import com.example.cart.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private  RecyclerView.LayoutManager layoutManager;

    private Button Next_process_btn;
    private TextView Total_price;
    private ProgressDialog progressbar;

    private Double overTotalPrice = 0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        progressbar = new ProgressDialog(this);


        Next_process_btn = (Button) findViewById(R.id.next_process_btn);
        Total_price = (TextView) findViewById(R.id.total_price);
        recyclerView = (RecyclerView)findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        Next_process_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this,ConfirmFinalOrderActivity.class);
                intent.putExtra("overTotalPrice", String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("CartLists");

        final FirebaseRecyclerOptions<Carts> option = new FirebaseRecyclerOptions.Builder<Carts>()
                .setQuery( cartListRef.child("UserView").child(Prevalent.CurrentOnlineUser.getPhone()).child("Products"),Carts.class)
                .build();

        FirebaseRecyclerAdapter<Carts, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Carts, CartViewHolder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int i, @NonNull final Carts model)
            {
                holder.CartProductName.setText(model.getPname());
                holder.CartProductPrice.setText("Price : " + model.getPrice() + " Rs");
                holder.CartProductQuantity.setText("Quantity : " + model.getQuantity());

                Double priceDouble = Double.parseDouble(model.getPrice());
                Double quantityDouble = Double.parseDouble(model.getQuantity());
                Double oneTypeProductPrice = priceDouble * quantityDouble;
                overTotalPrice = overTotalPrice + oneTypeProductPrice;

                Total_price.setText("Total price = " + String.valueOf(overTotalPrice) + " Rs");


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Edit",
                                        "Remove"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                if(i == 0)
                                {
                                    Intent intent = new Intent(CartActivity.this,ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                                if(i == 1)
                                {
                                    progressbar.setTitle("Item removed from Cart");
                                    progressbar.setMessage("Please wait,We are removing item from cart.");
                                    progressbar.setCanceledOnTouchOutside(false);
                                    progressbar.show();

                                    cartListRef.child("UserView")
                                            .child(Prevalent.CurrentOnlineUser.getPhone()).child("Products").child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        progressbar.dismiss();
                                                        Toast.makeText(CartActivity.this, "Item removed successfully.", Toast.LENGTH_SHORT).show();

                                                        Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                                        startActivity(intent);
                                                    }
                                                    else
                                                    {
                                                        String message = task.getException().toString();
                                                        progressbar.dismiss();
                                                        Toast.makeText(CartActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();


                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent,false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}