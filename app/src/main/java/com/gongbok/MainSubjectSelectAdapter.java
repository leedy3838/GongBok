package com.gongbok;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class MainSubjectData{
    String subjectName;
    Long ACount;
    Long subjectRating;

    MainSubjectData(String subjectName, Long ACount, Long subjectRating){
        this.subjectName = subjectName;
        this.ACount = ACount;
        this.subjectRating = subjectRating;
    }
}

public class MainSubjectSelectAdapter extends RecyclerView.Adapter<MainSubjectSelectAdapter.SubjectHolder> {
    List<MainSubjectData> SubjectDataValues;
    public MainSubjectSelectAdapter(List<MainSubjectData> SubjectDataList){
        this.SubjectDataValues = SubjectDataList;
    }

    public interface MainSubjectClickListener{
        void onItemClick(View v, MainSubjectData mainSubjectData);
    }

    private MainSubjectClickListener mainSubjectClickListener = null;

    public void setOnItemClickListener(MainSubjectClickListener listener){
        this.mainSubjectClickListener = listener;
    }

    @NonNull
    @Override
    public SubjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_subject_select_item, parent, false);
        return new SubjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectHolder holder, int position) {
        MainSubjectData mainSubjectData = SubjectDataValues.get(position);
        holder.subjectTitleName.setText(mainSubjectData.subjectName);
        holder.subjectRating.setText("" + mainSubjectData.ACount + " Solved");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    if (mainSubjectClickListener != null){
                        mainSubjectClickListener.onItemClick(view, SubjectDataValues.get(position));
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return SubjectDataValues.size();
    }

    public class SubjectHolder extends RecyclerView.ViewHolder {
        TextView subjectTitleName;
        TextView subjectRating;
        public SubjectHolder(@NonNull View itemView) {
            super(itemView);
            subjectRating = itemView.findViewById(R.id.mainSubjectSelectItemRating);
            subjectTitleName = itemView.findViewById(R.id.mainSubjectSelectItem);
        }
    }
}
