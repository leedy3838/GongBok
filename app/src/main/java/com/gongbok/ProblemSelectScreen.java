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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ProblemSelectScreen extends AppCompatActivity {
    List<ProblemData> DataList = new LinkedList<>();
    List<DataCompare> wrongList = new LinkedList<>();
    List<DataCompare> solvedList = new LinkedList<>();

    int sortMode = 0;

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

        //spinner 설정
        Spinner sortSpinner = findViewById(R.id.sortSpinner);
        String[] sortItems = getResources().getStringArray(R.array.sort);

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, sortItems);

        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sortSpinner.setAdapter(sortAdapter);


        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortMode = position;
                notifySpinnerIsChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //정보를 이전 activity에서 받는 intent
        Intent getIntent = getIntent();
        String subjectName = getIntent.getStringExtra("subjectName");
        String userName = getIntent.getStringExtra("userName");

        //MyLikeProblemScreen에서 왔을 때
        if(subjectName.equals("내가 좋아요 한 문제")){
            //문제 분류 안보이게 하기
            TextView disable = findViewById(R.id.문제분류);
            disable.setVisibility(View.GONE);

            Intent intent = new Intent(this, ProblemSolveScreen.class);

            db.collection("유저")
                    .document(userName)
                    .collection("좋아요 한 문제")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getId().equals("base"))
                                    continue;

                                String subject = document.getString("과목");
                                String problemNameOfLike = document.getString("문제 이름");

                                Log.d(TAG, subject +" " + problemNameOfLike);

                                String path = document.getString("경로");
                                Long likeNum = document.getLong("좋아요 수");
                                Long tier = document.getLong("난이도");
                                Long tryNum = document.getLong("시도 횟수");

                                boolean isSolved = false;
                                boolean isWrong = false;
                                if (solvedList.contains(new DataCompare(subject, problemNameOfLike)))
                                    isSolved = true;
                                if (wrongList.contains(new DataCompare(subject, problemNameOfLike)))
                                    isWrong = true;

                                ProblemData data = new ProblemData(problemNameOfLike, subject, path, likeNum, tier, tryNum, isSolved, isWrong, true);
                                DataList.add(data);
                            }

                            //RecyclerView에 목록 출력
                            RecyclerView recyclerView = findViewById(R.id.problemList);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ProblemSelectScreen.this));

                            ProblemAdapter adapter = new ProblemAdapter(DataList);
                            adapter.setOnItemClickListener(new ProblemAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View v, ProblemData data) {
                                    intent.putExtra("userName", userName);
                                    intent.putExtra("problemName", data.name);
                                    intent.putExtra("subjectName", data.subjectName);
                                    startActivity(intent);
                                }
                            });

                            recyclerView.setAdapter(adapter);
                        }
                    });
        }

        //과목 선택을 통해서 왔을 때
        else {
            Intent intent = new Intent(this, ProblemSolveScreen.class);

            db.collection("문제")
                    .document(subjectName)
                    .collection(subjectName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String ProblemName = document.getId();
                                    String path = document.getString("경로");
                                    Long likeNum = document.getLong("좋아요 수");
                                    Long tier = document.getLong("난이도");
                                    Long tryNum = document.getLong("시도 횟수");

                                    boolean isSolved = false;
                                    boolean isWrong = false;
                                    if (solvedList.contains(new DataCompare(subjectName, ProblemName)))
                                        isSolved = true;
                                    if (wrongList.contains(new DataCompare(subjectName, ProblemName)))
                                        isWrong = true;

                                    ProblemData data = new ProblemData(ProblemName, subjectName, path, likeNum, tier, tryNum, isSolved, isWrong, false);
                                    DataList.add(data);
                                }

                                //RecyclerView에 목록 출력
                                RecyclerView recyclerView = findViewById(R.id.problemList);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ProblemSelectScreen.this));

                                ProblemAdapter adapter = new ProblemAdapter(DataList);
                                adapter.setOnItemClickListener(new ProblemAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, ProblemData data) {
                                        intent.putExtra("userName", userName);
                                        intent.putExtra("problemName", data.name);
                                        intent.putExtra("subjectName", data.subjectName);
                                        startActivity(intent);
                                    }
                                });

                                recyclerView.setAdapter(adapter);
                            }
                        }
                    });
        }
    }

    public void goToMain(View view) {
        Intent homeIntent = new Intent(this, MainScreen.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    public void goToProblemSolve(View view) {
        startActivity(new Intent(this, ProblemSolveScreen.class));
    }

    //spinner가 바꼈을 때 RecyclerView를 갱신
    public void notifySpinnerIsChanged(){
        Intent getIntent = getIntent();
        String userName = getIntent.getStringExtra("userName");
        //RecyclerView에 목록 출력
        RecyclerView recyclerView = findViewById(R.id.problemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProblemSelectScreen.this));

        //DataList를 정렬 모드에 따라서 정렬
        Collections.sort(DataList, new Comparator<ProblemData>() {
            @Override
            public int compare(ProblemData data1, ProblemData data2) {
                if(sortMode == 0)
                    return data1.name.compareTo(data2.name);
                else if(sortMode == 1)
                    return (int)(data2.likeNum - data1.likeNum);
                else
                    return (int)(data2.tier - data1.tier);
            }
        });

        ProblemAdapter adapter = new ProblemAdapter(DataList);
        adapter.setOnItemClickListener(new ProblemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, ProblemData data) {

                Intent intent = new Intent(ProblemSelectScreen.this, ProblemSolveScreen.class);
                intent.putExtra("userName", userName);
                intent.putExtra("problemName", data.name);
                intent.putExtra("subjectName", data.subjectName);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainScreen.class));
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