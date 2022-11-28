package com.gongbok;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

class ExplanationData{
    String userName;
    Long userTier;
    String explanationPicPath;
    Long explanationLikes;
    String explanationName;

    ExplanationData(String explanationName, String userName, Long userTier,
                    String explanationPicPath, Long explanationLikes){
        this.explanationName = explanationName;
        this.userName = userName;
        this.userTier = userTier;
        this.explanationPicPath = explanationPicPath;
        this.explanationLikes = explanationLikes;
    }
}

public class ExplanationAdapter extends RecyclerView.Adapter<ExplanationAdapter.ExplanationViewHolder> { List<ExplanationData> DataList;
    Context context;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public ExplanationAdapter(Context context, List<ExplanationData> explanationDataList) {
        this.DataList = explanationDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ExplanationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.explanation_list, parent, false);
        return new ExplanationViewHolder(view);
    }

    public interface OnItemClickListener{
        void onItemClick(View v, ExplanationData data);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) { this.mListener = listener; }

    @Override
    public void onBindViewHolder(@NonNull ExplanationViewHolder holder, int position) {
        ExplanationData item = DataList.get(position);

        // 풀이 미리보기 사진 설정
        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child(item.explanationPicPath);

        Glide.with(context)
                .load(pathReference)
                .into(holder.explanationImageIv);

        // 등록자 이름 설정
        holder.explanationNameTv.setText(item.userName);

        // 좋아요 수 설정
        String likeString = item.explanationLikes +"";
        holder.explanationLikeTv.setText(likeString);

        // 등록자 티어 이미지 설정
        switch (item.userTier.intValue()){
            case 1:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s1);
                break;
            case 2:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s2);
                break;
            case 3:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s3);
                break;
            case 4:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s4);
                break;
            case 5:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s5);
                break;
            case 6:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s6);
                break;
            case 7:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s7);
                break;
            case 8:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s8);
                break;
            case 9:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s9);
                break;
            case 10:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s10);
                break;
            case 11:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s11);
                break;
            case 12:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s12);
                break;
            case 13:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s13);
                break;
            case 14:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s14);
                break;
            case 15:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s15);
                break;
            case 16:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s16);
                break;
            case 17:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s17);
                break;
            case 18:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s18);
                break;
            case 19:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s19);
                break;
            case 20:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s20);
                break;
            case 21:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s21);
                break;
            case 22:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s22);
                break;
            case 23:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s23);
                break;
            case 24:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s24);
                break;
            case 25:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s25);
                break;
            case 26:
                holder.explanationTierIv.setImageResource(R.drawable.rank_icons_s26);
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

    static class ExplanationViewHolder extends RecyclerView.ViewHolder{
        ImageView explanationImageIv;
        ImageView explanationTierIv;
        TextView explanationNameTv;
        TextView explanationLikeTv;

        public ExplanationViewHolder(View view) {
            super(view);

            explanationImageIv = view.findViewById(R.id.explanation_image);
            explanationTierIv = view.findViewById(R.id.explanation_tier);
            explanationNameTv = view.findViewById(R.id.explanation_name);
            explanationLikeTv = view.findViewById(R.id.explanation_like);
        }
    }

}
