package com.example.boutique;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.boutique.model.Cart;
import com.example.boutique.model.prevalent.Prevalent;
import com.example.boutique.viewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class CartActivity extends AppCompatActivity {
    private ImageView back, emptyCartImage;
    private RelativeLayout total_layout;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button validateCart, continuerAcheter;
    private TextView total, emptyCartText;
    private int totalPrice = 0;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        validateCart = (Button) findViewById(R.id.validateCart);
        continuerAcheter = (Button) findViewById(R.id.continuerAcheter);
        total = (TextView) findViewById(R.id.totalPrice);
        back = (ImageView) findViewById(R.id.back_cart);
        emptyCartText = (TextView) findViewById(R.id.emptyCartText);
        emptyCartImage = (ImageView) findViewById(R.id.emptyCartImage);
        total_layout = (RelativeLayout) findViewById(R.id.total);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        validateCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference checkIfOrderExist = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getEmail());
                checkIfOrderExist.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                             AlertDialog.Builder builderCart = new AlertDialog.Builder(CartActivity.this);
                             builderCart.setTitle("Vous avez déja commandé");
                             builderCart.setMessage("Votre commande est en cours d'expédition")
                                    .setCancelable(false)
                                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                              }
                                      });
                              AlertDialog alertDialogCart = builderCart.create();
                              alertDialogCart.show();
                        }else {
                            Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                            intent.putExtra("total", String.valueOf(totalPrice));
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        continuerAcheter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this, HomeActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIfCartEmpty();
        final DatabaseReference cartListrRef = FirebaseDatabase.getInstance().getReference().child("Cart list");
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListrRef.child("User View").child(Prevalent.currentOnlineUser.getEmail()).child("Products"), Cart.class)
                .build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                holder.prodQuantity.setText("Quantité : " + model.getQuantity());
                holder.prodPrice.setText(model.getPrice());
                holder.prodName.setText(model.getName());
                Picasso.get().load(model.getImage()).fit().into(holder.prodImage);
                String getPriceStr = model.getPrice();
                getPriceStr = getPriceStr.replace(" DH", "");
                getPriceStr = getPriceStr.trim();
                int oneProductPrice = (Integer.parseInt(getPriceStr)) * (Integer.parseInt(model.getQuantity()));
                totalPrice += oneProductPrice;
                total.setText(totalPrice + " DH");
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(CartActivity.this);
                        alertDialog2.setTitle("Confirmation de la suppression");
                        alertDialog2.setMessage("Supprimer?");
                        alertDialog2.setPositiveButton("Oui",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    cartListrRef.child("User View").child(Prevalent.currentOnlineUser.getEmail()).child("Products")
                                            .child(model.getId())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(CartActivity.this, "Supprimé!", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(CartActivity.this, CartActivity.class));
                                                        finish();
                                                    }
                                                }
                                            });

                                }
                            });
                        alertDialog2.setNegativeButton("Non",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                        alertDialog2.show();
                    }
                });
                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                        intent.putExtra("id", model.getId());
                        startActivity(intent);
                        finish();
                        total.setText(totalPrice + " DH");
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void checkIfCartEmpty() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cart list");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!(snapshot.child("User View").child(Prevalent.currentOnlineUser.getEmail()).exists())){
                    recyclerView.setVisibility(View.GONE);
                    validateCart.setVisibility(View.GONE);
                    total_layout.setVisibility(View.GONE);
                    continuerAcheter.setVisibility(View.VISIBLE);
                    emptyCartImage.setVisibility(View.VISIBLE);
                    emptyCartText.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}