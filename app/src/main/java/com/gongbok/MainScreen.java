package com.gongbok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;
import androidx.recyclerview.widget.SortedListAdapterCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.Arrays;
import java.util.HashMap;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainScreen extends AppCompatActivity {


    private final String TAG = "MainScreen";
    class ACProblem{
        String titleName;
        Long ACount;
        ACProblem(String titleName, Long ACount){
            this.titleName = titleName;
            this.ACount = ACount;
        }

    }
    //Firebase
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String userID;
    private String userUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        Intent solvedDataIntent = new Intent(this, SolvedProblemScreen.class);
        // 해당 유저에 관한 firebase 정보 다루기 위한 변수들
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //Firebase 세팅
        db = FirebaseFirestore.getInstance();

        if(user != null){
            userUid = user.getUid();
            Log.d(TAG, userUid);
        }

        //임시 userID
        DocumentReference docRef = db.collection("유저UID")
                .document(user.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userID = document.getString("닉네임");
                        Log.d(TAG, "로그인한 유저의 닉네임1 : " + userID);
                        settingUser();
                    }
                }
            }
        });
    }
    
    public void logOut(View v) {
        // 이메일/패스워드 사용자 로그아웃
        firebaseAuth.signOut();
        // 구글 사용자 로그아웃
        setResult(RESULT_OK);
        finish();
    }

    // firebase에서 유저UID 컬렉션에서 얻어온 닉네임으로 메인 화면 세팅
    public void settingUser(){
        Log.d(TAG, "로그인한 유저의 닉네임2 : " + userID);
        //임시 userID Intent 들어오면 수정
        db.collection("유저")
                .document(userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            //화면에 레이팅 반영
                            Map<String, Object> fieldValues = task.getResult().getData();
                            Long rate = (Long) fieldValues.get("레이팅");
                            TextView ratingValue = (TextView) findViewById(R.id.ratingValue);
                            ratingValue.setText("Rating " + rate);

                            //화면에 닉네임 반영
                            TextView nickName = (TextView) findViewById(R.id.nickName);
                            nickName.setText(userID);

                            TextView restOfRating = findViewById(R.id.restOfRating);
                            if (rate < 5) restOfRating.setText("Next " + ((int) (5-rate)));
                            else if (rate < 25) restOfRating.setText("Next " + ((int) (25-rate)));
                            else if (rate < 40) restOfRating.setText("Next " + ((int) (40-rate)));
                            else if (rate < 60) restOfRating.setText("Next " + ((int) (60-rate)));
                            else if (rate < 120) restOfRating.setText("Next " + ((int) (120-rate)));
                            else if (rate < 200) restOfRating.setText("Next " + ((int) (200-rate)));
                            else if (rate < 300) restOfRating.setText("Next " + ((int) (300-rate)));
                            else if (rate < 400) restOfRating.setText("Next " + ((int) (400-rate)));
                            else if (rate < 500) restOfRating.setText("Next " + ((int) (500-rate)));
                            else if (rate < 600) restOfRating.setText("Next " + ((int) (600-rate)));
                            else if (rate < 1000) restOfRating.setText("Next " + ((int) (1000-rate)));
                            else if (rate < 1400) restOfRating.setText("Next " + ((int) (1400-rate)));
                            else if (rate < 1800) restOfRating.setText("Next " + ((int) (1800-rate)));
                            else if (rate < 2200) restOfRating.setText("Next " + ((int) (2200-rate)));
                            else if (rate < 3000) restOfRating.setText("Next " + ((int) (3000-rate)));
                            else if (rate < 4000) restOfRating.setText("Next " + ((int) (4000-rate)));
                            else if (rate < 5000) restOfRating.setText("Next " + ((int) (5000-rate)));
                            else if (rate < 6000) restOfRating.setText("Next " + ((int) (6000-rate)));
                            else if (rate < 7000) restOfRating.setText("Next " + ((int) (7000-rate)));
                            else if (rate < 10000) restOfRating.setText("Next " + ((int) (10000-rate)));
                            else if (rate < 13000) restOfRating.setText("Next " + ((int) (13000-rate)));
                            else if (rate < 16000) restOfRating.setText("Next " + ((int) (16000-rate)));
                            else if (rate < 19000) restOfRating.setText("Next " + ((int) (19000-rate)));
                            else if (rate < 22000) restOfRating.setText("Next " + ((int) (22000-rate)));
                            else if (rate < 30000) restOfRating.setText("Next " + ((int) (30000-rate)));
                            else restOfRating.setText("Next 0");
                        }
                    }
                });
        db.collection("유저")
                .document(userID)
                .collection("푼 문제")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){
                            List<RatingData> ratings = new LinkedList<>();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                if (document.getId().equals("base")) continue;
                                Long tier = document.getLong("레이팅");
                                ratings.add(new RatingData("title", tier));
                            }
                            RecyclerView recyclerView = findViewById(R.id.mainRatingScreen);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(MainScreen.this, 13);
                            recyclerView.setLayoutManager(gridLayoutManager);
                            MainScreenAdapter mainScreenAdapter = new MainScreenAdapter(ratings);
                            recyclerView.setAdapter(mainScreenAdapter);
                        }
                    }
                });

        db.collection("유저")
                .document(userID)
                .collection("과목 별 푼 문제")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private static final String TAG = "PASS";

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<MainSubjectData> selectTitle = new LinkedList<>();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String problemName = document.getId();
                                Long ACount = document.getLong("문제 수");
                                selectTitle.add(new MainSubjectData(problemName, ACount));
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                selectTitle.sort(new Comparator<MainSubjectData>() {
                                    @Override
                                    public int compare(MainSubjectData problems1, MainSubjectData problems2) {
                                        return (int) (-problems1.ACount + problems2.ACount);
                                    } //더 큰 것이 앞으로 오도록 정렬
                                });
                            }
                            else{
                                //API 버전이 24와 같거나 낮은 경우
                            }
                            RecyclerView recyclerView = findViewById(R.id.mainSubjectTitle);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainScreen.this);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            MainSubjectSelectAdapter mainSubjectSelectAdapter = new MainSubjectSelectAdapter(selectTitle);

                            mainSubjectSelectAdapter.setOnItemClickListener(new MainSubjectSelectAdapter.MainSubjectClickListener() {
                                @Override
                                public void onItemClick(View v, MainSubjectData mainSubjectData) {
                                    String subjectName = mainSubjectData.subjectName;

                                    Intent intent = new Intent(MainScreen.this, SolvedProblemScreen.class);
                                    intent.putExtra("subjectName", mainSubjectData.subjectName);
                                    intent.putExtra("userName", userID);
                                    intent.putExtra("problemCount", (int)(mainSubjectData.ACount-1));
                                    startActivity(intent);
                                }
                            });
                            recyclerView.setAdapter(mainSubjectSelectAdapter);
                        }
                    }
                });
    }

    public void goToMyProblem(View view) {
        startActivity(new Intent(this, MyProblem.class));
    }

    public void goToTotalRanking(View view) {
        startActivity(new Intent(this, TotalRankingScreen.class));
    }

    public void goToProblemSolve(View view) {
        Intent intent = new Intent(this, SubjectSelectScreen.class);

        intent.putExtra("userName", userID);
        startActivity(intent);
    }

    public void goToMain(View view) {
        startActivity(new Intent(this, MainScreen.class));
    }

    public void goToEnrollProblem(View view) {
        ArrayList<Map<String, Integer>> ratingMap = new ArrayList<Map<String, Integer>>();
        ratingMap.add(getTierRating());
        Intent intent = new Intent(this, EnrollProblemScreen.class);
        intent.putExtra("nickname", userID);
        intent.putExtra("tierRating", ratingMap);
        startActivity(intent);
    }

    public void goToMyLikeProblem(View v){
        Intent intent = new Intent(this, ProblemSelectScreen.class);
        intent.putExtra("subjectName", "내가 좋아요 한 문제");
        intent.putExtra("userName", userID);
        startActivity(intent);
    }

    // 티어 레이팅 Map 객체 반환하는 함수
    public Map<String, Integer> getTierRating() {

        Map<String, Integer> Problemrating = new HashMap<>();

        Problemrating.put("브론즈5", 1);
        Problemrating.put("브론즈4", 2);
        Problemrating.put("브론즈3", 3);
        Problemrating.put("브론즈2", 4);
        Problemrating.put("브론즈1", 5);

        Problemrating.put("실버5", 12);
        Problemrating.put("실버4", 14);
        Problemrating.put("실버3", 16);
        Problemrating.put("실버2", 18);
        Problemrating.put("실버1", 20);

        Problemrating.put("골드5", 50);
        Problemrating.put("골드4", 55);
        Problemrating.put("골드3", 60);
        Problemrating.put("골드2", 65);
        Problemrating.put("골드1", 70);

        Problemrating.put("플래티넘5", 200);
        Problemrating.put("플래티넘4", 210);
        Problemrating.put("플래티넘3", 220);
        Problemrating.put("플래티넘2", 230);
        Problemrating.put("플래티넘1", 240);

        Problemrating.put("다이아몬드5", 520);
        Problemrating.put("다이아몬드4", 540);
        Problemrating.put("다이아몬드3", 560);
        Problemrating.put("다이아몬드2", 580);
        Problemrating.put("다이아몬드1", 600);

        Problemrating.put("민트", 1000);

        return Problemrating;
    }
}