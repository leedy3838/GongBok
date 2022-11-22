package com.gongbok;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class SolvedProblemData{
    String problemName;

    SolvedProblemData(String problemName){
        this.problemName = problemName;
    }
}

public class SolvedProblemScreenAdapter extends RecyclerView.Adapter<SolvedProblemScreenAdapter.SolvedProblemScreenHolder> {
    List<SolvedProblemData> SolvedDataValues;
    public SolvedProblemScreenAdapter(List<SolvedProblemData> SolvedDataList){
        this.SolvedDataValues = SolvedDataList;
    }

    @NonNull
    @Override
    public SolvedProblemScreenHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.solved_problem_screen_item, parent, false);
        return new SolvedProblemScreenHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SolvedProblemScreenHolder holder, int position) {
        SolvedProblemData solvedProblemItem = SolvedDataValues.get(position);
        holder.problemName.setText(solvedProblemItem.problemName);
    }

    @Override
    public int getItemCount() {
        return SolvedDataValues.size();
    }

    public class SolvedProblemScreenHolder extends RecyclerView.ViewHolder {
        TextView problemName;
        TextView problemLikes;
        public SolvedProblemScreenHolder(@NonNull View itemView) {

            super(itemView);
            problemName = itemView.findViewById(R.id.solvedProblemName);
            problemLikes = itemView.findViewById(R.id.likesCount);
        }
    }
}
