package com.example.boutique.viewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.boutique.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.MySliderViewHolder> {
    List<Integer> imageList;
    public SliderAdapter(List<Integer> list){
        this.imageList = list;
    }

    @Override
    public MySliderViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new MySliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MySliderViewHolder viewHolder, int position) {
        viewHolder.imageView.setImageResource(imageList.get(position));
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    class MySliderViewHolder extends SliderViewAdapter.ViewHolder{
        ImageView imageView;
        public MySliderViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item_slider);

        }
    }

}