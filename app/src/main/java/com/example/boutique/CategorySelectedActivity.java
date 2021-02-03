package com.example.boutique;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.boutique.model.Product;
import com.example.boutique.viewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class CategorySelectedActivity extends AppCompatActivity {
    private String category = "";
    private RecyclerView searchList;
    private TextView category_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selected);
        ImageView close_selected_category = (ImageView) findViewById(R.id.close_selected_category);
        category_name = (TextView) findViewById(R.id.category_name);
        close_selected_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        category = getIntent().getStringExtra("category");
        category_name.setText(category);
        searchList = findViewById(R.id.selected_category_products);
        searchList.setLayoutManager(new GridLayoutManager(this, 2));
    }
    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(reference.orderByChild("category").startAt(category).endAt(category+"\uf8ff"), Product.class)
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
                                Intent intentDetails = new Intent(CategorySelectedActivity.this, ProductDetailsActivity.class);
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
        searchList.setAdapter(adapter);
        adapter.startListening();
    }
}