package com.example.boutique.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boutique.Interface.ItemClickListner;
import com.example.boutique.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView prodName, prodPrice, prodQuantity;
    public ImageView prodImage, delete, edit;
    private ItemClickListner itemClickListner;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        prodName = itemView.findViewById(R.id.pr_name_cart);
        prodQuantity = itemView.findViewById(R.id.pr_quan_cart);
        prodPrice = itemView.findViewById(R.id.pr_price_cart);
        prodImage = itemView.findViewById(R.id.pr_image_cart);
        delete = itemView.findViewById(R.id.delete_from_cart);
        edit = itemView.findViewById(R.id.edit_from_cart);
    }

    @Override
    public void onClick(View v) {
        itemClickListner.onClick(v, getAdapterPosition(), false);
    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;

    }
}
