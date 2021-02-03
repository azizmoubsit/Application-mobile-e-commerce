package com.example.boutique;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.boutique.model.Cart;
import com.example.boutique.viewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminUserProductsActivity extends AppCompatActivity {
    private RecyclerView prList;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference prRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_products);
        String userEmail = getIntent().getStringExtra("email");
        prList = findViewById(R.id.product_orders_list);
        prList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        prList.setLayoutManager(layoutManager);
        ImageView close_p_orders = (ImageView) findViewById(R.id.close_p_orders);
        close_p_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        prRef = FirebaseDatabase.getInstance().getReference().child("Cart list").child("Admin View").child(userEmail).child("Products");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(prRef, Cart.class)
                        .build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                        holder.prodQuantity.setText("Quantité : " + model.getQuantity());
                        holder.prodPrice.setText(model.getPrice());
                        holder.prodName.setText(model.getName());
                        Picasso.get().load(model.getImage()).fit().into(holder.prodImage);
                        holder.edit.setVisibility(View.GONE);
                        holder.delete.setVisibility(View.GONE);
                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };
        prList.setAdapter(adapter);
        adapter.startListening();

    }
}