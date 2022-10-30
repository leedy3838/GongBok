package com.gongbok;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ShowExplanationNextScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_explanation_next_screen);
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