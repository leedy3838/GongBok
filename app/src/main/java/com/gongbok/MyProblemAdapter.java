package com.gongbok;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class MyProblemData{
    String problemName;
    String subjectName;

    MyProblemData(String problemName, String subjectName){
        this.problemName = problemName;
        this.subjectName = subjectName;
    }
}

public class MyProblemAdapter extends RecyclerView.Adapter<MyProblemAdapter.MyProblemScreenHolder> {
    List<MyProblemData> MyProblemDataValues;

    public MyProblemAdapter(List<MyProblemData> MyProblemDataList){
        this.MyProblemDataValues = MyProblemDataList;
    }

    @NonNull
    @Override
    public MyProblemScreenHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_problem_item, parent, false);
        return new MyProblemScreenHolder(view);
    }

    public interface MyProblemOnClickLister{
        void onItemClick(View v, MyProblemData myProblemData);
    }

    private MyProblemOnClickLister myProblemOnClickLister = null;

    public void setOnItemClickListener(MyProblemOnClickLister listener){
        this.myProblemOnClickLister = listener;
    }
    @Override
    public void onBindViewHolder(@NonNull MyProblemScreenHolder holder, int position) {
        MyProblemData MyProblemItem = MyProblemDataValues.get(position);
        holder.problemName.setText(MyProblemItem.problemName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    if (myProblemOnClickLister != null){
                        myProblemOnClickLister.onItemClick(view, MyProblemDataValues.get(position));
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return MyProblemDataValues.size();
    }

    public class MyProblemScreenHolder extends RecyclerView.ViewHolder{
        TextView problemName;

        public MyProblemScreenHolder(@NonNull View itemView) {
            super(itemView);
            problemName = itemView.findViewById(R.id.MyProblemName);
        }
    }
}
