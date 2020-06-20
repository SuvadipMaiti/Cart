package com.example.cart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button Create_account_btn;
    private EditText Register_username_input,Register_phone_number_input,Register_password_input;
    private ProgressDialog progressbar;
    private String DB = "Users";
    private TextView Admin_panel_link, Not_admin_panel_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Create_account_btn = (Button) findViewById(R.id.create_account_btn);

        Register_username_input = (EditText) findViewById(R.id.register_username_input);
        Register_phone_number_input = (EditText) findViewById(R.id.register_phone_number_input);
        Register_password_input = (EditText) findViewById(R.id.register_password_input);

        Admin_panel_link = (TextView) findViewById(R.id.admin_panel_link);
        Not_admin_panel_link = (TextView) findViewById(R.id.not_admin_panel_link);

        progressbar = new ProgressDialog(this);

        Create_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }

        });

        Admin_panel_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Create_account_btn.setText("Create Admin Account");
                Not_admin_panel_link.setVisibility(View.VISIBLE);
                Admin_panel_link.setVisibility(View.INVISIBLE);
                DB = "Admins";
            }
        });

        Not_admin_panel_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Create_account_btn.setText("Create Account");
                Not_admin_panel_link.setVisibility(View.INVISIBLE);
                Admin_panel_link.setVisibility(View.VISIBLE);
                DB = "Users";
            }
        });

    }

    private void createAccount() {
        String username = Register_username_input.getText().toString();
        String phone = Register_phone_number_input.getText().toString();
        String password = Register_password_input.getText().toString();

        progressbar.setTitle("Create Account ....");
        progressbar.setMessage("Please wait,We are checking all credentials.");
        progressbar.setCanceledOnTouchOutside(false);
        progressbar.show();

        Validation(username,phone,password);

    }

    private void Validation(final String username, final String phone, final String password) {

        if(TextUtils.isEmpty(username))
        {
            progressbar.dismiss();
            Toast.makeText(RegisterActivity.this, "Please Enter Username", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(phone))
        {
            progressbar.dismiss();
            Toast.makeText(RegisterActivity.this, "Please Enter phone number", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            progressbar.dismiss();
            Toast.makeText(RegisterActivity.this, "Please Enter password", Toast.LENGTH_LONG).show();
        }
        else
        {
            final DatabaseReference databaseRef;
            databaseRef = FirebaseDatabase.getInstance().getReference();

            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(!(dataSnapshot.child(DB).child(phone).exists()))
                    {
                        HashMap<String,Object> hasmap = new HashMap<>();
                        hasmap.put("username",username);
                        hasmap.put("phone",phone);
                        hasmap.put("password",password);

                        databaseRef.child(DB).child(phone).updateChildren(hasmap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(RegisterActivity.this, "Congratulations, your account successfully created.", Toast.LENGTH_LONG).show();
                                            progressbar.dismiss();
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                        else
                                        {
                                            progressbar.dismiss();
                                            Toast.makeText(RegisterActivity.this, "Network Error : Please try again after some time ...", Toast.LENGTH_LONG).show();

                                        }
                                    }
                                });

                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this, "This " + phone + " already exist.", Toast.LENGTH_LONG).show();
                        progressbar.dismiss();
                        Toast.makeText(RegisterActivity.this, "Please try with another phone number.", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
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