package com.example.cart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cart.Models.Admins;
import com.example.cart.Models.Users;
import com.example.cart.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button Main_login_btn,Main_join_now_btn;
    private ProgressDialog progressbar;
    private String DB = "Users";
    private String PhoneKey,PasswordKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Main_login_btn = (Button) findViewById(R.id.main_login_btn);
        Main_join_now_btn = (Button) findViewById(R.id.main_join_now_btn);

        progressbar = new ProgressDialog(this);
        Paper.init(this);

        Main_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        Main_join_now_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        DB = Paper.book().read(Prevalent.DB);
        if(DB == null)
        {
            DB = "Users";
        }
        if(DB.equals("Admins"))
        {
            PhoneKey = Paper.book().read(Prevalent.AdminPhoneKey);
            PasswordKey = Paper.book().read(Prevalent.AdminPasswordKey);
        }
        else if(DB.equals("Users")) {
             PhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
             PasswordKey = Paper.book().read(Prevalent.UserPasswordKey);
        }

        if(PhoneKey !="" && PasswordKey !="") {
            if (!TextUtils.isEmpty(PhoneKey) && !TextUtils.isEmpty(PasswordKey)) {

                progressbar.setTitle("Please wait....");
                progressbar.setMessage("Checking, already logged in or not....");
                progressbar.setCanceledOnTouchOutside(false);
                progressbar.show();

                AllowAccess(PhoneKey,PasswordKey);

            }
        }


    }

    private void AllowAccess(final String phone, final String password) {

        final DatabaseReference databaseRef;
        databaseRef = FirebaseDatabase.getInstance().getReference();

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(dataSnapshot.child(DB).child(phone).exists())
                {
                    if(DB.equals("Admins"))
                    {
                        Admins adminData = dataSnapshot.child(DB).child(phone).getValue(Admins.class);
                        if(adminData.getPhone().equals(phone))
                        {
                            if(adminData.getPassword().equals(password))
                            {
                                Toast.makeText(MainActivity.this, "You are already logged in.", Toast.LENGTH_LONG).show();
                                progressbar.dismiss();

                                Intent intent = new Intent(MainActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                            }

                        }
                    }
                    else if(DB.equals("Users"))
                    {
                        Users usersData = dataSnapshot.child(DB).child(phone).getValue(Users.class);
                        if(usersData.getPhone().equals(phone))
                        {
                            if(usersData.getPassword().equals(password))
                            {
                                Prevalent.CurrentOnlineUser = usersData;
                                Toast.makeText(MainActivity.this, "You are already logged in.", Toast.LENGTH_LONG).show();
                                progressbar.dismiss();

                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }

                        }
                    }
                }
                progressbar.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}