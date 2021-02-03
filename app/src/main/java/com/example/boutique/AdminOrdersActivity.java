package com.example.boutique;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.boutique.model.Order;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminOrdersActivity extends AppCompatActivity {
    private RecyclerView ordersList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        ordersList = findViewById(R.id.orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));

        ImageView close_orders = (ImageView) findViewById(R.id.close_orders);
        close_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Order> options =
                new FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(ordersRef, Order.class)
                .build();
        FirebaseRecyclerAdapter<Order, OrderHolder> adapter =
                new FirebaseRecyclerAdapter<Order, OrderHolder>(options) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    protected void onBindViewHolder(@NonNull OrderHolder holder, int position, @NonNull Order model) {
                        holder.email.setText("Email : " + model.getEmail().replace(',', '.'));
                        holder.name.setText("Nom et pénom : " + model.getName());
                        holder.phone.setText("Téléphone : " + model.getPhone());
                        holder.city.setText("Ville : " + model.getCity());
                        holder.address.setText("Adresse : " + model.getAddress());
                        holder.date.setText("La date : " + model.getDate());
                        holder.total.setText("Prix total : " + model.getTotal() + " DH");
                        holder.accept_order.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ordersRef.child(model.getEmail()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            FirebaseDatabase.getInstance().getReference().child("Cart list").child("Admin View").child(model.getEmail()).removeValue();
                                            Toast.makeText(AdminOrdersActivity.this, "Ordre apprové", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(AdminOrdersActivity.this, AdminOrdersActivity.class);
                                        }
                                    }
                                });
                            }
                        });
                        holder.viewOrderProducts.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String em = getRef(position).getKey();
                                Intent intent = new Intent(AdminOrdersActivity.this, AdminUserProductsActivity.class);
                                intent.putExtra("email", em);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent, false);
                       return new OrderHolder(view);
                    }
                };
        ordersList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class OrderHolder extends RecyclerView.ViewHolder {
        public TextView email, address, city, date, name, phone, total;
        public Button viewOrderProducts;
        public ImageView accept_order;

        public OrderHolder(@NonNull View itemView) {
            super(itemView);
            accept_order = itemView.findViewById(R.id.accept_order);
            email = itemView.findViewById(R.id.order_email);
            address = itemView.findViewById(R.id.orders_address);
            city = itemView.findViewById(R.id.orders_city);
            date = itemView.findViewById(R.id.orders_date);
            name = itemView.findViewById(R.id.user_name_orders);
            phone = itemView.findViewById(R.id.orders_phone);
            total = itemView.findViewById(R.id.orders_price);
            viewOrderProducts = itemView.findViewById(R.id.show_all_products_oreder);
        }
    }
}