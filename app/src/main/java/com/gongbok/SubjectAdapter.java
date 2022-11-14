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


    //onClickListener 부착(SubjectSelectScreen에서 함수 정의할 것)
    public interface OnItemClickListener{
        void onItemClick(View v, SubjectData data);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        SubjectData item = DataList.get(position);

        holder.subjectName.setText(item.name);
        holder.problemNum.setText(String.valueOf(item.count));

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition() ;
                if (pos != RecyclerView.NO_POSITION) {
                    if(mListener != null)
                        mListener.onItemClick(v, DataList.get(pos));
                }
            }
        });
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