package com.example.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cart.Models.Products;
import com.example.cart.Prevalent.Prevalent;
import com.example.cart.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DatabaseReference productsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        Paper.init(this);



        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);



        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentCart = new Intent(HomeActivity.this,CartActivity.class);
                startActivity(intentCart);
            }
        });




        DrawerLayout drawer = findViewById(R.id.drawer_layout);



        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView userProfileName = headerView.findViewById(R.id.user_profile_name);
        CircleImageView userProfileImage = headerView.findViewById(R.id.user_profile_image);
        if(Prevalent.CurrentOnlineUser != null) {
            if(Prevalent.CurrentOnlineUser.getName() != null) {
                userProfileName.setText(Prevalent.CurrentOnlineUser.getName());
            }
            else
            {
                userProfileName.setText(Prevalent.CurrentOnlineUser.getUsername());
            }
            Picasso.get().load(Prevalent.CurrentOnlineUser.getImage()).placeholder(R.drawable.ic_baseline_account_circle_24).into(userProfileImage);
        }

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_cart, R.id.nav_orders,R.id.nav_categories,R.id.nav_settings,R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                int menuId = destination.getId();
                switch (menuId){
                    case R.id.nav_cart:
                        fab.hide();
                        Intent intentCart = new Intent(HomeActivity.this,CartActivity.class);
                        startActivity(intentCart);
                        break;
                    case R.id.nav_orders:
                        Toast.makeText(HomeActivity.this, "open orders", Toast.LENGTH_SHORT).show();
                        fab.hide();
                        break;
                    case R.id.nav_categories:
                        Toast.makeText(HomeActivity.this, "open categories", Toast.LENGTH_SHORT).show();
                        fab.hide();
                        break;
                    case R.id.nav_settings:
                        fab.hide();

                        Intent intentSettings = new Intent(HomeActivity.this, SettingsActivity.class);
                        startActivity(intentSettings);

                        break;
                    case R.id.nav_logout:
                        fab.hide();

                        Paper.book().destroy();
                        Toast.makeText(HomeActivity.this, "Logout successful", Toast.LENGTH_LONG).show();
                        Intent intentLogout = new Intent(HomeActivity.this, MainActivity.class);
                        intentLogout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intentLogout);
                        finish();
                        break;
                    default:
                        fab.show();
                        break;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(productsRef, Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model)
                    {
                        holder.productName.setText(model.getPname());
                        holder.productDescription.setText(model.getDescription());
                        holder.productPrice.setText("Price : " + model.getPrice() + " Rs");
                        Picasso.get().load(model.getImage()).into(holder.productImage);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(HomeActivity.this,ProductDetailsActivity.class);
                                intent.putExtra("pid", model.getPid());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
                       ProductViewHolder holder = new ProductViewHolder(view);
                       return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}