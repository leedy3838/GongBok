package com.gongbok;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.database.collection.BuildConfig;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class SolvedProblemScreen extends AppCompatActivity {

    private final String TAG = "SolvedProblemScreen";

    class ACProblem{
        String titleName;
        int tempValue;
        ACProblem(String titleName, int tempValue){
            this.titleName = titleName;
            this.tempValue = tempValue;
        }
    }

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solved_problem_screen);

        db = FirebaseFirestore.getInstance();
        Intent getIntent = getIntent();

        String userID = getIntent.getStringExtra("userName");
        String solvedTitle = getIntent.getStringExtra("subjectName");
        int problemCount = getIntent.getIntExtra("problemCount", 0);

        db.collection("유저")
                .document(userID)
                .collection("과목 별 푼 문제")
                .document(solvedTitle)
                .collection(solvedTitle)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<SolvedProblemData> acProblems = new LinkedList<>();
                            TextView solvedCount;
                            solvedCount = findViewById(R.id.solvedCounts);
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String problemName = document.getId();
                                if (document.getId().equals("base")) continue;
                                acProblems.add(new SolvedProblemData(problemName));
                            }
                            solvedCount.setText(Integer.toString(problemCount));

                            TextView titleSubject = findViewById(R.id.solvedSubjectTitle);
                            titleSubject.setText(solvedTitle);

                            RecyclerView recyclerView = findViewById(R.id.solvedProblemList);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SolvedProblemScreen.this);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            SolvedProblemScreenAdapter solvedProblemScreenAdapter = new SolvedProblemScreenAdapter(acProblems);

                            solvedProblemScreenAdapter.setOnItemClickListener(new SolvedProblemScreenAdapter.SolvedOnItemClickListener() {
                                @Override
                                public void onItemClick(View v, SolvedProblemData solvedProblemData) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SolvedProblemScreen.this);
                                    builder.setTitle("이동할 화면을 선택하세요");

                                    builder.setPositiveButton("문제 보기", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            String problemName = solvedProblemData.problemName;

                                            Intent intent = new Intent(SolvedProblemScreen.this, SolvedProblemCheckScreen.class);
                                            intent.putExtra("problemName", problemName);
                                            intent.putExtra("subjectName", solvedTitle);
                                            startActivity(intent);
                                        }
                                    });

                                    builder.setNegativeButton("풀이 보기", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            String problemName = solvedProblemData.problemName;
                                            //problemName 이 [고등]미적분 2021년 9월 모의고사 23번 같은 형식으로 되어있음
                                            problemName = problemName.replace(solvedTitle+" ", "");
                                            //MainScreen.class 부분만 다른 것으로 대체 후 사용
                                            Intent intent = new Intent(SolvedProblemScreen.this, MainScreen.class);
                                            intent.putExtra("problemName", problemName);
                                            intent.putExtra("subjectName", solvedTitle);
                                            startActivity(intent);
                                        }
                                    });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                }
                            });
                            recyclerView.setAdapter(solvedProblemScreenAdapter);
                        }
                    }
                });
    }

}
