package com.example.boutique;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.boutique.model.prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class ConfirmFinalOrderActivity extends AppCompatActivity {
    private ImageView back;
    private EditText name, city, address, phone;
    private Button confirmButton;
    private String totalPrice;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);
        progressDialog = new ProgressDialog(this);
        back = (ImageView) findViewById(R.id.back_Valider);
        name = (EditText) findViewById(R.id.name_confrim_ship);
        city = (EditText) findViewById(R.id.city_confrim_ship);
        address = (EditText) findViewById(R.id.address_confirm_ship);
        phone = (EditText) findViewById(R.id.phone_confirm_ship);
        confirmButton = (Button) findViewById(R.id.save__confirm_ship);
        totalPrice = getIntent().getStringExtra("total");
        remplirChamps(name, address, phone);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForm();
            }
        });
    }

    private void checkForm() {
        if(TextUtils.isEmpty(name.getText().toString())){
            Toast.makeText(this, "Saisir votre nom et prénom", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(city.getText().toString())){
            Toast.makeText(this, "Saisir votre ville", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(address.getText().toString())){
            Toast.makeText(this, "Saisir votre adresse", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(phone.getText().toString())){
            Toast.makeText(this, "Saisir votre numéro de téléphone", Toast.LENGTH_SHORT).show();
        }else {
            confirmOrder();
        }
    }

    private void confirmOrder() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String saveDate = simpleDateFormat.format(calendar.getTime());
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getEmail());
        HashMap<String, Object> orders = new HashMap<>();
        orders.put("total", totalPrice);
        orders.put("email", Prevalent.currentOnlineUser.getEmail());
        orders.put("name", name.getText().toString());
        orders.put("phone", phone.getText().toString());
        orders.put("address", address.getText().toString());
        orders.put("city", city.getText().toString());
        orders.put("date", saveDate);
        reference.updateChildren(orders).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.setTitle("Validation..");
                    progressDialog.setMessage("Veuillez patienter...");
                    progressDialog.show();
                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart list")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getEmail())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ConfirmFinalOrderActivity.this, "Votre commande a été passée avec succès", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });

    }

    private void remplirChamps(EditText name, EditText address, EditText phone) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(Prevalent.currentOnlineUser.getEmail());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String fullname = Objects.requireNonNull(snapshot.child("fullname").getValue()).toString();
                    String phoneRef = Objects.requireNonNull(snapshot.child("phone").getValue()).toString();
                    try {
                        String adresse = Objects.requireNonNull(snapshot.child("adresse").getValue()).toString();
                        address.setText(adresse);
                    }catch (Exception e){
                        e.getStackTrace();
                    }
                    name.setText(fullname);
                    phone.setText(phoneRef);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}