package com.example.cart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cart.Models.Users;
import com.example.cart.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView Settings_profile_image;
    private TextView Close_settings_btn,Update_account_settings_btn, Profile_image_change_btn;
    private EditText Settings_phone_number,Settings_full_name,Settings_address;

    private Uri imageUri;
    private String myUrl = "";
    private StorageReference StorageRef,filepath;
    private String checker = "";
    private ProgressDialog progressbar;
    private String downloadImageUrl,UserPhoneOrder,UserName,UserAddress;
    private DatabaseReference DatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Settings_profile_image = (CircleImageView) findViewById(R.id.settings_profile_image);
        Close_settings_btn = (TextView) findViewById(R.id.close_settings_btn);
        Update_account_settings_btn = (TextView) findViewById(R.id.update_account_settings_btn);
        Profile_image_change_btn = (TextView) findViewById(R.id.profile_image_change_btn);
        Settings_phone_number = (EditText) findViewById(R.id.settings_phone_number);
        Settings_full_name = (EditText) findViewById(R.id.settings_full_name);
        Settings_address = (EditText) findViewById(R.id.settings_address);

        progressbar = new ProgressDialog(this);
        DatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        StorageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");


        Close_settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Update_account_settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();
                }
            }
        });

        Profile_image_change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";
                // start cropping activity for pre-acquired image saved on the device
                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);
            }
        });

        userInfoDisplay(Settings_profile_image,Settings_full_name,Settings_phone_number,Settings_address);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data != null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            Settings_profile_image.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Error, Try again !!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
            finish();
        }
    }

    private void updateOnlyUserInfo()
    {
        Validation();

        SaveUserInfoToDatabase();
    }

    private void userInfoSaved()
    {
        Validation();

        if(checker.equals("clicked"))
        {
            if(imageUri != null)
            {
                filepath = StorageRef.child(Prevalent.CurrentOnlineUser.getPhone() + ".jpg");
                final UploadTask uploadTask = filepath.putFile(imageUri);


                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String message = e.toString();
                        progressbar.dismiss();
                        Toast.makeText(SettingsActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(SettingsActivity.this, "Image uploaded successfully.", Toast.LENGTH_SHORT).show();

                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadImageUrl = uri.toString();
                                Toast.makeText(SettingsActivity.this, "Got Image url ..." , Toast.LENGTH_SHORT).show();
                                SaveUserInfoToDatabase();
                            }
                        });


                    }
                });

            }
            else
            {
                progressbar.dismiss();
                Toast.makeText(SettingsActivity.this, "Image not found.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void Validation() {

        progressbar.setTitle("Update profile");
        progressbar.setMessage("Please wait,while we are updating your profile.");
        progressbar.setCanceledOnTouchOutside(false);
        progressbar.show();

        UserName = Settings_full_name.getText().toString();
        UserPhoneOrder = Settings_phone_number.getText().toString();
        UserAddress = Settings_address.getText().toString();
        if(TextUtils.isEmpty(UserName))
        {
            Toast.makeText(this, "Full name is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(UserPhoneOrder))
        {
            Toast.makeText(this, "Phone number is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(UserAddress))
        {
            Toast.makeText(this, "Address is mandatory.", Toast.LENGTH_SHORT).show();
        }

    }




    private void SaveUserInfoToDatabase() {


        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("name", UserName);
        hashMap.put("phoneOrder", UserPhoneOrder);
        hashMap.put("address", UserAddress);
        if(downloadImageUrl != null) {
            hashMap.put("image", downloadImageUrl);
        }
        DatabaseRef.child(Prevalent.CurrentOnlineUser.getPhone()).updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {

                            DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Users");
                            productRef.child(Prevalent.CurrentOnlineUser.getPhone()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists())
                                    {
                                        Users users = snapshot.getValue(Users.class);
                                        Prevalent.CurrentOnlineUser = users;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                            progressbar.dismiss();
                            Toast.makeText(SettingsActivity.this, "Profile info updated.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            String message = task.getException().toString();
                            progressbar.dismiss();
                            Toast.makeText(SettingsActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }


    private void userInfoDisplay(final CircleImageView settings_profile_image, final EditText settings_full_name, final EditText settings_phone_number, final EditText settings_address)
    {
        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.CurrentOnlineUser.getPhone());
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.child("image").exists())
                    {
                        String image = snapshot.child("image").getValue().toString();
                        String name = snapshot.child("name").getValue().toString();
                        String phone = snapshot.child("phone").getValue().toString();
                        String address = snapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(settings_profile_image);
                        settings_full_name.setText(name);
                        settings_phone_number.setText(phone);
                        settings_address.setText(address);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}