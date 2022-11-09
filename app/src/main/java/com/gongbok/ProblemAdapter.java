package com.gongbok;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class ProblemData{
    String name;
    String link;
    int like;
    int tier;

    ProblemData(String name, String link, int like, int tier){
        this.name = name;
        this.link = link;
        this.like = like;
        this.tier = tier;
    }
}

class ProblemViewHolder extends RecyclerView.ViewHolder{
    public ProblemViewHolder(@NonNull View itemView) {
        super(itemView);

        //여기서부터 click listener 붙여주면 됨.
    }
}

public class ProblemAdapter extends RecyclerView.Adapter<ProblemViewHolder>{
    List<ProblemData> DataList;

    public ProblemAdapter(List<ProblemData> problemDataList){
        this.DataList = problemDataList;
    }

    @NonNull
    @Override
    public ProblemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.problem_list, parent, false);
        return new ProblemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProblemViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 10;
    }
}
