package com.example.boutique;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.boutique.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditRemoveProductActivity extends AppCompatActivity {
    private String productID = "";
    private String prodNameStr, prodDescStr, prodPriceStr;
    private Button editProductbtn, removeProductbtn;
    private EditText productName, productDescription, productPrice;
    private ImageView productImage;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_remove_product);
        ImageView close_EditRemove = (ImageView) findViewById(R.id.close_EditRemove);
        productImage = (ImageView) findViewById(R.id.image_edit_remove);
        editProductbtn = (Button) findViewById(R.id.editProduct);
        removeProductbtn = (Button) findViewById(R.id.removeProduct);
        productName = (EditText) findViewById(R.id.prodName_edit);
        productDescription = (EditText) findViewById(R.id.prodDesc_edit);
        productPrice = (EditText) findViewById(R.id.prodPrice_edit);
        progressDialog = new ProgressDialog(this);
        close_EditRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        productID = getIntent().getStringExtra("id");
        getProductDetails(productID);
        editProductbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProduct(productID);
            }
        });
        removeProductbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeProduct(productID);
            }
        });

    }

    private void editProduct(String productID) {
        prodNameStr = productName.getText().toString();
        prodNameStr = prodNameStr.replace('.', ',');
        prodDescStr = productDescription.getText().toString();
        prodDescStr = prodDescStr.replace('.', ',');
        prodPriceStr = productPrice.getText().toString();
        prodPriceStr = prodPriceStr.replace('.', ',');
        if(TextUtils.isEmpty(prodNameStr)){
            Toast.makeText(this, "Veuillez saisir le nom du produit..", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(prodDescStr)){
            Toast.makeText(this, "Veuillez saisir la déscription du produit..", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(prodPriceStr)){
            Toast.makeText(this, "Veuillez saisir le prix du produit..", Toast.LENGTH_SHORT).show();
        }else {
            HashMap<String, Object> editProdMap = new HashMap<>();
            editProdMap.put("name", prodNameStr);
            editProdMap.put("description", prodDescStr);
            editProdMap.put("price", prodPriceStr);
            progressDialog.setTitle("Modifier produit");
            progressDialog.setTitle("Veuillez patienter...");
            progressDialog.show();
            FirebaseDatabase.getInstance().getReference().child("Products")
                    .child(productID)
                    .updateChildren(editProdMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(EditRemoveProductActivity.this, "Produit modifié", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void removeProduct(String productID) {
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(EditRemoveProductActivity.this);
        alertDialog2.setTitle("Confirmation de la suppression");
        alertDialog2.setMessage("Supprimer?");
        alertDialog2.setPositiveButton("Oui",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.setTitle("Suppression..");
                        progressDialog.setTitle("Veuillez patienter...");
                        progressDialog.show();
                        FirebaseDatabase.getInstance().getReference().child("Products")
                                .child(productID)
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progressDialog.dismiss();
                                            Toast.makeText(EditRemoveProductActivity.this, "Produit supprimé", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(EditRemoveProductActivity.this, ShowProductsToAdminActivity.class));
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
                    productPrice.setText(product.getPrice());
                    Picasso.get().load(product.getImage()).fit().into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}