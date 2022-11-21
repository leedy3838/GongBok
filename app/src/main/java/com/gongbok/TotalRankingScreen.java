package com.gongbok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class TotalRankingScreen extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.total_ranking_screen);

        db = FirebaseFirestore.getInstance();
        db.collection("유저")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<RankingData> ranking = new LinkedList<>();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String nickName = document.getId();
                                Long rating = document.getLong("레이팅");
                                ranking.add(new RankingData(nickName, rating));
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                                ranking.sort(new Comparator<RankingData>() {
                                    @Override
                                    public int compare(RankingData rankingData1, RankingData rankingData2) {
                                        return (int) (rankingData2.rating-rankingData1.rating);
                                    }
                                });
                            }
                            for (int i=0;i<ranking.size();i++){
                                ranking.get(i).nickName = "#" + (i+1) + " " + ranking.get(i).nickName;
                            }
                            RecyclerView recyclerView = findViewById(R.id.RankingList);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TotalRankingScreen.this);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            RankingScreenAdapter rankingScreenAdapter = new RankingScreenAdapter(ranking);
                            recyclerView.setAdapter(rankingScreenAdapter);
                        }
                    }
                });
    }
    public void goToProblemSolve(View view) {
        startActivity(new Intent(this, SubjectSelectScreen.class));
    }

    public void goToMain(View view) {
        startActivity(new Intent(this, MainScreen.class));
    }

    public void goToEnrollProblem(View view) {
        startActivity(new Intent(this, EnrollProblemScreen.class));
    }
}