package com.gongbok;

import static android.view.View.GONE;

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
        else {
            holder.problemName.setText(item.subjectName + " " + item.name);
            holder.likeNum.setVisibility(GONE);
            holder.likeText.setVisibility(GONE);
        }
        holder.likeNum.setText(String.valueOf(item.likeNum));

        if(item.tier == 1)
            holder.tier.setImageResource(R.drawable.rank_icons_s1);
        else if(item.tier == 2)
            holder.tier.setImageResource(R.drawable.rank_icons_s2);
        else if(item.tier == 3)
            holder.tier.setImageResource(R.drawable.rank_icons_s3);
        else if(item.tier == 4)
            holder.tier.setImageResource(R.drawable.rank_icons_s4);
        else if(item.tier == 5)
            holder.tier.setImageResource(R.drawable.rank_icons_s5);
        else if(item.tier == 6)
            holder.tier.setImageResource(R.drawable.rank_icons_s6);
        else if(item.tier == 7)
            holder.tier.setImageResource(R.drawable.rank_icons_s7);
        else if(item.tier == 8)
            holder.tier.setImageResource(R.drawable.rank_icons_s8);
        else if(item.tier == 9)
            holder.tier.setImageResource(R.drawable.rank_icons_s9);
        else if(item.tier == 10)
            holder.tier.setImageResource(R.drawable.rank_icons_s10);
        else if(item.tier == 11)
            holder.tier.setImageResource(R.drawable.rank_icons_s11);
        else if(item.tier == 12)
            holder.tier.setImageResource(R.drawable.rank_icons_s12);
        else if(item.tier == 13)
            holder.tier.setImageResource(R.drawable.rank_icons_s13);
        else if(item.tier == 14)
            holder.tier.setImageResource(R.drawable.rank_icons_s14);
        else if(item.tier == 15)
            holder.tier.setImageResource(R.drawable.rank_icons_s15);
        else if(item.tier == 16)
            holder.tier.setImageResource(R.drawable.rank_icons_s16);
        else if(item.tier == 17)
            holder.tier.setImageResource(R.drawable.rank_icons_s17);
        else if(item.tier == 18)
            holder.tier.setImageResource(R.drawable.rank_icons_s18);
        else if(item.tier == 19)
            holder.tier.setImageResource(R.drawable.rank_icons_s19);
        else if(item.tier == 20)
            holder.tier.setImageResource(R.drawable.rank_icons_s20);
        else if(item.tier == 21)
            holder.tier.setImageResource(R.drawable.rank_icons_s21);
        else if(item.tier == 22)
            holder.tier.setImageResource(R.drawable.rank_icons_s22);
        else if(item.tier == 23)
            holder.tier.setImageResource(R.drawable.rank_icons_s23);
        else if(item.tier == 24)
            holder.tier.setImageResource(R.drawable.rank_icons_s24);
        else if(item.tier == 25)
            holder.tier.setImageResource(R.drawable.rank_icons_s25);
        else if(item.tier == 26)
            holder.tier.setImageResource(R.drawable.rank_icons_s26);

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
        TextView likeText;

        public ProblemViewHolder(@NonNull View view) {
            super(view);

            problemName = view.findViewById(R.id.problemName);
            likeNum = view.findViewById(R.id.likeNum);
            tier = view.findViewById(R.id.tierImage);
            likeText = view.findViewById(R.id.좋아요);
        }
    }
}
