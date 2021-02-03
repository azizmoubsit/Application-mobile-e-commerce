package com.example.boutique;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.boutique.model.User;
import com.example.boutique.model.prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button login_btn, signin_btn;
    private ProgressDialog loadingBar;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Appuyer à nouveau pour quitter", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingBar = new ProgressDialog(this);
        login_btn = (Button) findViewById(R.id.login_btn);
        signin_btn = (Button) findViewById(R.id.signin_btn);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
            }
        });
        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signinIntent = new Intent(MainActivity.this, RegisterActivity.class);
                signinIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signinIntent);
                finish();
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
                            if(userData.getAdmin() == 1){
                                Toast.makeText(MainActivity.this, "Admin : " + userData.getFullname(), Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                                Prevalent.currentOnlineUser = userData;
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(MainActivity.this, "Bienvenue, " + userData.getFullname(), Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUser = userData;
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }else{
                            Toast.makeText(MainActivity.this, "Mot de passe incorrect!"+userData.getFullname(), Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                    }
                }else{
                    Toast.makeText(MainActivity.this, "L'e-mail entré ne correspond à aucun compte. Veuillez créer un compte", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


}