package com.example.boutique.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boutique.Interface.ItemClickListner;
import com.example.boutique.R;

public class UserViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView fullNameUser, emailUser;
    private ItemClickListner itemClickListner;
    public ImageView edit_user, delete_user;
    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        fullNameUser = (TextView) itemView.findViewById(R.id.fullname_show_users);
        emailUser = (TextView) itemView.findViewById(R.id.email_show_users);
        edit_user = (ImageView) itemView.findViewById(R.id.edit_user);
        delete_user = (ImageView) itemView.findViewById(R.id.delete_user);
    }

    @Override
    public void onClick(View v) {
        itemClickListner.onClick(v, getAdapterPosition(), false);
    }

}
