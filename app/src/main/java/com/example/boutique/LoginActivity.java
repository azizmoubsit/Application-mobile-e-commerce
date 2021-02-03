package com.example.boutique;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import com.rey.material.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.boutique.model.User;
import com.example.boutique.model.prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private Button loginBtn;
    private CheckBox remeberMe;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBtn = (Button) findViewById(R.id.login);
        inputEmail = (EditText) findViewById(R.id.email_login);
        inputPassword = (EditText) findViewById(R.id.password);
        remeberMe = (CheckBox) findViewById(R.id.remember_me);
        Paper.init(this);
        loadingBar = new ProgressDialog(this);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String email = inputEmail.getText().toString().replace('.', ',');
        String password = inputPassword.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Veuillez saisir un email valide..", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Veuillez saisir un mot de passe..", Toast.LENGTH_LONG).show();
        }
        else{
            loadingBar.setTitle("Connexion..");
            loadingBar.setMessage("Veuillez patineter...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            allowAccesToAccount(email, password);
        }
    }

    private void allowAccesToAccount(String email, String password) {
        if(remeberMe.isChecked()){
            Paper.book().write(Prevalent.userEmailKey, email);
            Paper.book().write(Prevalent.userPassKey, password);
        }
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Users").child(email).exists()){
                    User userData = snapshot.child("Users").child(email).getValue(User.class);
                    assert userData != null;
                    if(userData.getEmail().equals(email)){
                        if(userData.getPassword().equals(password)){
                            if(userData.getAdmin() == 1){
                                Toast.makeText(LoginActivity.this, "Admin : " + userData.getFullname(), Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                Prevalent.currentOnlineUser = userData;
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(LoginActivity.this, "Bienvenue, " + userData.getFullname(), Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUser = userData;
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }else{
                            Toast.makeText(LoginActivity.this, "Mot de passe incorrect!", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "L'e-mail entré ne correspond à aucun compte. Veuillez créer un compte", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}