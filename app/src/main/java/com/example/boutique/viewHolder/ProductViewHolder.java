package com.example.boutique.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.boutique.Interface.ItemClickListner;
import com.example.boutique.R;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView procduct_name, product_price;
    public ImageView product_image;
    public ItemClickListner itemClickListner;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        product_image = (ImageView) itemView.findViewById(R.id.product_image);
        product_price = (TextView) itemView.findViewById(R.id.product_price);
        procduct_name = (TextView) itemView.findViewById(R.id.product_name);
    }


    public void setItemClickListner(ItemClickListner itemClickListner){
        this.itemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View v) {
        itemClickListner.onClick(v, getAdapterPosition(), false);
    }
}
