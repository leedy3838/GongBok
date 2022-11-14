package com.gongbok;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LogInScreen extends AppCompatActivity {
    //일반 로그인에 사용
    Button mLoginBtn;
    SignInButton mGoogleLogInBtn;
    Button mSignupBtn;
    private EditText mLoginID;
    private EditText mLoginPassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;


    //구글 로그인에 사용
    private String TAG = "GoogleLoginActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount gsa;
    private FirebaseUser user;
    String googleNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        // 파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();
        // 파이어스토어 다루기 위한 객체 선언
        db = FirebaseFirestore.getInstance();

        // 이메일/패스워드로 로그인
        mLoginBtn = findViewById(R.id.logInBtn);
        mGoogleLogInBtn = findViewById(R.id.googleLoginBtn);
        mSignupBtn = findViewById(R.id.signupBtn);

        mLoginID = findViewById(R.id.logInId);
        mLoginPassword = findViewById(R.id.logInPassword);

        mLoginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email = mLoginID.getText().toString().trim();
                String pwd = mLoginPassword.getText().toString().trim();
                firebaseAuth.signInWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(LogInScreen.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    startActivity(new Intent(LogInScreen.this, MainScreen.class));
                                    Toast.makeText(LogInScreen.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(LogInScreen.this, "로그인 오류 발생", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // 구글로 로그인


        // Google 로그인을 앱에 통합
        // GoogleSignInOptions 개체를 구성할 때 requestIdToken을 호출

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        mGoogleLogInBtn = findViewById(R.id.googleLoginBtn);
        mGoogleLogInBtn.setOnClickListener(view -> {
            // 기존에 로그인 했던 계정을 확인한다.
            gsa = GoogleSignIn.getLastSignedInAccount(LogInScreen.this);

            if (gsa != null) { // 로그인 되있는 경우
                Toast.makeText(LogInScreen.this, "이미 로그인되어 있습니다.", Toast.LENGTH_SHORT).show();
                startMainScreen();
            }
            else
                signIn();
        });
    }

    // 구글 회원가입 or 로그인 과정의 시작
    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startForResult.launch(signInIntent);
    }

    private final ActivityResultLauncher<Intent> startForResult =
        registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            // The Task returned from this call is always completed, no need to attach a listener.
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                            handleSignInResult(task);
                        }
                    }
                });




    // 사용자 정보 가져오기
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

            if (acct != null) {
                firebaseAuthWithGoogle(acct.getIdToken());

                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();

                Log.d(TAG, "handleSignInResult:personName "+personName);
                Log.d(TAG, "handleSignInResult:personGivenName "+personGivenName);
                Log.d(TAG, "handleSignInResult:personEmail "+personEmail);
                Log.d(TAG, "handleSignInResult:personId "+personId);
                Log.d(TAG, "handleSignInResult:personFamilyName "+personFamilyName);
                Log.d(TAG, "handleSignInResult:personPhoto "+personPhoto);
            }

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        user = firebaseAuth.getCurrentUser();

                        db.collection("구글uid")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            boolean flag = false;
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                // 기존 사용자
                                                if (document.getId().equals(user.getUid())) {
                                                    // 로그인 성공하였으므로 메인화면으로 이동 (로그아웃 버튼 인식 포함)
                                                    Toast.makeText(LogInScreen.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                                    flag = true;
                                                    startMainScreen();
                                                }
                                            }

                                            //if(!flag)가 없으면 로그인 성공해도 startMainScreen()이 아닌 getNickname() 실행됨
                                            if(!flag) {
                                                // 처음 가입하는 사용자
                                                // fireStore에 유저 Uid 등록
                                                Map<String, Object> googleUid = new HashMap<>();
                                                googleUid.put("uid", user.getUid());

                                                db.collection("구글uid").document(user.getUid()).set(googleUid);
                                                // 닉네임 설정 화면으로 이동
                                                getNickname();
                                                // startMainScreen(); 은 getNickname()의 콜백함수에서 실행행
                                           }
                                        }
                                    }
                                });
                    }
                    else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LogInScreen.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    // 회원 삭제요청
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, task -> {
                    // ...
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }


    public void goToRegister(View view) {
        startActivity(new Intent(this, SignUpScreen.class));
    }

    // 구글 유저들의 닉네임 설정을 위한 함수
    private void getNickname(){
        Log.d(TAG, "실행");
        Intent nicknameIntent = new Intent(LogInScreen.this, NicknameScreen.class);
        nicknameForResult.launch(nicknameIntent);
        Log.d(TAG, "실행");
    }

    // 위 함수의 인텐트에 대한 콜백함수 정의
    private final ActivityResultLauncher<Intent> nicknameForResult =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            Log.d(TAG, "실행2");
                            if (result.getResultCode() == RESULT_OK) {
                                googleNickname = result.getData().getStringExtra("nickname");
                                Log.d(TAG, "NicknameScreen에서 얻은 값 : " + googleNickname);

                                String name = googleNickname;

                                // 최초로 생성된 닉네임이므로 firestore의 user 컬렉션에 document 추가
                                Map<String, Object> userBase = new HashMap<>();
                                userBase.put("레이팅", 0);
                                userBase.put("티어", 0);

                                Map<String, Object> base = new HashMap<>();
                                base.put("base", 0);

                                db.collection("유저")
                                        .document(name)
                                        .set(userBase);

                                db.collection("유저")
                                        .document(name)
                                        .collection("과목 별 푼 문제")
                                        .document("base")
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

                                Toast.makeText(LogInScreen.this, "닉네임 등록 및 로그인 성공", Toast.LENGTH_SHORT).show();
                                startMainScreen();
                            }
                        }
                    });

    // 로그아웃
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    firebaseAuth.signOut();
                    Toast.makeText(LogInScreen.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                    // ...
                });
        gsa = null;
    }

    // 메인화면에서 로그아웃 버튼 누를 경우 signOut() 실행
    private void startMainScreen(){
        Intent nicknameIntent = new Intent(LogInScreen.this, MainScreen.class);
        signOutForResult.launch(nicknameIntent);
    }

    //구글 사용자 로그아웃 위한 콜백함수 정의
    private final ActivityResultLauncher<Intent> signOutForResult =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK) {
                                signOut();
                            }
                        }
                    });
}