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

    public interface SolvedOnItemClickListener{
        void onItemClick(View v, SolvedProblemData solvedProblemData);
    }

    private SolvedOnItemClickListener solveListener = null;

    public void setOnItemClickListener(SolvedOnItemClickListener listener){
        this.solveListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull SolvedProblemScreenHolder holder, int position) {
        SolvedProblemData solvedProblemItem = SolvedDataValues.get(position);
        holder.problemName.setText(solvedProblemItem.problemName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    if (solveListener != null){
                        solveListener.onItemClick(view, SolvedDataValues.get(position));
                    }
                }
            }
        });
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
