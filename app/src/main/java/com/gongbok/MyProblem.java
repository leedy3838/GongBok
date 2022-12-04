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

public class MyProblem extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_problem);

        db = FirebaseFirestore.getInstance();
        Intent getIntent = getIntent();

        String userID = getIntent.getStringExtra("nickname");

        db.collection("유저")
                .document(userID)
                .collection("내가 올린 문제")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<MyProblemData> myProblemData = new LinkedList<>();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                if (document.getId().equals("base")) continue;
                                String problemName = document.getId();
                                String subjectName = document.getString("경로");
                                subjectName = subjectName.split("/")[0];

                                myProblemData.add(new MyProblemData(problemName, subjectName));
                            }
                            TextView myProblemCount = findViewById(R.id.MyProblemCounts);
                            myProblemCount.setText(""+myProblemData.size());

                            RecyclerView recyclerView = findViewById(R.id.MyProblemList);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyProblem.this);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            MyProblemAdapter myProblemAdapter = new MyProblemAdapter(myProblemData);

                            myProblemAdapter.setOnItemClickListener(new MyProblemAdapter.MyProblemOnClickLister() {
                                @Override
                                public void onItemClick(View v, MyProblemData myProblemData) {
                                    Intent intent = new Intent(MyProblem.this, SolvedProblemCheckScreen.class);
                                    intent.putExtra("problemName", myProblemData.subjectName + " " + myProblemData.problemName);
                                    intent.putExtra("subjectName", myProblemData.subjectName);
                                    startActivity(intent);
                                }
                            });

                            recyclerView.setAdapter(myProblemAdapter);
                        }
                    }
                });
    }

    public void goToBackInSPC(View view){
        finish();
    }
}