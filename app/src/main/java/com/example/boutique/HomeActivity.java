package com.example.boutique;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.boutique.model.Product;
import com.example.boutique.model.prevalent.Prevalent;
import com.example.boutique.viewHolder.ProductViewHolder;
import com.example.boutique.viewHolder.SliderAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ProgressDialog loadingBar;
    private boolean doubleBackToExitPressedOnce = false;
    private DatabaseReference productRef;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView searchActivity_button;
    private SliderView sliderView;
    @SuppressLint({"ResourceType", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        searchActivity_button = (ImageView) findViewById(R.id.searchActivity_button);
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        loadingBar = new ProgressDialog(this);
        Paper.init(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic__menu);
        toolbar.getNavigationIcon();
        FloatingActionButton fab = findViewById(R.id.fab);
        searchActivity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, SearchForProductActivity.class));
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new  Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        /*NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);*/
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if(id == R.id.nav_cart){
                    Intent intent = new  Intent(HomeActivity.this, CartActivity.class);
                    startActivity(intent);
                    return true;
                }else if(id == R.id.nav_categories){
                    startActivity(new Intent(HomeActivity.this, CategoriesActivity.class));
                    return true;
                }else if(id == R.id.nav_setting){
                    startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                    return true;
                }else if(id == R.id.nav_logout){
                    Paper.book().destroy();
                    loadingBar.setTitle("Déconnexion..");
                    loadingBar.setMessage("Veuillez patienter...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                NavigationUI.onNavDestinationSelected(item, navController);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);


        View headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.nav_username);
        TextView userEmail = headerView.findViewById(R.id.nav_email);
        userName.setText(Prevalent.currentOnlineUser.getFullname());
        userEmail.setText(Prevalent.currentOnlineUser.getEmail().replace(',', '.'));
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();

        /*   Slider*/
        sliderView = findViewById(R.id.imageSlider);
        List<Integer> slideList = new ArrayList<>();
        slideList.add(R.drawable.slide);
        slideList.add(R.drawable.slide2);
        slideList.add(R.drawable.slide3);
        SliderAdapter sliderAdapter = new SliderAdapter(slideList);
        sliderView.setSliderAdapter(sliderAdapter);



    }//**************************End of oncreate ****************************************

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(productRef, Product.class)
                        .build();
        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {
                        holder.procduct_name.setText(model.getName());
                        holder.product_price.setText(model.getPrice() + " DH");
                        Picasso.get().load(model.getImage()).fit().into(holder.product_image);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intentDetails = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                intentDetails.putExtra("id", model.getId());
                                startActivity(intentDetails);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        @SuppressLint("ResourceType") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Appuyer à nouveau pour quitter", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


}