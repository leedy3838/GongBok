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
    long tryNum;
    Boolean isSolved = false;
    Boolean isWrong = false;
    //좋아요 한 문제들을 출력할 때 isLikeProblem이 true
    Boolean isLikeProblem = false;

    ProblemData(String name, String subjectName, String link, long likeNum, long tier, long tryNum, Boolean isSolved, Boolean isWrong, Boolean isLikeProblem){
        this.name = name;
        this.link = link;
        this.subjectName = subjectName;
        this.likeNum = likeNum;
        this.tier = tier;
        this.isSolved = isSolved;
        this.isWrong = isWrong;
        this.isLikeProblem = isLikeProblem;
        this.tryNum = tryNum;
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

        if(item.isSolved)
            holder.problemName.setTextColor(Color.rgb(0x06,0xED,0xC9));
        if(item.isWrong)
            holder.problemName.setTextColor(Color.rgb(0xff,0x00,0x00));

        int tier = (int)item.tier;
        switch (tier){
            case 1:
                holder.tier.setImageResource(R.drawable.rank_icons_s1);
                break;
            case 2:
                holder.tier.setImageResource(R.drawable.rank_icons_s2);
                break;
            case 3:
                holder.tier.setImageResource(R.drawable.rank_icons_s3);
                break;
            case 4:
                holder.tier.setImageResource(R.drawable.rank_icons_s4);
                break;
            case 5:
                holder.tier.setImageResource(R.drawable.rank_icons_s5);
                break;
            case 6:
                holder.tier.setImageResource(R.drawable.rank_icons_s6);
                break;
            case 7:
                holder.tier.setImageResource(R.drawable.rank_icons_s7);
                break;
            case 8:
                holder.tier.setImageResource(R.drawable.rank_icons_s8);
                break;
            case 9:
                holder.tier.setImageResource(R.drawable.rank_icons_s9);
                break;
            case 10:
                holder.tier.setImageResource(R.drawable.rank_icons_s10);
                break;
            case 11:
                holder.tier.setImageResource(R.drawable.rank_icons_s11);
                break;
            case 12:
                holder.tier.setImageResource(R.drawable.rank_icons_s12);
                break;
            case 13:
                holder.tier.setImageResource(R.drawable.rank_icons_s13);
                break;
            case 14:
                holder.tier.setImageResource(R.drawable.rank_icons_s14);
                break;
            case 15:
                holder.tier.setImageResource(R.drawable.rank_icons_s15);
                break;
            case 16:
                holder.tier.setImageResource(R.drawable.rank_icons_s16);
                break;
            case 17:
                holder.tier.setImageResource(R.drawable.rank_icons_s17);
                break;
            case 18:
                holder.tier.setImageResource(R.drawable.rank_icons_s18);
                break;
            case 19:
                holder.tier.setImageResource(R.drawable.rank_icons_s19);
                break;
            case 20:
                holder.tier.setImageResource(R.drawable.rank_icons_s20);
                break;
            case 21:
                holder.tier.setImageResource(R.drawable.rank_icons_s21);
                break;
            case 22:
                holder.tier.setImageResource(R.drawable.rank_icons_s22);
                break;
            case 23:
                holder.tier.setImageResource(R.drawable.rank_icons_s23);
                break;
            case 24:
                holder.tier.setImageResource(R.drawable.rank_icons_s24);
                break;
            case 25:
                holder.tier.setImageResource(R.drawable.rank_icons_s25);
                break;
            case 26:
                holder.tier.setImageResource(R.drawable.rank_icons_s26);
                break;
        }

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
