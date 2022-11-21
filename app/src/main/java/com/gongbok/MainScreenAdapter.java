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
        //나중에 티어 별로 이미지 다르게 할것.
        //holder.tier.setImageResource(R.drawable.rank_icons_s2);
    }

    @Override
    public int getItemCount() {
        return RatingDataValues.size();
    }

    static class MainScreenHolder extends RecyclerView.ViewHolder{
        ImageButton tier;

        public MainScreenHolder(@NonNull View itemView) {
            super(itemView);

            tier = itemView.findViewById(R.id.mainItem);
        }
    }
}
