package com.example.cart.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cart.Interface.ItemClickListner;
import com.example.cart.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView productName,productDescription,productPrice;
    public ImageView productImage;
    public ItemClickListner listner;

    public ProductViewHolder(@NonNull View itemView)
    {
        super(itemView);

        productName = (TextView) itemView.findViewById(R.id.product_name);
        productDescription = (TextView) itemView.findViewById(R.id.product_description);
        productPrice = (TextView) itemView.findViewById(R.id.product_price);
        productImage = (ImageView) itemView.findViewById(R.id.product_image);
    }

    public void setItemClickListner(ItemClickListner listner)
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View v)
    {
        listner.onClick(v, getAdapterPosition(),false);
    }
}
