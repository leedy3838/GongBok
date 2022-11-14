package com.gongbok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProblemSolveScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_solve_screen);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        Intent getIntent = getIntent();
        String subjectName = getIntent.getStringExtra("과목 이름");
        String problemName = getIntent.getStringExtra("문제 이름");

        DocumentReference docRef = db.collection("문제")
                .document(subjectName)
                .collection(subjectName)
                .document(problemName);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String path = document.getString("경로");
                        ImageView iv = findViewById(R.id.problemImage);

                        StorageReference storageRef = storage.getReference();
                        StorageReference pathReference = storageRef.child(path);

                        Glide.with(ProblemSolveScreen.this)
                                .load(pathReference)
                                .into(iv);
                    }
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

    public void goToEnrollProblem(View view) {
        startActivity(new Intent(this, EnrollProblemScreen.class));
    }
}