package com.example.cart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cart.Models.Users;
import com.example.cart.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private Button Main_login_btn;
    private EditText Login_phone_number_input,Login_password_input;
    private ProgressDialog progressbar;
    private String DB = "Users";
    private CheckBox Remember_me_checkbox;
    private TextView Admin_panel_link, Not_admin_panel_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Main_login_btn = (Button) findViewById(R.id.main_login_btn);

        Login_phone_number_input = (EditText) findViewById(R.id.login_phone_number_input);
        Login_password_input = (EditText) findViewById(R.id.login_password_input);

        Admin_panel_link = (TextView) findViewById(R.id.admin_panel_link);
        Not_admin_panel_link = (TextView) findViewById(R.id.not_admin_panel_link);

        Remember_me_checkbox = (CheckBox) findViewById(R.id.remember_me_checkbox);
        Paper.init(this);

        progressbar = new ProgressDialog(this);

        Main_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        Admin_panel_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main_login_btn.setText("Admin Login");
                Not_admin_panel_link.setVisibility(View.VISIBLE);
                Admin_panel_link.setVisibility(View.INVISIBLE);
                DB = "Admins";
            }
        });

        Not_admin_panel_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main_login_btn.setText("Admin Login");
                Not_admin_panel_link.setVisibility(View.INVISIBLE);
                Admin_panel_link.setVisibility(View.VISIBLE);
                DB = "Users";
            }
        });

    }

    private void Login() {

        String phone = Login_phone_number_input.getText().toString();
        String password = Login_password_input.getText().toString();

        progressbar.setTitle("Login ....");
        progressbar.setMessage("Please wait,We are checking all credentials.");
        progressbar.setCanceledOnTouchOutside(false);
        progressbar.show();

        Validation(phone,password);
    }

    private void Validation(final String phone, final String password) {

        if(TextUtils.isEmpty(phone))
        {
            progressbar.dismiss();
            Toast.makeText(LoginActivity.this, "Please Enter phone number", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            progressbar.dismiss();
            Toast.makeText(LoginActivity.this, "Please Enter password", Toast.LENGTH_LONG).show();
        }
        else
        {

            final DatabaseReference databaseRef;
            databaseRef = FirebaseDatabase.getInstance().getReference();

            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(DB).child(phone).exists())
                    {

                        Users usersData = dataSnapshot.child(DB).child(phone).getValue(Users.class);
                        if(usersData.getPhone().equals(phone))
                        {
                            if(usersData.getPassword().equals(password))
                            {
                               if(DB.equals("Admins"))
                               {
                                   //Remember me
                                   if(Remember_me_checkbox.isChecked())
                                   {
                                       Paper.book().write(Prevalent.AdminPhoneKey,phone);
                                       Paper.book().write(Prevalent.AdminPasswordKey,password);
                                       Paper.book().write(Prevalent.DB,"Admins");
                                   }
                                   Toast.makeText(LoginActivity.this, "Welcome Admin, Logged in successfully.", Toast.LENGTH_LONG).show();
                                   progressbar.dismiss();

                                   Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                   startActivity(intent);
                               }
                               else if(DB.equals("Users"))
                               {
                                   //Remember me
                                   if(Remember_me_checkbox.isChecked())
                                   {
                                       Paper.book().write(Prevalent.UserPhoneKey,phone);
                                       Paper.book().write(Prevalent.UserPasswordKey,password);
                                       Paper.book().write(Prevalent.DB,"Users");
                                   }
                                   Prevalent.CurrentOnlineUser = usersData;
                                   Toast.makeText(LoginActivity.this, "Welcome User, Logged in successfully.", Toast.LENGTH_LONG).show();
                                   progressbar.dismiss();
                                   Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                   startActivity(intent);
                               }

                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this, "Please enter correct password.", Toast.LENGTH_LONG).show();
                                progressbar.dismiss();
                            }
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Please enter correct Phone number.", Toast.LENGTH_LONG).show();
                            progressbar.dismiss();
                        }

                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "This " + phone + " do not exist.", Toast.LENGTH_LONG).show();
                        progressbar.dismiss();
                        Toast.makeText(LoginActivity.this, "Please create a new account.", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}