package com.example.cart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String CategoryName, ProductName, ProductDescription , ProductPrice ,SaveCurrentDate,SaveCurrentTime, ProductRandomKey,downloadImageUrl;
    private ImageView Select_product_image;
    private EditText Product_name,Product_description,Product_price;
    private Button Add_new_product_btn;
    private static final int galleryPick = 1;
    private Uri imageUri;
    private StorageReference StorageRef,filePath;
    private DatabaseReference DatabaseRef;
    private ProgressDialog progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        StorageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        DatabaseRef = FirebaseDatabase.getInstance().getReference().child("Products");
        CategoryName = getIntent().getExtras().get("category").toString();

        progressbar = new ProgressDialog(this);


        Select_product_image = (ImageView) findViewById(R.id.select_product_image);
        Product_name = (EditText) findViewById(R.id.product_name);
        Product_description = (EditText) findViewById(R.id.product_description);
        Product_price = (EditText) findViewById(R.id.product_price);
        Add_new_product_btn = (Button) findViewById(R.id.add_new_product_btn);

        Select_product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        Add_new_product_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validation();
            }
        });


    }



    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,galleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == galleryPick && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();
            Select_product_image.setImageURI(imageUri);
        }
    }

    private void Validation() {
        ProductName = Product_name.getText().toString();
        ProductDescription = Product_description.getText().toString();
        ProductPrice = Product_price.getText().toString();
        
        if(imageUri == null)
        {
            Toast.makeText(this, "Product image is mandatory ...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(ProductName))
        {
            Toast.makeText(this, "Please write product name ...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(ProductDescription))
        {
            Toast.makeText(this, "Please write product description ...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(ProductPrice))
        {
            Toast.makeText(this, "Please write product price ...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreProductInformation();
        }
    }

    private void StoreProductInformation() {
        progressbar.setTitle("Add new product");
        progressbar.setMessage("Please wait,We are adding new product.");
        progressbar.setCanceledOnTouchOutside(false);
        progressbar.show();

        Calendar calender = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd yyyy");
        SaveCurrentDate = currentDate.format(calender.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        SaveCurrentTime = currentTime.format(calender.getTime());
        ProductRandomKey = SaveCurrentDate + SaveCurrentTime;

        filePath = StorageRef.child(imageUri.getLastPathSegment() + ProductRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                progressbar.dismiss();
                Toast.makeText(AdminAddNewProductActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Image uploaded successfully.", Toast.LENGTH_SHORT).show();

                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadImageUrl = uri.toString();
                        Toast.makeText(AdminAddNewProductActivity.this, "Got product Image url ..." , Toast.LENGTH_SHORT).show();
                        SaveProductInfoToDatabase();
                    }
                });


            }
        });
    }

    private void SaveProductInfoToDatabase() {
        HashMap<String,Object> hashmap = new HashMap<>();
        hashmap.put("pid",ProductRandomKey);
        hashmap.put("date",SaveCurrentDate);
        hashmap.put("time",SaveCurrentTime);
        hashmap.put("category",CategoryName);
        hashmap.put("pname",ProductName);
        hashmap.put("description",ProductDescription);
        hashmap.put("price",ProductPrice);
        hashmap.put("image",downloadImageUrl);

        DatabaseRef.child(ProductRandomKey).updateChildren(hashmap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {

                            progressbar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this, "Product is added successfully.", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            String message = task.getException().toString();
                            progressbar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
}