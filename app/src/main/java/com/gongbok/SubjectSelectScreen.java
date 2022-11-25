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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;

public class SubjectSelectScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_select_screen);

        Intent intent = new Intent(this, ProblemSelectScreen.class);

        Intent getIntent = getIntent();
        String userName = getIntent.getStringExtra("userName");

        List<SubjectData> DataList = new LinkedList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("문제")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String subjectName = document.getId();
                                Long problemNum = document.getLong("문제 수");
                                // 추후 이 공간에 유저가 올린 문제 수를 MyProblemNum에 넣는 코드 필요

                                SubjectData data = new SubjectData(subjectName, problemNum);
                                DataList.add(data);
                            }

                            //RecyclerView에 목록 출력
                            RecyclerView recyclerView = findViewById(R.id.subjectList) ;
                            recyclerView.setLayoutManager(new LinearLayoutManager(SubjectSelectScreen.this));

                            SubjectAdapter adapter = new SubjectAdapter(DataList);
                            adapter.setOnItemClickListener(new SubjectAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View v, SubjectData data) {
                                    String subjectName = data.name;

                                    //과목을 선택하면 그에 해당하는 과목의 문제 리스트 출력
                                    intent.putExtra("subjectName", subjectName);
                                    intent.putExtra("userName", userName);
                                    startActivity(intent);
                                }
                            });

                            recyclerView.setAdapter(adapter);

                            //전체 문제 수 출력
                            TextView totalProblemNum = findViewById(R.id.totalProblemNum);
                            int problemSum = 0;
                            for(int i = 0; i<DataList.size(); i++)
                                problemSum += DataList.get(i).count;
                            //setText 안에 int 쓰면 안되더라구요...
                            totalProblemNum.setText(String.valueOf(problemSum));
                        }
                    }
                });
    }

    public void goToMain(View view) {
        startActivity(new Intent(this, MainScreen.class));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainScreen.class));
    }
}