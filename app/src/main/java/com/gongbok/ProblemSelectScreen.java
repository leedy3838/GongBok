package com.gongbok;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ProblemSelectScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_select_screen);
    }

    public void goToMain(View view) {
        startActivity(new Intent(this, MainScreen.class));
    }

    public void goToProblemSolve(View view) {
        startActivity(new Intent(this, ProblemSolveScreen.class));
    }
}