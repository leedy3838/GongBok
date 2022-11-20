package com.gongbok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.SortedList;
import androidx.recyclerview.widget.SortedListAdapterCallback;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
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
import java.util.List;
import java.util.Map;

public class MainScreen extends AppCompatActivity {

    private final String TAG = "MainScreen";
    //Firebase
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String userID;

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
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> fieldValues = task.getResult().getDocuments();
                            long problemRates[] = new long[fieldValues.size()];
                            for (int i = 0; i < fieldValues.size(); i++) {

                            }
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
        startActivity(new Intent(this, SubjectSelectScreen.class));
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