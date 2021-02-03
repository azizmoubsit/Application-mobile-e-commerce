package com.example.boutique;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.boutique.model.Product;
import com.example.boutique.model.prevalent.Prevalent;
import com.example.boutique.viewHolder.ProductViewHolder;
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
import com.zolad.zoominimageview.ZoomInImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {
    private TextView productName, productDescription, productPrice, textDetailsTitle;
    private Button addToCart;
    private ImageView  back;
    private ZoomInImageView productImage;
    private ElegantNumberButton numberOfProducts;
    private String productID = "";
    private String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        textDetailsTitle = (TextView) findViewById(R.id.textDetailsTitle);
        productID = getIntent().getStringExtra("id");
        addToCart = (Button) findViewById(R.id.addToCart);
        productName = (TextView) findViewById(R.id.product_details_name);
        productDescription = (TextView) findViewById(R.id.product_details_description);
        productPrice = (TextView) findViewById(R.id.product_details_price);
        productImage = (ZoomInImageView) findViewById(R.id.product_details_image);
        back = (ImageView) findViewById(R.id.back_details_pr);
        numberOfProducts = (ElegantNumberButton) findViewById(R.id.product_details_number);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getProductDetails(productID);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCartList();
            }
        });
    }


    private void addToCartList() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = simpleDateFormat.format(calendar.getTime());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cart list");
        HashMap<String, Object> cartList = new HashMap<>();
        cartList.put("id", productID);
        cartList.put("name", productName.getText().toString());
        cartList.put("price", productPrice.getText().toString());
        cartList.put("date", currentDate);
        cartList.put("image", imageUrl);
        cartList.put("quantity", numberOfProducts.getNumber());
        reference.child("User View").child(Prevalent.currentOnlineUser.getEmail())
                    .child("Products").child(productID)
                    .updateChildren(cartList)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                reference.child("Admin View").child(Prevalent.currentOnlineUser.getEmail())
                                        .child("Products").child(productID)
                                        .updateChildren(cartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProductDetailsActivity.this, "Produit ajouté au panier", Toast.LENGTH_SHORT).show();
                                            int colorBackground = ContextCompat.getColor(ProductDetailsActivity.this, R.color.grey);
                                            addToCart.setBackgroundColor(colorBackground);
                                            addToCart.setText("Ajouté");
                                            addToCart.setEnabled(false);
                                        }
                                    }
                                });
                            }
                        }
                    });
    }

    private void getProductDetails(String productID) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Product product = snapshot.getValue(Product.class);
                    assert product != null;
                    productName.setText(product.getName());
                    productDescription.setText(product.getDescription());
                    productPrice.setText(product.getPrice()+" DH");
                    Picasso.get().load(product.getImage()).fit().into(productImage);
                    imageUrl = product.getImage();
                    textDetailsTitle.setText(product.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}