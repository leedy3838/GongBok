package com.gongbok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ShowExplanationScreen extends AppCompatActivity {

    // 풀이를 등록한 사람의 이름
    private String userName;
    private Long userTier;
    private String explanationPicPath;
    private Long explanationLikes;
    private String explanationName;
    String problemName;
    String subjectName;

    // 현재 접속 중인 사용자 이름
    private String userID;

    private TextView likeCountTv;
    private TextView userNameTv;
    private TextView explanationTv;
    private ImageView userTierIv;
    private ImageView explanationIv;
    private ImageView heartIv;
    private TextView likeBtn;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    // 좋아요 로직에 사용
    Boolean isLike = false;
    DocumentReference explanationNameDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_explanation_screen);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        userTier = intent.getLongExtra("userTier", 0);
        explanationPicPath = intent.getStringExtra("imagePath");
        explanationLikes = intent.getLongExtra("likes", 0);
        explanationName = intent.getStringExtra("explanationName");
        subjectName = intent.getStringExtra("subjectName");
        problemName = intent.getStringExtra("problemName");
        userID = intent.getStringExtra("userID");

        likeCountTv = findViewById(R.id.likeCount);
        userNameTv = findViewById(R.id.showEx_name_tv);
        userTierIv = findViewById(R.id.showEx_tier_iv);
        explanationTv = findViewById(R.id.showEx_title);
        explanationIv = findViewById(R.id.showEx_image);
        heartIv = findViewById(R.id.heart);
        likeBtn = findViewById(R.id.likeButton);

        userNameTv.setText(userName);
        explanationTv.setText(explanationName);
        likeCountTv.setText(explanationLikes.toString());

        Log.d("showEx", explanationLikes.toString());
        db = FirebaseFirestore.getInstance();


        explanationNameDocRef = db.collection("문제")
                .document(subjectName)
                .collection(subjectName)
                .document(problemName)
                .collection(problemName)
                .document(explanationName);

        //이미 좋아요 한 문제인지 아닌지 확인
        db.collection("유저")
                .document(userID)
                .collection("좋아요 한 풀이")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String likedExplanationName = document.getId();

                            if(likedExplanationName.equals(userName+"님의 풀이")) {
                                isLike= true;
                                break;
                            }
                        }

                        //이미 좋아요 한 문제라면 빈 하트를 찬 하트로 변경
                        if(isLike){
                            ImageView likeImage = findViewById(R.id.heart);
                            likeImage.setImageDrawable(getResources().getDrawable(R.drawable.full_heart));
                        }
                    }
                });


        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child(explanationPicPath);

        Glide.with(ShowExplanationScreen.this)
                .load(pathReference)
                .into(explanationIv);

        switch (userTier.intValue()){
            case 1:
                userTierIv.setImageResource(R.drawable.rank_icons_s1);
                break;
            case 2:
                userTierIv.setImageResource(R.drawable.rank_icons_s2);
                break;
            case 3:
                userTierIv.setImageResource(R.drawable.rank_icons_s3);
                break;
            case 4:
                userTierIv.setImageResource(R.drawable.rank_icons_s4);
                break;
            case 5:
                userTierIv.setImageResource(R.drawable.rank_icons_s5);
                break;
            case 6:
                userTierIv.setImageResource(R.drawable.rank_icons_s6);
                break;
            case 7:
                userTierIv.setImageResource(R.drawable.rank_icons_s7);
                break;
            case 8:
                userTierIv.setImageResource(R.drawable.rank_icons_s8);
                break;
            case 9:
                userTierIv.setImageResource(R.drawable.rank_icons_s9);
                break;
            case 10:
                userTierIv.setImageResource(R.drawable.rank_icons_s10);
                break;
            case 11:
                userTierIv.setImageResource(R.drawable.rank_icons_s11);
                break;
            case 12:
                userTierIv.setImageResource(R.drawable.rank_icons_s12);
                break;
            case 13:
                userTierIv.setImageResource(R.drawable.rank_icons_s13);
                break;
            case 14:
                userTierIv.setImageResource(R.drawable.rank_icons_s14);
                break;
            case 15:
                userTierIv.setImageResource(R.drawable.rank_icons_s15);
                break;
            case 16:
                userTierIv.setImageResource(R.drawable.rank_icons_s16);
                break;
            case 17:
                userTierIv.setImageResource(R.drawable.rank_icons_s17);
                break;
            case 18:
                userTierIv.setImageResource(R.drawable.rank_icons_s18);
                break;
            case 19:
                userTierIv.setImageResource(R.drawable.rank_icons_s19);
                break;
            case 20:
                userTierIv.setImageResource(R.drawable.rank_icons_s20);
                break;
            case 21:
                userTierIv.setImageResource(R.drawable.rank_icons_s21);
                break;
            case 22:
                userTierIv.setImageResource(R.drawable.rank_icons_s22);
                break;
            case 23:
                userTierIv.setImageResource(R.drawable.rank_icons_s23);
                break;
            case 24:
                userTierIv.setImageResource(R.drawable.rank_icons_s24);
                break;
            case 25:
                userTierIv.setImageResource(R.drawable.rank_icons_s25);
                break;
            case 26:
                userTierIv.setImageResource(R.drawable.rank_icons_s26);
                break;
        }


    }

    public void likeButtonClicked(View v) {
        //이미 좋아요 한 문제라면 하트의 모양을 바꾸고 firestore의 좋아요 한 문제 리스트에서 제거
        if (isLike) {
            isLike = false;
            //좋아요 수 감소
            explanationNameDocRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();

                            Long like = document.getLong("좋아요 수");
                            like = like - 1;
                            explanationNameDocRef.update("좋아요 수", like);

                            likeCountTv.setText(like.toString());
                        }
                    });


            heartIv.setImageDrawable(getResources().getDrawable(R.drawable.empty_heart));

            db.collection("유저")
                    .document(userID)
                    .collection("좋아요 한 풀이")
                    .document(userName+ "님의 풀이")
                    .delete();
        }
        //좋아요 했던 문제가 아니라면 하트의 모양을 바꾸고 좋아요 한 문제 리스트에 추가
        else {
            isLike = true;
            //좋아요 수 추가
            explanationNameDocRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();

                            Long like = document.getLong("좋아요 수");
                            like = like + 1;
                            explanationNameDocRef.update("좋아요 수", like);

                            likeCountTv.setText(like.toString());
                        }
                    });

            heartIv.setImageDrawable(getResources().getDrawable(R.drawable.full_heart));

            Map<String, Object> base = new HashMap<>();
            base.put("base", 0);

            db.collection("유저")
                    .document(userID)
                    .collection("좋아요 한 풀이")
                    .document(userName+ "님의 풀이")
                    .set(base);
        }
    }


    public void goToMain(View view) {
        Intent homeIntent = new Intent(this, MainScreen.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

}