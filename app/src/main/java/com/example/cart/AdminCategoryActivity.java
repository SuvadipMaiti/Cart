package com.example.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import io.paperdb.Paper;

public class AdminCategoryActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView Cat_t_shirts,Cat_mobiles,Cat_books,Cat_laptops;
    private ImageView Cat_shoess,Cat_sweathers,Cat_sports,Cat_watches;
    private Intent intent;
    private Button Admin_logout_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);


        Cat_t_shirts = (ImageView) findViewById(R.id.cat_t_shirts);
        Cat_mobiles = (ImageView) findViewById(R.id.cat_mobiles);
        Cat_books = (ImageView) findViewById(R.id.cat_books);
        Cat_laptops = (ImageView) findViewById(R.id.cat_laptops);
        Cat_shoess = (ImageView) findViewById(R.id.cat_shoess);
        Cat_sweathers = (ImageView) findViewById(R.id.cat_sweathers);
        Cat_sports = (ImageView) findViewById(R.id.cat_sports);
        Cat_watches = (ImageView) findViewById(R.id.cat_watches);

        Cat_t_shirts.setOnClickListener(this);
        Cat_mobiles.setOnClickListener(this);
        Cat_books.setOnClickListener(this);
        Cat_laptops.setOnClickListener(this);
        Cat_shoess.setOnClickListener(this);
        Cat_sweathers.setOnClickListener(this);
        Cat_sports.setOnClickListener(this);
        Cat_watches.setOnClickListener(this);

        Paper.init(this);
        Admin_logout_btn = (Button) findViewById(R.id.admin_logout_btn);
        Admin_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().destroy();
                Intent intent = new Intent(AdminCategoryActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public void onClick(View v) {
        intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
        switch (v.getId())
        {
            case R.id.cat_t_shirts:
                intent.putExtra("category","cat_t_shirts");
                startActivity(intent);
                break;
            case R.id.cat_mobiles:
                intent.putExtra("category","cat_mobiles");
                startActivity(intent);
                break;
            case R.id.cat_books:
                intent.putExtra("category","cat_books");
                startActivity(intent);
                break;
            case R.id.cat_laptops:
                intent.putExtra("category","cat_laptops");
                startActivity(intent);
                break;
            case R.id.cat_shoess:
                intent.putExtra("category","cat_shoess");
                startActivity(intent);
                break;
            case R.id.cat_sweathers:
                intent.putExtra("category","cat_sweathers");
                startActivity(intent);
                break;
            case R.id.cat_sports:
                intent.putExtra("category","cat_sports");
                startActivity(intent);
                break;
            case R.id.cat_watches:
                intent.putExtra("category","cat_watches");
                startActivity(intent);
                break;
        }
    }
}