package com.example.boutique;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class CategoriesActivity extends AppCompatActivity {
    private ImageView close_cat;
    private RelativeLayout rl_mobiles, rl_pc, rl_tv, rl_clothes, rl_shoes, rl_games, rl_accessory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        close_cat = (ImageView) findViewById(R.id.close_cat);
        rl_mobiles = (RelativeLayout) findViewById(R.id.rl_mobiles);
        rl_pc = (RelativeLayout) findViewById(R.id.rl_pc);
        rl_tv = (RelativeLayout) findViewById(R.id.rl_tv);
        rl_clothes = (RelativeLayout) findViewById(R.id.rl_clothes);
        rl_shoes = (RelativeLayout) findViewById(R.id.rl_shoes);
        rl_games = (RelativeLayout) findViewById(R.id.rl_games);
        rl_accessory = (RelativeLayout) findViewById(R.id.rl_accessory);
        rl_mobiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoriesActivity.this, CategorySelectedActivity.class);
                intent.putExtra("category", "Téléphones et tablettes");
                startActivity(intent);
            }
        });
        rl_pc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoriesActivity.this, CategorySelectedActivity.class);
                intent.putExtra("category", "Ordinateurs");
                startActivity(intent);
            }
        });
        rl_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoriesActivity.this, CategorySelectedActivity.class);
                intent.putExtra("category", "Télévisions");
                startActivity(intent);
            }
        });
        rl_clothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoriesActivity.this, CategorySelectedActivity.class);
                intent.putExtra("category", "Vêtements");
                startActivity(intent);
            }
        });
        rl_shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoriesActivity.this, CategorySelectedActivity.class);
                intent.putExtra("category", "Chaussures");
                startActivity(intent);
            }
        });
        rl_games.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoriesActivity.this, CategorySelectedActivity.class);
                intent.putExtra("category", "Jeux video et consoles");
                startActivity(intent);
            }
        });
        rl_accessory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoriesActivity.this, CategorySelectedActivity.class);
                intent.putExtra("category", "Accessoires mode");
                startActivity(intent);
            }
        });


        close_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}