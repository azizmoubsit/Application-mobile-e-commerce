package com.example.boutique;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
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
import java.util.Objects;

public class AddProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String saveCurrentDate, productRandomKey, downloadImageUrl, prodNameStr, prodDescStr, prodPriceStr;
    private EditText prodName, prodDesc, prodPrice;
    private TextView importImageText;
    private Button addNewProd;
    private ImageView importImageImage, close_addprod;
    private Uri imageUri ;
    private StorageReference productimageRef;
    private DatabaseReference productRef;
    private static final int galleryPick = 1;
    private ProgressDialog loadingBar;
    private RelativeLayout addingImage;
    private Spinner spinnerCategories;
    private String cat = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        close_addprod = (ImageView) findViewById(R.id.close_addprod);
        spinnerCategories = findViewById(R.id.spinnerCategories);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(arrayAdapter);
        spinnerCategories.setOnItemSelectedListener(this);
        productimageRef = FirebaseStorage.getInstance().getReference().child("Product images");
        loadingBar = new ProgressDialog(this);
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        prodName = (EditText) findViewById(R.id.prodName);
        prodDesc = (EditText) findViewById(R.id.prodDesc);
        prodPrice = (EditText) findViewById(R.id.prodPrice);
        addNewProd = (Button) findViewById(R.id.addNewProduct);
        importImageImage = (ImageView) findViewById(R.id.importImageImage);
        importImageText = (TextView) findViewById(R.id.importImageText);
        addingImage = (RelativeLayout) findViewById(R.id.prodImg);
        close_addprod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        addNewProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    }

    private void validate() {
        prodNameStr = prodName.getText().toString();
        prodNameStr = prodNameStr.replace('.', ',');
        prodDescStr = prodDesc.getText().toString();
        prodDescStr = prodDescStr.replace('.', ',');
        prodPriceStr = prodPrice.getText().toString();
        prodPriceStr = prodPriceStr.replace('.', ',');
        if(imageUri == null){
            Toast.makeText(this, "Veuillez choisir l'image du produit..", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(prodNameStr)){
            Toast.makeText(this, "Veuillez saisir le nom du produit..", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(prodDescStr)){
            Toast.makeText(this, "Veuillez saisir la déscription du produit..", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(prodPriceStr)){
            Toast.makeText(this, "Veuillez saisir le prix du produit..", Toast.LENGTH_SHORT).show();
        }else {
            storeProductInfo();
        }
    }
    private void storeProductInfo(){
        loadingBar.setTitle("Ajout d'un nouveau produit.");
        loadingBar.setMessage("Veuillez patinter...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        productRandomKey = "" + System.currentTimeMillis();
        StorageReference filePath = productimageRef.child(imageUri.getLastPathSegment()+productRandomKey);
        final UploadTask uploadTask = filePath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AddProductActivity.this, "Erreur : "+message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw Objects.requireNonNull(task.getException());
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadImageUrl = Objects.requireNonNull(task.getResult()).toString();
                            saveProductToDatabase();
                        }
                    }
                });
            }
        });
    }


    private void saveProductToDatabase() {
        HashMap<String, Object> produit = new HashMap<>();
        produit.put("id", productRandomKey);
        produit.put("image", downloadImageUrl);
        produit.put("name", prodNameStr);
        produit.put("description", prodDescStr);
        produit.put("price", prodPriceStr);
        produit.put("category", cat);
        produit.put("date", saveCurrentDate);
        productRef.child(productRandomKey).updateChildren(produit).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AddProductActivity.this, "Produit ajouté avec succès!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddProductActivity.this, ShowUsersActivity.class));
                    finish();
                    loadingBar.dismiss();
                }else {
                    Toast.makeText(AddProductActivity.this, "Erreur : "+ Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }

    private void openGallery() {
        Intent galleyIntent = new Intent();
        galleyIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleyIntent.setType("image/*");
        startActivityForResult(galleyIntent, galleryPick);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == galleryPick && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            importImageImage.setImageResource(R.drawable.importedsucces);
            importImageText.setText("Image séléctionné");
            Toast.makeText(this, "Image séléctionné", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        cat = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}