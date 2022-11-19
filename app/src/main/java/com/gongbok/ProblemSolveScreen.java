package com.gongbok;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    Long rating;
    Long tier;
    Boolean isLike = false;
    DocumentReference problemNameDocRef;

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

        //이미 좋아요 한 문제인지 아닌지 확인
        db.collection("유저")
                .document(userName)
                .collection("좋아요 한 문제")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String likedProblemName = document.getId();

                            if(likedProblemName.equals(subjectName+" "+problemName)) {
                                isLike = true;
                                break;
                            }
                        }

                        //이미 좋아요 한 문제라면 빈 하트를 찬 하트로 변경
                        if(isLike){
                            ImageView likeImage = findViewById(R.id.heart);
                            likeImage.setImageDrawable(getResources().getDrawable(R.drawable.full_heart));
                        }
                    }
                });

        problemNameDocRef = db.collection("문제")
                .document(subjectName)
                .collection(subjectName)
                .document(problemName);

        //처음 액티비티 생성 시 화면 구성
        problemNameDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    path = document.getString("경로");
                    tier = document.getLong("난이도");
                    rating = document.getLong("레이팅");
                    Long likeCount = document.getLong("좋아요 수");
                    Long trialCount = document.getLong("시도 횟수");
                    Long solvedCount = document.getLong("맞힌 횟수");

                    ImageView iv = findViewById(R.id.problemImage);
                    TextView trialCountTextView = findViewById(R.id.trialCount);
                    TextView solvedCountTextView = findViewById(R.id.solvedCount);
                    TextView likeCountTextView = findViewById(R.id.likeCount);

                    trialCountTextView.setText(String.valueOf(trialCount));
                    solvedCountTextView.setText(String.valueOf(solvedCount));
                    likeCountTextView.setText(String.valueOf(likeCount));

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

        problemNameDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();

                Long answer = document.getLong("정답");
                Long trialCount = document.getLong("시도 횟수");

                //문제의 시도 횟수 증가
                trialCount += 1;
                problemNameDocRef.update("시도 횟수", trialCount);

                if(userAnswer.equals(answer))
                    answerIsRight();
                else{
                    // 맞힌 문제 리스트에 있는지 확인
                    db.collection("유저")
                            .document(userName)
                            .collection("푼 문제")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    boolean isExistInRight = false;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if(document.getId().equals("base"))
                                            continue;

                                        String existProblemName = document.getString("문제 이름");

                                        if(existProblemName.equals(problemName))
                                            isExistInRight = true;
                                    }
                                    if(isExistInRight){
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ProblemSolveScreen.this);
                                        builder.setTitle("오답이지만 이미 맞히신 문제입니다.");

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
                                    else{
                                        //틀린 문제 리스트에 추가
                                        Map<String, Object> problemBase = new HashMap<>();
                                        problemBase.put("경로", path);
                                        problemBase.put("과목", subjectName);
                                        problemBase.put("문제 이름", problemName);
                                        problemBase.put("난이도", tier);
                                        problemBase.put("레이팅", rating);

                                        db.collection("유저")
                                                .document(userName)
                                                .collection("틀린 문제")
                                                .document(subjectName+" "+problemName)
                                                .set(problemBase);

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
            }
        });
    }

    public void answerIsRight(){
        //문제를 맞혔을 경우 틀린 문제 리스트에서 삭제
        db.collection("유저")
                .document(userName)
                .collection("틀린 문제")
                .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getId().equals("base"))
                                        continue;

                                    String existProblemName = document.getString("문제 이름");

                                    if(existProblemName.equals(problemName)){
                                        db.collection("유저")
                                                .document(userName)
                                                .collection("틀린 문제")
                                                .document(document.getId())
                                                .delete();
                                    }
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
                        boolean isExist = false;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String existProblemName = document.getString("문제 이름");

                            if(document.getId().equals("base"))
                                continue;
                            if(existProblemName.equals(problemName))
                                isExist = true;
                        }

                        if(isExist){
                            // 맞힌 문제 리스트에 이미 존재하는지 확인
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
                            problemBase.put("과목", subjectName);
                            problemBase.put("문제 이름", problemName);
                            problemBase.put("난이도", tier);
                            problemBase.put("레이팅", rating);

                            db.collection("유저")
                                    .document(userName)
                                    .collection("푼 문제")
                                    .document(subjectName+" "+problemName)
                                    .set(problemBase);

                            //문제를 처음 풀었을 때만 보상 포인트 제공
                            db.collection("유저")
                                    .document(userName)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot document = task.getResult();

                                            Long point = document.getLong("레이팅");

                                            db.collection("유저")
                                                    .document(userName)
                                                    .update("레이팅", point + rating);
                                        }
                                    });

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
    public void likeButtonClicked(View v){
        //이미 좋아요 한 문제라면 하트의 모양을 바꾸고 firestore의 좋아요 한 문제 리스트에서 제거
        if(isLike){
            isLike = false;

            ImageView likeImage = findViewById(R.id.heart);
            likeImage.setImageDrawable(getResources().getDrawable(R.drawable.empty_heart));

            db.collection("유저")
                    .document(userName)
                    .collection("좋아요 한 문제")
                    .document(subjectName+" "+problemName)
                    .delete();
        }
        //좋아요 했던 문제가 아니라면 하트의 모양을 바꾸고 좋아요 한 문제 리스트에 추가
        else{
            isLike = true;

            ImageView likeImage = findViewById(R.id.heart);
            likeImage.setImageDrawable(getResources().getDrawable(R.drawable.full_heart));

            Map<String, Object> problemBase = new HashMap<>();
            problemBase.put("과목", subjectName);
            problemBase.put("문제 이름", problemName);

            db.collection("유저")
                    .document(userName)
                    .collection("좋아요 한 문제")
                    .document(subjectName+" "+problemName)
                    .set(problemBase);
        }
    }
}