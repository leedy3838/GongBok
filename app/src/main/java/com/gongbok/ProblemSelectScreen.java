package com.gongbok;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;

public class ProblemSelectScreen extends AppCompatActivity {
    List<ProblemData> DataList = new LinkedList<>();
    List<DataCompare> wrongList = new LinkedList<>();
    List<DataCompare> solvedList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_select_screen);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //정보를 이전 activity에서 받는 intent
        Intent getIntent = getIntent();
        String subjectName = getIntent.getStringExtra("subjectName");
        String userName = getIntent.getStringExtra("userName");


        TextView subjectNameTextView = findViewById(R.id.subjectName);
        subjectNameTextView.setText(subjectName);

        db.collection("유저")
                .document(userName)
                .collection("틀린 문제")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot document : task.getResult()){
                            if(document.getId().equals("base"))
                                continue;

                            String subjectNameInProblem = document.getString("과목");
                            String problemNameInProblme = document.getString("문제 이름");

                            wrongList.add(new DataCompare(subjectNameInProblem, problemNameInProblme));
                        }
                    }
                });

        db.collection("유저")
                .document(userName)
                .collection("푼 문제")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot document : task.getResult()){
                            if(document.getId().equals("base"))
                                continue;

                            String subjectNameInProblem = document.getString("과목");
                            String problemNameInProblme = document.getString("문제 이름");

                            solvedList.add(new DataCompare(subjectNameInProblem, problemNameInProblme));
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //정보를 이전 activity에서 받는 intent
        Intent getIntent = getIntent();
        String subjectName = getIntent.getStringExtra("subjectName");
        String userName = getIntent.getStringExtra("userName");

        Intent intent = new Intent(this, ProblemSolveScreen.class);
        intent.putExtra("subjectName", subjectName);
        intent.putExtra("userName", userName);

        db.collection("문제")
                .document(subjectName)
                .collection(subjectName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(TAG, "wrongList");
                        for(DataCompare i : wrongList){
                            Log.d(TAG, i.subjectName +" " + i.problemName);
                        }
                        Log.d(TAG, "solvedList");
                        for(DataCompare i : solvedList){
                            Log.d(TAG, i.subjectName +" " + i.problemName);
                        }
                        if (task.isSuccessful()) {
                            Log.d(TAG, "main");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String ProblemName = document.getId();
                                String path = document.getString("경로");
                                Long likeNum = document.getLong("좋아요 수");
                                Long tier = document.getLong("난이도");

                                boolean isSolved = false;
                                boolean isWrong = false;
                                if(solvedList.contains(new DataCompare(subjectName, ProblemName)))
                                    isSolved = true;
                                if(wrongList.contains(new DataCompare(subjectName, ProblemName)))
                                    isWrong = true;

                                Log.d(TAG, isSolved +" " + isWrong + " " + subjectName + " " +ProblemName);
                                ProblemData data = new ProblemData(ProblemName, path, likeNum, tier, isSolved, isWrong);
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
class DataCompare{
    String subjectName;
    String problemName;

    public DataCompare(String subjectName, String problemName){
        this.subjectName = subjectName;
        this.problemName = problemName;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        DataCompare data = (DataCompare)obj;
        if(this.subjectName.equals(data.subjectName) && this.problemName.equals(data.problemName))
            return true;
        else
            return false;
    }

}