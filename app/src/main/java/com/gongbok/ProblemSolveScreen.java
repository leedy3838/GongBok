package com.gongbok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ProblemSolveScreen extends AppCompatActivity {
    String subjectName;
    String problemName;
    String userName;
    String path;
    DocumentReference docRef;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_solve_screen);

        Intent getIntent = getIntent();
        subjectName = getIntent.getStringExtra("subjectName");
        problemName = getIntent.getStringExtra("problemName");
        userName = getIntent.getStringExtra("userName");

        TextView subjectNameTextView = findViewById(R.id.subjectName);
        subjectNameTextView.setText(subjectName);

        docRef = db.collection("문제")
                .document(subjectName)
                .collection(subjectName)
                .document(problemName);

        //처음 액티비티 생성 시 화면 구성
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    path = document.getString("경로");
                    Long trialCount = document.getLong("시도 횟수");
                    Long solvedCount = document.getLong("맞힌 횟수");

                    ImageView iv = findViewById(R.id.problemImage);
                    TextView trialCountTextView = findViewById(R.id.trialCount);
                    TextView solvedCountTextView = findViewById(R.id.solvedCount);

                    trialCountTextView.setText(String.valueOf(trialCount));
                    solvedCountTextView.setText(String.valueOf(solvedCount));

                    StorageReference storageRef = storage.getReference();
                    StorageReference pathReference = storageRef.child(path);

                    Glide.with(ProblemSolveScreen.this)
                            .load(pathReference)
                            .into(iv);
                }
            }
        });
    }

    public void goToMain(View view) {
        startActivity(new Intent(this, MainScreen.class));
    }

    public void goToProblemSolve(View view) {
        startActivity(new Intent(this, SubjectSelectScreen.class));
    }

    public void goToEnrollProblem(View view) {
        startActivity(new Intent(this, EnrollProblemScreen.class));
    }

    public void submitButtonClicked(View view){
        EditText inputAnswer = findViewById(R.id.inputAnswer);
        Long userAnswer = Long.parseLong(inputAnswer.getText().toString().trim());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();

                Long answer = document.getLong("정답");
                Long trialCount = document.getLong("시도 횟수");

                if(userAnswer.equals(answer))
                    answerIsRight();
                else{
                    //틀린 문제 리스트에 추가
                    Map<String, Object> problemBase = new HashMap<>();
                    problemBase.put("경로", path);

                    db.collection("유저")
                            .document(userName)
                            .collection("틀린 문제")
                            .document(problemName)
                            .set(problemBase);

                    trialCount += 1;
                    docRef.update("시도 횟수", trialCount);

                    AlertDialog.Builder builder = new AlertDialog.Builder(ProblemSolveScreen.this);
                    builder.setTitle("틀리셨습니다.");

                    builder.setPositiveButton("다시 풀어보기", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int pos) {

                        }
                    });
                    builder.setNegativeButton("그만 풀기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(ProblemSolveScreen.this, MainScreen.class));
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
    }

    public void answerIsRight(){
        db.collection("유저")
                .document(userName)
                .collection("푼 문제")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        boolean isExist = false;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String existProblemName = document.getId();

                            if(existProblemName.equals(problemName))
                                isExist = true;
                        }

                        if(isExist){
                            AlertDialog.Builder builder = new AlertDialog.Builder(ProblemSolveScreen.this);
                            builder.setTitle("이미 푼 문제입니다.");

                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int pos) {
                                    startActivity(new Intent(ProblemSolveScreen.this, MainScreen.class));
                                }
                            });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                        else{
                            //푼 문제 리스트에 추가
                            Map<String, Object> problemBase = new HashMap<>();
                            problemBase.put("경로", path);

                            db.collection("유저")
                                    .document(userName)
                                    .collection("푼 문제")
                                    .document(problemName)
                                    .set(problemBase);

                            AlertDialog.Builder builder = new AlertDialog.Builder(ProblemSolveScreen.this);
                            builder.setTitle("문제를 맞추셨습니다.");

                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int pos) {
                                    startActivity(new Intent(ProblemSolveScreen.this, MainScreen.class));
                                }
                            });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }
                });
    }
}