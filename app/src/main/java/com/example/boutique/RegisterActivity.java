package com.example.boutique;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import io.paperdb.Paper;

public class RegisterActivity extends AppCompatActivity {
    private Button createAccountButton;
    private EditText inputPhoneNumber, inputFullname, inputEmail, inputPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        inputPhoneNumber = (EditText) findViewById(R.id.phone_register);
        createAccountButton = (Button) findViewById(R.id.signin);
        inputFullname = (EditText) findViewById(R.id.fullname_register);
        inputEmail = (EditText) findViewById(R.id.email_register);
        inputPassword = (EditText) findViewById(R.id.password_register);
        loadingBar = new ProgressDialog(this);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
        Paper.init(this);
        String userEmailKey = Paper.book().read(Prevalent.userEmailKey);
        String userPassKey = Paper.book().read(Prevalent.userPassKey);
        if(userEmailKey != "" && userPassKey != ""){
            if(!TextUtils.isEmpty(userEmailKey) && !TextUtils.isEmpty(userPassKey)){
                allowAccess(userEmailKey, userPassKey);
                loadingBar.setTitle("Connexion..");
                loadingBar.setMessage("Veuillez patienter..");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }
    }

    private void createAccount() {
        String fullname = inputFullname.getText().toString();
        String email = inputEmail.getText().toString().replace('.', ',');
        String password = inputPassword.getText().toString();
        String phone = inputPhoneNumber.getText().toString();
        if(TextUtils.isEmpty(fullname)){
            Toast.makeText(this, "Veuillez saisir votre nom et prénom..", Toast.LENGTH_LONG).show();
        }
        else if(email.indexOf('@')==-1 || TextUtils.isEmpty(email)){
            Toast.makeText(this, "Veuillez saisir un email valide..", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Veuillez saisir un mot de passe..", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Veuillez saisir votre numéro de téléphone..", Toast.LENGTH_LONG).show();
        }
        else{
            loadingBar.setTitle("Création de votre compte");
            loadingBar.setMessage("Veuillez patienter, nous vérifions vos données..");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            validatEmail(email, fullname, password, phone);
        }
    }

    private void validatEmail(String email,String fullname,String password, String phone) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!(snapshot.child("Users").child(email).exists())){
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("admin", 0);
                    userDataMap.put("email", email);
                    userDataMap.put("fullname", fullname);
                    userDataMap.put("password", password);
                    userDataMap.put("phone", phone);
                    rootRef.child("Users").child(email).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        Toast.makeText(RegisterActivity.this, "Compte créé avec succès!", Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();
                                        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(loginIntent);
                                        finish();
                                    }else {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Erreur survenue réessayer plus tard", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }else {
                    Toast.makeText(RegisterActivity.this, email + " est déja utilisé", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void allowAccess(String email, String password) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Users").child(email).exists()){
                    User userData = snapshot.child("Users").child(email).getValue(User.class);
                    if(userData.getEmail().equals(email)){
                        if(userData.getPassword().equals(password)){
                            Toast.makeText(RegisterActivity.this, "Bienvenue, " + userData.getFullname(), Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(RegisterActivity.this, "Mot de passe incorrect!"+userData.getFullname(), Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                    }
                }else{
                    Toast.makeText(RegisterActivity.this, "L'e-mail entré ne correspond à aucun compte. Veuillez créer un compte", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}