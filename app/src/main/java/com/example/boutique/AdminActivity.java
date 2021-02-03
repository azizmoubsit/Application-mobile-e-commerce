package com.example.boutique;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import io.paperdb.Paper;

public class AdminActivity extends AppCompatActivity {
    private static final int IMAGE_REQUEST = 2;
    private RelativeLayout seeProducts, seeOrders, seeUsers;
    private ImageView logoutBtn;
    boolean doubleBackToExitPressedOnce = false;
    private ProgressDialog progressDialog;

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
        setContentView(R.layout.activity_admin);
        progressDialog = new ProgressDialog(this);
        seeProducts = (RelativeLayout) findViewById(R.id.seeProducts);
        seeOrders = (RelativeLayout) findViewById(R.id.seeOrders);
        seeUsers = (RelativeLayout) findViewById(R.id.seeUsers);
        seeUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this, ShowUsersActivity.class));
            }
        });
        seeOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this, AdminOrdersActivity.class));
            }
        });
        seeProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this, ShowProductsToAdminActivity.class));
            }
        });
        logoutBtn = (ImageView) findViewById(R.id.logout_admin);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Déconnexion..");
                progressDialog.setTitle("Veuillez patienter...");
                progressDialog.show();
                Paper.book().destroy();
                Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}