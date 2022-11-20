package com.gongbok;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class RankingData{
    String nickName;
    long rating;

    RankingData(String nickName, long rating){
        this.nickName = nickName;
        this.rating = rating;
    }
}

public class RankingScreenAdapter extends RecyclerView.Adapter<RankingScreenAdapter.RankingScreenHolder> {
    List<RankingData> RankingDataValues;
    public RankingScreenAdapter(List<RankingData> RankingDataList){
        this.RankingDataValues = RankingDataList;
    }

    @NonNull
    @Override
    public RankingScreenHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_item, parent, false);
        return new RankingScreenHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingScreenHolder holder, int position) {
        RankingData rankingItem = RankingDataValues.get(position);

        holder.nickName.setText(rankingItem.nickName);
        holder.rating.setText("Rating " + (int)rankingItem.rating);
    }

    @Override
    public int getItemCount() {
       return RankingDataValues.size();
    }

    public class RankingScreenHolder extends RecyclerView.ViewHolder {
        TextView nickName;
        TextView rating;
        public RankingScreenHolder(View RankingView) {
            super(RankingView);

            nickName = RankingView.findViewById(R.id.userName);
            rating = RankingView.findViewById(R.id.rankingRating);
        }
    }
}
