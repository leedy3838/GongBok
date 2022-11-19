package com.gongbok;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class ProblemData{
    String name;
    String subjectName;
    String link;
    long likeNum;
    long tier;
    Boolean isSolved = false;
    Boolean isWrong = false;
    //좋아요 한 문제들을 출력할 때 isLikeProblem이 true
    Boolean isLikeProblem = false;

    ProblemData(String name, String subjectName, String link, long likeNum, long tier, Boolean isSolved, Boolean isWrong, Boolean isLikeProblem){
        this.name = name;
        this.link = link;
        this.subjectName = subjectName;
        this.likeNum = likeNum;
        this.tier = tier;
        this.isSolved = isSolved;
        this.isWrong = isWrong;
        this.isLikeProblem = isLikeProblem;
    }
}

public class ProblemAdapter extends RecyclerView.Adapter<ProblemAdapter.ProblemViewHolder>{
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

    //onClickListener 부착(ProblemSelectScreen에서 함수 정의할 것)
    public interface OnItemClickListener{
        void onItemClick(View v, ProblemData data);
    }

    private ProblemAdapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(ProblemAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    //리사이클러 뷰에서 뷰홀더를 재사용할 때 글자 색이 이상하게 되는 거 해결
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull ProblemViewHolder holder, int position) {
        ProblemData item = DataList.get(position);

        if(!item.isLikeProblem)
            holder.problemName.setText(item.name);
        else
            holder.problemName.setText(item.subjectName + " " + item.name);
        holder.likeNum.setText(String.valueOf(item.likeNum));
        //추후 tierImage가 추가되면 이에 대해서 이미지 바꿔주기

        if(item.isSolved)
            holder.problemName.setTextColor(Color.rgb(0x03,0xDA,0xC5));
        if(item.isWrong)
            holder.problemName.setTextColor(Color.rgb(0xff,0x00,0x00));

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

    static class ProblemViewHolder extends RecyclerView.ViewHolder{
        TextView problemName;
        TextView likeNum;
        ImageView tier;

        public ProblemViewHolder(@NonNull View view) {
            super(view);

            problemName = view.findViewById(R.id.problemName);
            likeNum = view.findViewById(R.id.likeNum);
            tier = view.findViewById(R.id.tierImage);
        }
    }
}
