package com.gongbok;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class SubjectData{
    String name;
    Long count;

    SubjectData(String name, Long count){
        this.name = name;
        this.count = count;
    }
}

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>{
    List<SubjectData> DataList;

    public SubjectAdapter(List<SubjectData> subjectDataList){
        this.DataList = subjectDataList;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_list, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        SubjectData item = DataList.get(position);

        holder.subjectName.setText(item.name);
        holder.problemNum.setText(String.valueOf(item.count));
    }

    @Override
    public int getItemCount() {
        return DataList.size();
    }

    static class SubjectViewHolder extends RecyclerView.ViewHolder{
        TextView subjectName;
        TextView problemNum;

        public SubjectViewHolder(View view) {
            super(view);

            subjectName = view.findViewById(R.id.SubjectName);
            problemNum = view.findViewById(R.id.problemNum);
        }
    }
}