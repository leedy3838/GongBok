package com.gongbok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;

public class ProblemSelectScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_select_screen);

        List<ProblemData> DataList = new LinkedList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //정보를 이전 activity에서 받는 intent
        Intent getIntent = getIntent();
        String subjectName = getIntent.getStringExtra("subjectName");
        String userName = getIntent.getStringExtra("userName");

        Intent intent = new Intent(this, ProblemSolveScreen.class);
        intent.putExtra("subjectName", subjectName);
        intent.putExtra("userName", userName);

        TextView subjectNameTextView = findViewById(R.id.subjectName);
        subjectNameTextView.setText(subjectName);

        db.collection("문제")
                .document(subjectName)
                .collection(subjectName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String ProblemName = document.getId();
                                String path = document.getString("경로");
                                Long likeNum = document.getLong("좋아요 수");
                                Long tier = document.getLong("난이도");

                                ProblemData data = new ProblemData(ProblemName, path, likeNum, tier);
                                DataList.add(data);
                            }

                            //RecyclerView에 목록 출력
                            RecyclerView recyclerView = findViewById(R.id.problemList) ;
                            recyclerView.setLayoutManager(new LinearLayoutManager(ProblemSelectScreen.this));

                            ProblemAdapter adapter = new ProblemAdapter(DataList);
                            adapter.setOnItemClickListener(new ProblemAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View v, ProblemData data) {
                                    intent.putExtra("problemName", data.name);
                                    startActivity(intent);
                                }
                            });
                            recyclerView.setAdapter(adapter);
                        }
                    }
                });
    }

    public void goToMain(View view) {
        startActivity(new Intent(this, MainScreen.class));
    }

    public void goToProblemSolve(View view) {
        startActivity(new Intent(this, ProblemSolveScreen.class));
    }
}