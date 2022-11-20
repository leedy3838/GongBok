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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.Arrays;
import java.util.HashMap;

import android.widget.Button;
import android.widget.ImageButton;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainScreen extends AppCompatActivity {

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
    
    private GoogleSignInAccount gsa;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        firebaseAuth = FirebaseAuth.getInstance();

        //Firebase 세팅
        db = FirebaseFirestore.getInstance();

        //임시 userID Intent 들어오면 수정
        String userID = "root712";
        db.collection("유저")
                .document(userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            //화면에 레이팅 반영
                            Map<String, Object> fieldValues = task.getResult().getData();
                            Long rate = (Long)fieldValues.get("레이팅");
                            TextView ratingValue = (TextView)findViewById(R.id.ratingValue);
                            ratingValue.setText("Rating " + rate);

                            //화면에 닉네임 반영
                            TextView nickName = (TextView)findViewById(R.id.nickName);
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
    
    public void logOut(View v) {
        // 이메일/패스워드 사용자 로그아웃
        firebaseAuth.signOut();
        // 구글 사용자 로그아웃
        setResult(RESULT_OK);

        finish();
    }
    public void goToMyProblem(View view) {
        startActivity(new Intent(this, MyProblem.class));
    }

    public void goToTotalRanking(View view) {
        startActivity(new Intent(this, TotalRankingScreen.class));
    }

    public void goToProblemSolve(View view) {
        startActivity(new Intent(this, SubjectSelectScreen.class));
    }

    public void goToMain(View view) {
        startActivity(new Intent(this, MainScreen.class));
    }

    public void goToEnrollProblem(View view) {
        startActivity(new Intent(this, EnrollProblemScreen.class));
    }
}