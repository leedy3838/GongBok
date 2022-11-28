package com.gongbok;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;

public class ExplanationSelectScreen extends AppCompatActivity {

    String subjectName;
    String problemName;

    // ExplanationData 구성 요소
    String userName;
    Long userTier;
    String explanationName;
    String explanationPicPath;
    Long explanationLikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explanation_select_screen);

        Intent showIntent = new Intent(this, ShowExplanationScreen.class);

        Intent intent = getIntent();
        subjectName = intent.getStringExtra("subjectName");
        problemName = intent.getStringExtra("problemName");


        Log.d("select", subjectName + " " + problemName);

        List<ExplanationData> DataList = new LinkedList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        CollectionReference colRef = db.collection("문제")
                .document(subjectName)
                .collection(subjectName)
                .document(problemName)
                .collection(problemName);

        colRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if(document.getId().equals("base"))
                                    continue;

                                explanationName = document.getId();
                                userName = document.getString("등록자명");
                                userTier = document.getLong("등록자 티어");
                                explanationPicPath = document.getString("경로");
                                explanationLikes = document.getLong("좋아요 수");

                                ExplanationData data = new ExplanationData(explanationName, userName, userTier, explanationPicPath, explanationLikes);
                                DataList.add(data);
                            }

                            //RecyclerView에 목록 출력
                            RecyclerView recyclerView = findViewById(R.id.explanationList) ;
                            recyclerView.setLayoutManager(new GridLayoutManager(ExplanationSelectScreen.this, 2));

                            ExplanationAdapter adapter = new ExplanationAdapter(ExplanationSelectScreen.this, DataList);
                            adapter.setOnItemClickListener(new ExplanationAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View v, ExplanationData data) {

                                    showIntent.putExtra("subjectName", subjectName);
                                    showIntent.putExtra("problemName", problemName);
                                    showIntent.putExtra("userName", data.userName);
                                    showIntent.putExtra("userTier", data.userTier);
                                    showIntent.putExtra("imagePath", data.explanationPicPath);
                                    showIntent.putExtra("likes", data.explanationLikes);
                                    showIntent.putExtra("explanationName", data.explanationName);

                                    startActivity(showIntent);
                                }
                            });

                            recyclerView.setAdapter(adapter);
                        }
                    }
                });

    }

    public void goToMain(View view) {
        Intent homeIntent = new Intent(this, MainScreen.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}