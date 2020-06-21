package com.example.cart.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cart.Interface.ItemClickListner;
import com.example.cart.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView CartProductName,CartProductQuantity,CartProductPrice;
    public ItemClickListner itemClickListner;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        CartProductName = (TextView) itemView.findViewById(R.id.cart_product_name);
        CartProductPrice = (TextView) itemView.findViewById(R.id.cart_product_price);
        CartProductQuantity = (TextView) itemView.findViewById(R.id.cart_product_quantity);
    }

    public void setItemClickListner(ItemClickListner itemClickListner)
    {
        this.itemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View v) {
        itemClickListner.onClick(v, getAdapterPosition(), false);
    }
}
