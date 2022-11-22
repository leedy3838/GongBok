package com.gongbok;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class RatingData{
    String problemName;
    long tier;

    RatingData(String problemName, long tier){
        this.problemName = problemName;
        this.tier = tier;
    }
}

public class MainScreenAdapter extends RecyclerView.Adapter<MainScreenAdapter.MainScreenHolder> {
    List<RatingData> RatingDataValues;
    public MainScreenAdapter(List<RatingData> RatingDataList){
        this.RatingDataValues = RatingDataList;
    }

    @NonNull
    @Override
    public MainScreenHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_screen_item, parent, false);
        return new MainScreenHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainScreenHolder holder, int position) {
        RatingData ratingItem = RatingDataValues.get(position);

        if (ratingItem.tier < 2) holder.tierB.setImageResource(R.drawable.rank_icons_s1);
        else if (ratingItem.tier < 3) holder.tierB.setImageResource(R.drawable.rank_icons_s2);
        else if (ratingItem.tier < 4) holder.tierB.setImageResource(R.drawable.rank_icons_s3);
        else if (ratingItem.tier < 5) holder.tierB.setImageResource(R.drawable.rank_icons_s4);
        else if (ratingItem.tier < 12) holder.tierB.setImageResource(R.drawable.rank_icons_s5);
        else if (ratingItem.tier < 14) holder.tierB.setImageResource(R.drawable.rank_icons_s6);
        else if (ratingItem.tier < 16) holder.tierB.setImageResource(R.drawable.rank_icons_s7);
        else if (ratingItem.tier < 18) holder.tierB.setImageResource(R.drawable.rank_icons_s8);
        else if (ratingItem.tier < 20) holder.tierB.setImageResource(R.drawable.rank_icons_s9);
        else if (ratingItem.tier < 50) holder.tierB.setImageResource(R.drawable.rank_icons_s10);
        else if (ratingItem.tier < 55) holder.tierB.setImageResource(R.drawable.rank_icons_s11);
        else if (ratingItem.tier < 60) holder.tierB.setImageResource(R.drawable.rank_icons_s12);
        else if (ratingItem.tier < 65) holder.tierB.setImageResource(R.drawable.rank_icons_s13);
        else if (ratingItem.tier < 70) holder.tierB.setImageResource(R.drawable.rank_icons_s14);
        else if (ratingItem.tier < 200) holder.tierB.setImageResource(R.drawable.rank_icons_s15);
        else if (ratingItem.tier < 210) holder.tierB.setImageResource(R.drawable.rank_icons_s16);
        else if (ratingItem.tier < 220) holder.tierB.setImageResource(R.drawable.rank_icons_s17);
        else if (ratingItem.tier < 230) holder.tierB.setImageResource(R.drawable.rank_icons_s18);
        else if (ratingItem.tier < 240) holder.tierB.setImageResource(R.drawable.rank_icons_s19);
        else if (ratingItem.tier < 520) holder.tierB.setImageResource(R.drawable.rank_icons_s20);
        else if (ratingItem.tier < 540) holder.tierB.setImageResource(R.drawable.rank_icons_s21);
        else if (ratingItem.tier < 560) holder.tierB.setImageResource(R.drawable.rank_icons_s22);
        else if (ratingItem.tier < 580) holder.tierB.setImageResource(R.drawable.rank_icons_s23);
        else if (ratingItem.tier < 600) holder.tierB.setImageResource(R.drawable.rank_icons_s24);
        else if (ratingItem.tier < 1000) holder.tierB.setImageResource(R.drawable.rank_icons_s25);
        else holder.tierB.setImageResource(R.drawable.rank_icons_s30);
    }

    @Override
    public int getItemCount() {
        return RatingDataValues.size();
    }

    static class MainScreenHolder extends RecyclerView.ViewHolder{
        ImageButton tierB;

        public MainScreenHolder(@NonNull View itemView) {
            super(itemView);

            tierB = itemView.findViewById(R.id.mainItem);
        }
    }
}
