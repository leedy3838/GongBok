package com.gongbok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SignUpScreen extends AppCompatActivity {

    private final String TAG = "SignUpScreen";
    EditText mRegisterEmailId;
    EditText mRegisterPwd;
    EditText mRegisterPwdCheck;
    EditText mRegisterNickname;
    Button mRegisterBtn;
    Button mNicknameDupCheckBtn;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_screen);

        firebaseAuth = FirebaseAuth.getInstance();
         db = FirebaseFirestore.getInstance();

        mRegisterEmailId = findViewById(R.id.registerEmailId);
        mRegisterPwd = findViewById(R.id.registerPwd);
        mRegisterPwdCheck = findViewById(R.id.registerPwdCheck);
        mRegisterNickname = findViewById(R.id.registerNickname);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mNicknameDupCheckBtn = findViewById(R.id.nicknameDupCheck);


        mNicknameDupCheckBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String name = mRegisterNickname.getText().toString().trim();
                db.collection("유저")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                boolean flag = false;
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (document.getId().equals(name)) {
                                            mRegisterNickname.setText(null);
                                            Toast.makeText(SignUpScreen.this, "이미 존재하는 닉네임입니다.\n 다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
                                            flag = true;
                                            break;
                                        }
                                    }
                                }

                                if (!flag) {
                                    Toast.makeText(SignUpScreen.this, "사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
                                    mRegisterBtn.setEnabled(true);
                                }
                            }
                        });
            }
        });

        mRegisterNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mRegisterBtn.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email = mRegisterEmailId.getText().toString().trim();
                String pwd = mRegisterPwd.getText().toString().trim();
                String pwdCheck = mRegisterPwdCheck.getText().toString().trim();
                String name = mRegisterNickname.getText().toString().trim();
                if( email.length() == 0 | pwd.length() == 0 | pwdCheck.length() == 0 | name.length() == 0){
                    Toast.makeText(SignUpScreen.this, "모든 정보를 입력하였는지 확인해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pwd.equals(pwdCheck)) {
                    Log.d(TAG, "등록 버튼 " + email + ", " + pwd);

                    firebaseAuth.createUserWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener(SignUpScreen.this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        user = firebaseAuth.getCurrentUser();
                                        // 최초로 생성된 닉네임이므로 firestore의 user 컬렉션에 document 추가
                                        Map<String, Object> userBase = new HashMap<>();
                                        userBase.put("레이팅", 0);
                                        userBase.put("티어", 0);
                                        userBase.put("올린 문제 수", 0);

                                        Map<String, Object> base = new HashMap<>();
                                        base.put("base", 0);

                                        db.collection("유저")
                                                .document(name)
                                                .set(userBase);

                                        db.collection("유저")
                                                .document(name)
                                                .collection("과목 별 푼 문제")
                                                .document("[고1]공통수학")
                                                .set(base);

                                        db.collection("유저")
                                                .document(name)
                                                .collection("과목 별 푼 문제")
                                                .document("[고2]수학1")
                                                .set(base);

                                        db.collection("유저")
                                                .document(name)
                                                .collection("과목 별 푼 문제")
                                                .document("[고2]수학2")
                                                .set(base);

                                        db.collection("유저")
                                                .document(name)
                                                .collection("과목 별 푼 문제")
                                                .document("[고등]기하")
                                                .set(base);

                                        db.collection("유저")
                                                .document(name)
                                                .collection("과목 별 푼 문제")
                                                .document("[고등]미적분")
                                                .set(base);

                                        db.collection("유저")
                                                .document(name)
                                                .collection("과목 별 푼 문제")
                                                .document("[고등]확률과 통계")
                                                .set(base);

                                        db.collection("유저")
                                                .document(name)
                                                .collection("과목 별 푼 문제")
                                                .document("[자유]기하학")
                                               .set(base);

                                        db.collection("유저")
                                                .document(name)
                                                .collection("내가 올린 문제")
                                                .document("base")
                                                .set(base);

                                        db.collection("유저")
                                                .document(name)
                                                .collection("내가 올린 풀이")
                                                .document("base")
                                                .set(base);

                                        db.collection("유저")
                                                .document(name)
                                                .collection("좋아요 한 문제")
                                                .document("base")
                                                .set(base);

                                        db.collection("유저")
                                                .document(name)
                                                .collection("틀린 문제")
                                                .document("base")
                                                .set(base);

                                        db.collection("유저")
                                                .document(name)
                                                .collection("푼 문제")
                                                .document("base")
                                                .set(base);

                                        // 가입 성공하였으므로 MainScreen으로 이동
                                        loginAfterSignUp(email, pwd, name);
                                        Toast.makeText(SignUpScreen.this, "회원가입 및 로그인 성공.", Toast.LENGTH_SHORT).show();
                                        finish();
                                                                            }
                                    else{
                                        // 이메일 아이디 중복으로 가입 실패하였으므로 지우고 다시 입력 받음
                                        mRegisterEmailId.setText(null);
                                        Toast.makeText(SignUpScreen.this, "이미 존재하는 이메일 아이디 입니다.\n다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            });
                }
                else{
                    // 비밀번호 확인 과정에서 실패하여 비밀번호 다시 입력 받음
                    mRegisterPwd.setText(null);
                    mRegisterPwdCheck.setText(null);
                    Toast.makeText(SignUpScreen.this, "비밀번호가 틀렸습니다. 다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    // EditText가 아닌 화면의 다른 곳을 클릭하면 소프트 키보드 내려감
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    // 돌아가기 버튼 누르면 다시 로그인 화면으로 돌아감
    public void goToLogin(View view) {
        startActivity(new Intent(this, LogInScreen.class));
    }

    // 회원가입 성공하였으므로 로그인하며 해당 유저의 UID를 "유저UID" collection에 추가하고 필드 값으로 닉네임 추가하고
    // MainScreen으로 이동
    public void loginAfterSignUp (String email, String pwd, String name){
        firebaseAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(SignUpScreen.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // 해당 유저의 UID를 "유저UID" collection에 추가하고 필드 값으로 닉네임 추가
                            user = firebaseAuth.getCurrentUser();
                            Map<String, Object> UidNickname = new HashMap<>();
                            UidNickname.put("닉네임", name);
                            db.collection("유저UID").document(user.getUid()).set(UidNickname);

                            // MainScreen으로 이동
                            startActivity(new Intent(SignUpScreen.this, MainScreen.class));
                        }
                    }
                });
    }
}

