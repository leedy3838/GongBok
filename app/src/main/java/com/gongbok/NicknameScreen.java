package com.gongbok;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class NicknameScreen extends AppCompatActivity {

    EditText mGoogleNicknameInputEt;
    TextView mGoogleNicknameRegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nickname_screen);

        Log.d("GoogleLogin", "실행3");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mGoogleNicknameRegisterBtn = findViewById(R.id.registerGoogleNickname);
        mGoogleNicknameInputEt = findViewById(R.id.inputGoogleNickname);


        mGoogleNicknameRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mGoogleNicknameInputEt.getText().toString().trim();
                db.collection("유저")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                boolean flag = false;
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (document.getId().equals(name)) {
                                            // 중복되는 닉네임 있으므로 EditText 값 비우고 다시 입력 요청
                                            mGoogleNicknameInputEt.setText(null);
                                            Toast.makeText(NicknameScreen.this, "이미 존재하는 닉네임입니다.\n다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
                                            flag = true;
                                            break;

                                        }
                                    }
                                    if (!flag) {
                                        Log.d("Google","실행5");
                                        Toast.makeText(NicknameScreen.this, "닉네임이 설정 완료되었습니다", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(NicknameScreen.this, LogInScreen.class);
                                        intent.putExtra("nickname", name);
                                        setResult(RESULT_OK, intent);
                                        // Activity.RESULT_OK 로 하면 오류 발생
                                        finish();
                                    }
                                }
                            }
                        });
            }
        });


    }


}
