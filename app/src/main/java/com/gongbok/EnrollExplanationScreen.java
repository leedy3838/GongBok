package com.gongbok;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EnrollExplanationScreen extends AppCompatActivity {

    private String userName;
    private Long userTier;
    private String problemName;
    private String fileName;
    private String subject;
    private String tier1;
    private String tier2;
    private int tier1Num;
    private int tier2Num;

    private long ratingGap;

    DocumentReference problemRef;
    private long solveNum;
    boolean solveNumFlag;
    // totalTier = 난이도 이름(브론즈1), totalTierNum = 티어를 숫자로 표현(1), tierRating = 문제의 난이도별 레이팅 점수
    private int totalTierNum;

    Map<String, Integer> Problemrating;

    private Dialog answerDialog;

    private TextView answerInfoTv;
    private TextView problemNameTv;
    private ImageView userTierIv;
    private TextView userNameTv;
    private ImageView imageView;
    private TextView answerTv;
    private ProgressBar progressBar;
    private TextView uploadBtn;

    private FirebaseFirestore db;
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();

    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enroll_explanation_screen);

        // firebase 사용하기 위한 참조
        db = FirebaseFirestore.getInstance();

        // 커스텀 다이얼로그
        answerDialog = new Dialog(EnrollExplanationScreen.this);
        answerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        answerDialog.setContentView(R.layout.enroll_custom_dialog);

        // xml파일로부터 다양한 View와 스피너 객체화
        problemNameTv = findViewById(R.id.answer_problemTitle_tv);
        userNameTv = findViewById(R.id.answer_user_name);
        userTierIv = findViewById(R.id.answer_user_tier);
        progressBar = findViewById(R.id.answer_progress_View);
        imageView = findViewById(R.id.answer_image);
        answerTv = findViewById(R.id.answer_tv);
        uploadBtn = findViewById(R.id.answer_upload_btn);
        answerInfoTv = findViewById(R.id.answer_info_tv);

        Spinner tier1Spinner = findViewById(R.id.tier_1);
        Spinner tier2Spinner = findViewById(R.id.tier_2);

        // 문제 과목 분류, 문제 이름, 등록자 닉네임 받아올 인텐트 설정
        Intent intent = getIntent();
        problemName = intent.getStringExtra("problemName");
        userName = intent.getStringExtra("userName");
        subject = intent.getStringExtra("subjectName");

        problemNameTv.setText(problemName);
        userNameTv.setText(userName);

        // 유저의 티어 값 받아와서 이미지뷰에 해당 티어 사진 출력
        db.collection("유저")
                .document(userName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();

                        userTier = document.getLong("티어");
                        switch (userTier.intValue()){
                            case 1:
                                userTierIv.setImageResource(R.drawable.rank_icons_s1);
                                break;
                            case 2:
                                userTierIv.setImageResource(R.drawable.rank_icons_s2);
                                break;
                            case 3:
                                userTierIv.setImageResource(R.drawable.rank_icons_s3);
                                break;
                            case 4:
                                userTierIv.setImageResource(R.drawable.rank_icons_s4);
                                break;
                            case 5:
                                userTierIv.setImageResource(R.drawable.rank_icons_s5);
                                break;
                            case 6:
                                userTierIv.setImageResource(R.drawable.rank_icons_s6);
                                break;
                            case 7:
                                userTierIv.setImageResource(R.drawable.rank_icons_s7);
                                break;
                            case 8:
                                userTierIv.setImageResource(R.drawable.rank_icons_s8);
                                break;
                            case 9:
                                userTierIv.setImageResource(R.drawable.rank_icons_s9);
                                break;
                            case 10:
                                userTierIv.setImageResource(R.drawable.rank_icons_s10);
                                break;
                            case 11:
                                userTierIv.setImageResource(R.drawable.rank_icons_s11);
                                break;
                            case 12:
                                userTierIv.setImageResource(R.drawable.rank_icons_s12);
                                break;
                            case 13:
                                userTierIv.setImageResource(R.drawable.rank_icons_s13);
                                break;
                            case 14:
                                userTierIv.setImageResource(R.drawable.rank_icons_s14);
                                break;
                            case 15:
                                userTierIv.setImageResource(R.drawable.rank_icons_s15);
                                break;
                            case 16:
                                userTierIv.setImageResource(R.drawable.rank_icons_s16);
                                break;
                            case 17:
                                userTierIv.setImageResource(R.drawable.rank_icons_s17);
                                break;
                            case 18:
                                userTierIv.setImageResource(R.drawable.rank_icons_s18);
                                break;
                            case 19:
                                userTierIv.setImageResource(R.drawable.rank_icons_s19);
                                break;
                            case 20:
                                userTierIv.setImageResource(R.drawable.rank_icons_s20);
                                break;
                            case 21:
                                userTierIv.setImageResource(R.drawable.rank_icons_s21);
                                break;
                            case 22:
                                userTierIv.setImageResource(R.drawable.rank_icons_s22);
                                break;
                            case 23:
                                userTierIv.setImageResource(R.drawable.rank_icons_s23);
                                break;
                            case 24:
                                userTierIv.setImageResource(R.drawable.rank_icons_s24);
                                break;
                            case 25:
                                userTierIv.setImageResource(R.drawable.rank_icons_s25);
                                break;
                            case 26:
                                userTierIv.setImageResource(R.drawable.rank_icons_s26);
                                break;
                        }
                    }
                });

        //-------------------------------------------------------------------------------------------------------------------

        //스피너 어댑터 설정

        String[] tier1Items = getResources().getStringArray(R.array.level_choice1);
        String[] tier2Items = getResources().getStringArray(R.array.level_choice2);

        ArrayAdapter<String> tier1Adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, tier1Items);
        ArrayAdapter<String> tier2Adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, tier2Items);

        tier1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tier2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tier1Spinner.setAdapter(tier1Adapter);
        tier2Spinner.setAdapter(tier2Adapter);

        // 레벨1 스피너 (브론즈, 실버, ..., 민트 설정)
        tier1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (tier1Items[position].equals("민트")) {
                    Toast.makeText(EnrollExplanationScreen.this, "민트는 X(민트)를 선택해주세요", Toast.LENGTH_SHORT).show();
                    tier1 = tier1Items[position];
                    tier1Num = 26;
                } else {
                    tier1 = tier1Items[position];
                    tier1Num = 5 * position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                tier1 = null;
            }
        });

        // 레벨2 스피너 (세부 티어 : 1, 2, 3, 4, 5 설정)
        tier2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position != 5 && tier1Num < 26) {
                    tier2 = tier2Items[position];
                    tier2Num = position + 1;
                }
                // 민트인데 level2에서 X(민트)를 고르지 않은 경우
                else if (position != 5 && tier1Num == 26) {
                    Toast.makeText(EnrollExplanationScreen.this, "민트는 X(민트)를 선택해주세요", Toast.LENGTH_SHORT).show();
                    tier2 = "";
                    tier2Num = 0;
                }
                // 민트가 아닌 데 level2에서 X(민트)를 고른 경우
                else if (tier1Num < 26) {
                    Toast.makeText(EnrollExplanationScreen.this, "해당 옵션은 민트만 선택 가능합니다", Toast.LENGTH_SHORT).show();
                } else {
                    tier2 = "";
                    tier2Num = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                tier2 = null;
            }
        });

        // 프로그래스바 숨기기
        progressBar.setVisibility(View.INVISIBLE);

        //-------------------------------------------------------------------------------------------------------------------

        // 이미지 클릭 이벤트
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerTv.setVisibility(View.INVISIBLE);
                answerInfoTv.setVisibility(View.INVISIBLE);
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/");
                activityResult.launch(galleryIntent);
            }
        });



        // 업로드 버튼 클릭 이벤트
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null ) {
                    uploadToFirebase(imageUri);
                } else{
                    showAnswerDialog();
                }
            }
        });

        //-------------------------------------------------------------------------------------------------------------------

        // 풀이가 가리키는 문제의 필드 값 수정을 위한 Map
        Problemrating = new HashMap<>();

        // 브론즈
        Problemrating.put("1", 1);
        Problemrating.put("2", 2);
        Problemrating.put("3", 3);
        Problemrating.put("4", 4);
        Problemrating.put("5", 5);

        //실버
        Problemrating.put("6", 12);
        Problemrating.put("7", 14);
        Problemrating.put("8", 16);
        Problemrating.put("9", 18);
        Problemrating.put("10", 20);

        //골드
        Problemrating.put("11", 50);
        Problemrating.put("12", 55);
        Problemrating.put("13", 60);
        Problemrating.put("14", 65);
        Problemrating.put("15", 70);

        //플래티넘
        Problemrating.put("16", 200);
        Problemrating.put("17", 210);
        Problemrating.put("18", 220);
        Problemrating.put("19", 230);
        Problemrating.put("20", 240);

        //다이아몬드
        Problemrating.put("21", 520);
        Problemrating.put("22", 540);
        Problemrating.put("23", 560);
        Problemrating.put("24", 580);
        Problemrating.put("25", 600);

        //민트
        Problemrating.put("26", 1000);


    }

    //-------------------------------------------------------------------------------------------------------------------

    private void uploadToFirebase(Uri uri) {

        fileName = subject + "/" + problemName + "/" + userName + "님의 풀이." + getFileExtension(uri);
        StorageReference fileRef = reference.child(fileName);

        solveNumFlag = false;

        // 1. 풀이 사진을 Storage의 경로(fileName)에 저장
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                problemRef = db.collection("문제")
                        .document(subject)
                        .collection(subject)
                        .document(problemName);



                // 2. 기존에 유저가 올린 적 있는지 체크 후 풀이 document의 필드 값 설정
                problemRef
                        .collection(problemName)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    // 1. 유저가 기존에 해당 문제에 대한 풀이 올린 적 있다면 풀이 수 증가 X
                                    for (QueryDocumentSnapshot document : task.getResult()){
                                        if(document.getId().equals(userName + "님의 풀이")){
                                            Toast.makeText(EnrollExplanationScreen.this, "기존에 올린 풀이를 갱신하셨습니다", Toast.LENGTH_SHORT).show();
                                            solveNumFlag = true;
                                        }
                                    }

                                    // 2. 풀이 document의 필드 값 설정
                                    Map<String, Object> explanationBase = new HashMap<>();
                                    explanationBase.put("경로", fileName);
                                    explanationBase.put("등록자명", userName);
                                    explanationBase.put("등록자 티어", userTier);
                                    explanationBase.put("좋아요 수", 0);

                                    problemRef
                                            .collection(problemName)
                                            .document(userName + "님의 풀이")
                                            .set(explanationBase);
                                }
                            }
                        });

                // 3. 해당 문제 난이도, 풀이 수 필드 값 수정

                problemRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                long tier = document.getLong("난이도");
                                long tierSum = document.getLong("난이도 평가의 합");
                                long tierNum = document.getLong("난이도를 평가한 사람 수");
                                long rating = document.getLong("레이팅");
                                solveNum = document.getLong("풀이 수");

                                ratingGap = rating;
                                ++tierNum;
                                solveNum += 1;
                                tierSum += getTotalTierNum();

                                if (tierNum >= 10) {
                                    tier = tierSum / tierNum;
                                    rating = Problemrating.get(Long.toString(tier));
                                    ratingGap = rating - ratingGap;
                                    Log.d("Check", tier +"");
                                    Log.d("Check", ratingGap +"");
                                }
                                else {
                                    ratingGap = 0;
                                }


                                problemRef.update("난이도", tier);
                                problemRef.update("난이도 평가의 합", tierSum);
                                problemRef.update("난이도를 평가한 사람 수", tierNum);
                                problemRef.update("레이팅", rating);
                                if(!solveNumFlag)
                                    problemRef.update("풀이 수", solveNum);
                            }
                        }
                    }
                });

                // 3. 난이도 평가한 사람이 10명 이상어서 레이팅 점수 바뀐 경우 기존 유저들 레이팅 변경
                db.collection("문제")
                        .document(subject)
                        .collection(subject)
                        .document(problemName)
                        .collection("문제를 푼 유저")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("Check", "뭐가 더 먼저 실행되나");
                                    db.collection("유저")
                                            .document(document.getId())
                                            .update("레이팅", FieldValue.increment(ratingGap));
                                }
                            }
                        });


                // 4. 유저 collection의  내가 올린 풀이 document에 저장하고 필드 값 세팅
                Map<String, Object> myExplanations = new HashMap<>();
                myExplanations.put("경로", fileName);

                db.collection("유저")
                        .document(userName)
                        .collection("내가 올린 풀이")
                        .document(problemName)
                        .set(myExplanations);

                // 업로드 성공 후 다시 문제 선택 화면으로 복귀
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(EnrollExplanationScreen.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                Intent homeIntent = new Intent(EnrollExplanationScreen.this, MainScreen.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(EnrollExplanationScreen.this, "업로드 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 파일 확장자 반환하는 함수
    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));
    }



    // 최종적으로 선택된 문제의 난이도를 String으로 반환
    public int getTotalTierNum() {
        totalTierNum = tier1Num + tier2Num;
        return totalTierNum;
    }

    // 커스텀 다이얼로그 디자인 및 출력
    public void showAnswerDialog() {
        answerDialog.show();

        TextView yesBtn = answerDialog.findViewById(R.id.yesBtn);
        yesBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DocumentReference problemRef = db.collection("문제")
                        .document(subject)
                        .collection(subject)
                        .document(problemName);

                problemRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                long tier = document.getLong("난이도");
                                long tierSum = document.getLong("난이도 평가의 합");
                                long tierNum = document.getLong("난이도를 평가한 사람 수");
                                long rating = document.getLong("레이팅");

                                ++tierNum;
                                tierSum += getTotalTierNum();
                                ;

                                if (tierNum >= 10) {
                                    tier = tierSum / tierNum;
                                    rating = Problemrating.get(Long.toString(tier));

                                }

                                problemRef.update("난이도", tier);
                                problemRef.update("난이도 평가의 합", tierSum);
                                problemRef.update("난이도를 평가한 사람 수", tierNum);
                                problemRef.update("레이팅", rating);

                            }
                        }
                    }
                });
                Intent homeIntent = new Intent(EnrollExplanationScreen.this, MainScreen.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        });

        TextView noBtn = answerDialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                answerDialog.dismiss();
            }
        });
    }

    ActivityResultLauncher<Intent> activityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null){
                        imageUri = result.getData().getData();
                        imageView.setImageURI(imageUri);
                    }
                }
            }
    );

    public void goToMain(View view) {
        Intent homeIntent = new Intent(this, MainScreen.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

}