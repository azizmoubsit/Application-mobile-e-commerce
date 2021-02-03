package com.example.boutique;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.boutique.model.User;
import com.example.boutique.model.prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Objects;
import io.paperdb.Paper;

public class SettingActivity extends AppCompatActivity {
    private Button save_setting_btn;
    private EditText fullnameInput, passwordInput, phoneInput, adresseInput;
    private ImageView close_setting;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        loadingBar = new ProgressDialog(this);
        save_setting_btn = (Button) findViewById(R.id.save_setting_btn) ;
        fullnameInput = (EditText) findViewById(R.id.name_update_setting);
        passwordInput = (EditText) findViewById(R.id.password_update_setting);
        phoneInput = (EditText) findViewById(R.id.phone_update_setting);
        adresseInput = (EditText) findViewById(R.id.address_update_setting) ;
        close_setting = (ImageView) findViewById(R.id.close_setting);
        userInfoDisplay(fullnameInput, passwordInput, phoneInput, adresseInput);
        close_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        save_setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInfoSave();
            }
        });

    }


    private void validatEmail(String fullname, String password, String phone, String adresse) {
        DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Users").child(Prevalent.currentOnlineUser.getEmail()).exists()){
                    HashMap<String, Object> users = new HashMap<>();
                    users.put("admin", 0);
                    users.put("fullname", fullname);
                    users.put("password", password);
                    users.put("phone", phone);
                    users.put("adresse", adresse);
                    rootRef.child("Users").child(Prevalent.currentOnlineUser.getEmail()).updateChildren(users)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        User userData = snapshot.child("Users").child(Paper.book().read(Prevalent.userEmailKey)).getValue(User.class);
                                        Prevalent.currentOnlineUser = userData;
                                        Toast.makeText(SettingActivity.this, "Modifié avec succès!", Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();
                                    }else {
                                        loadingBar.dismiss();
                                        Toast.makeText(SettingActivity.this, "Erreur survenue réessayer plus tard", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void userInfoSave() {
        String fullname = fullnameInput.getText().toString();
        String password = passwordInput.getText().toString();
        String phone = phoneInput.getText().toString();
        String adresse = adresseInput.getText().toString();
        if(TextUtils.isEmpty(fullname)){
            Toast.makeText(this, "Veuillez saisir votre nom et prénom..", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Veuillez saisir un mot de passe..", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Veuillez saisir votre numéro de téléphone..", Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(adresse)){
            Toast.makeText(this, "Veuillez saisir une adresse..", Toast.LENGTH_LONG).show();
        }else {
            loadingBar.setTitle("Mis à jour de votre compte");
            loadingBar.setMessage("Veuillez patienter..");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            validatEmail(fullname, password, phone, adresse);
        }
    }



    private void userInfoDisplay(EditText fullnameInput, EditText passwordInput, EditText phoneInput, EditText adresseInput) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getEmail());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name = Objects.requireNonNull(snapshot.child("fullname").getValue()).toString();
                    String pass = Objects.requireNonNull(snapshot.child("password").getValue()).toString();
                    String phone = Objects.requireNonNull(snapshot.child("phone").getValue()).toString();
                    try {
                        String adresse = Objects.requireNonNull(snapshot.child("adresse").getValue()).toString();
                        adresseInput.setText(adresse);
                    }catch (Exception e){
                        e.getStackTrace();
                    }
                    fullnameInput.setText(name);
                    phoneInput.setText(phone);
                    passwordInput.setText(pass);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}