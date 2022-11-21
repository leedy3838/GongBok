package com.gongbok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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

    String userName = "LDY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        // 해당 유저에 관한 firebase 정보 다루기 위한 변수들
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //Firebase 세팅
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "로그인한 유저의 uid : " + user.getUid());

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
        String userID = "root712";
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

        //푼 문제 선택 타이틀 수정
        /*db.collection("유저")
                .document(userID)
                .collection("괴목 별 푼 문제")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private static final String TAG = "PASS";

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<ACProblem> pstitles = new LinkedList<>();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String problemName = document.getString("과목");
                                Long ACount = document.getLong("문제 수");
                                pstitles.add(new ACProblem(problemName, ACount));
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                questionTitle.sort(new Comparator<ACProblem>() {
                                    @Override
                                    public int compare(ACProblem problems1, ACProblem problems2) {
                                        return (int) (-problems1.ACount + problems2.ACount);
                                    } //더 큰 것이 앞으로 오도록 정렬
                                });
                            }
                            else{
                                //API 버전이 24와 같거나 낮은 경우
                            }
                            TextView ACPTitle1 = (TextView)findViewById(R.id.ACpTitle1);
                            ACPTitle1.setText(pstitles.get(0).titleName);
                            TextView ACPTitle2 = (TextView)findViewById(R.id.ACpTitle2);
                            ACPTitle2.setText(pstitles.get(1).titleName);
                            TextView ACPTitle3 = (TextView)findViewById(R.id.ACpTitle3);
                            ACPTitle3.setText(pstitles.get(2).titleName);
                            TextView ACPTitle4 = (TextView)findViewById(R.id.ACpTitle4);
                            ACPTitle4.setText(pstitles.get(3).titleName);
                            TextView ACPTitle5 = (TextView)findViewById(R.id.ACpTitle5);
                            ACPTitle5.setText(pstitles.get(4).titleName);
                            //아직 미구현
                        }
                    }
                });
        */
    }

    public void goToMyProblem(View view) {
        startActivity(new Intent(this, MyProblem.class));
    }

    public void goToTotalRanking(View view) {
        startActivity(new Intent(this, TotalRankingScreen.class));
    }

    public void goToProblemSolve(View view) {
        Intent intent = new Intent(this, SubjectSelectScreen.class);

        intent.putExtra("userName", userName);
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
        intent.putExtra("userName", userName);
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