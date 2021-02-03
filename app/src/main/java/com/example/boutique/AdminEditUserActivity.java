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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Objects;

public class AdminEditUserActivity extends AppCompatActivity {
    private Button save_setting_btn;
    private EditText fullnameInput, passwordInput, phoneInput;
    private ImageView close_edit;
    private ProgressDialog loadingBar;
    private String emailIntent ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_user);
        emailIntent = getIntent().getStringExtra("emailIntent");
        loadingBar = new ProgressDialog(this);
        save_setting_btn = (Button) findViewById(R.id.save_admin_setting_btn) ;
        fullnameInput = (EditText) findViewById(R.id.name_admin_update_setting);
        passwordInput = (EditText) findViewById(R.id.password_admin_update_setting);
        phoneInput = (EditText) findViewById(R.id.phone_admin_update_setting);
        close_edit = (ImageView) findViewById(R.id.close_edit);
        usersInfoDisplay(fullnameInput, passwordInput, phoneInput);
        close_edit.setOnClickListener(new View.OnClickListener() {
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


    private void validatEmail(String fullname, String password, String phone) {
        DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Users").child(emailIntent).exists()){
                    HashMap<String, Object> users = new HashMap<>();
                    users.put("fullname", fullname);
                    users.put("password", password);
                    users.put("phone", phone);
                    rootRef.child("Users").child(emailIntent).updateChildren(users)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(AdminEditUserActivity.this, "Modifié avec succès!", Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();
                                    }else {
                                        loadingBar.dismiss();
                                        Toast.makeText(AdminEditUserActivity.this, "Erreur survenue réessayer plus tard", Toast.LENGTH_LONG).show();
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
        if(TextUtils.isEmpty(fullname)){
            Toast.makeText(this, "Veuillez saisir votre nom et prénom..", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Veuillez saisir un mot de passe..", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Veuillez saisir votre numéro de téléphone..", Toast.LENGTH_LONG).show();
        }else {
            loadingBar.setTitle("Mis à jour du compte");
            loadingBar.setMessage("Veuillez patienter..");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            validatEmail(fullname, password, phone);
        }
    }



    private void usersInfoDisplay(EditText fullnameInput, EditText passwordInput, EditText phoneInput) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(emailIntent);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    try {
                        String name = Objects.requireNonNull(snapshot.child("fullname").getValue()).toString();
                        String pass = Objects.requireNonNull(snapshot.child("password").getValue()).toString();
                        String phone = Objects.requireNonNull(snapshot.child("phone").getValue()).toString();
                        fullnameInput.setText(name);
                        phoneInput.setText(phone);
                        passwordInput.setText(pass);
                    }catch (Exception e){
                        e.getStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}