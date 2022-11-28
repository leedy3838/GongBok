package com.gongbok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class SolvedProblemCheckScreen extends AppCompatActivity {

    String problemName;
    String subjectName;

    String path;

    Long tier;
    Long rating;
    Long likeNum;
    Long trialCount;
    Long solvedCount;
    Long answer;

    DocumentReference problemRef;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solved_problem_check_screen);

        Intent getIntent = getIntent();
        problemName = getIntent.getStringExtra("problemName");
        subjectName = getIntent.getStringExtra("subjectName");
        problemName = problemName.replace(subjectName+" ", "");

        problemRef = db.collection("문제")
                .document(subjectName)
                .collection(subjectName)
                .document(problemName);

        problemRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();

                    path = document.getString("경로");
                    tier = document.getLong("난이도");
                    rating = document.getLong("레이팅");
                    likeNum = document.getLong("좋아요 수");
                    answer = document.getLong("정답");

                    trialCount = document.getLong("시도 횟수");
                    solvedCount = document.getLong("맞힌 횟수");

                    TextView titleName = findViewById(R.id.solvedProblemName);
                    TextView subjectTitle = findViewById(R.id.solvedProblemSubjectTitle);

                    titleName.setText(problemName);
                    subjectTitle.setText(subjectName);

                    ImageView iv = findViewById(R.id.solvedProblemImage);

                    StorageReference storageReference = storage.getReference();
                    StorageReference pathReference = storageReference.child(path);
                    Log.d("PASS:PATH", path);
                    Glide.with(SolvedProblemCheckScreen.this)
                            .load(pathReference)
                            .into(iv);

                    TextView problemTrialCount = findViewById(R.id.solvedProblemTrialCount);
                    TextView problemSolvedCount = findViewById(R.id.solvedProblemSolvedCount);
                    TextView problemLikes = findViewById(R.id.solvedProblemLikes);
                    TextView problemAnswer = findViewById(R.id.solvedProblemAnswer);

                    problemTrialCount.setText("trial count : " + trialCount);
                    problemSolvedCount.setText("solved count : " + solvedCount);
                    problemLikes.setText(Long.toString(likeNum) + " likes");
                    problemAnswer.setText(Long.toString(answer));
                }
            }
        });
    }

    public void goToSolvedProblemScreen(View view){
        finish();
    }


}