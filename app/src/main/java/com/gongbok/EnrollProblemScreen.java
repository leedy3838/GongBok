package com.gongbok;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1.WriteResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EnrollProblemScreen extends AppCompatActivity{

    private String TAG = "sainfe_enroll";
    private ImageView imageView;
    private TextView enrollTv;
    private ProgressBar progressBar;
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
    private String subject;
    private String level1;
    private String level2;
    private String totalLevel;
    private int tierRating;
    private int totalLevelNum;
    private int level1Num;
    private int level2Num;
    private String problemName;
    private String problemAnswer;
    private String fileName;
    private EditText problemNameEt;
    private EditText problemAnswerEt;


    private ArrayList<Map<String, Integer>> tierRatingArrayList;

    private FirebaseFirestore db;
    private String userName;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enroll_problem_screen);

        db = FirebaseFirestore.getInstance();

        String a = "abc" + null;
        Log.d(TAG, a);

        Intent intent = getIntent();
        userName = intent.getStringExtra("nickname");

        // 메인화면으로부터 받은 티어정보 추출을 위한 ArrayList
        tierRatingArrayList = (ArrayList<Map<String, Integer>>) intent.getSerializableExtra("tierRating");

        // xml의 뷰 객체를 변수에 담기
        Spinner subjectSpinner = findViewById(R.id.subject_spinner);

        problemNameEt = findViewById(R.id.problem_name);

        progressBar = findViewById(R.id.progress_View);
        imageView = findViewById(R.id.enroll_image);
        enrollTv = findViewById(R.id.enroll_tv);

        Spinner level1Spinner = findViewById(R.id.level_1);
        Spinner level2Spinner = findViewById(R.id.level_2);
        problemAnswerEt = findViewById(R.id.problem_answer);

        TextView uploadBtn = findViewById(R.id.upload_btn);

        //스피너 어댑터 설정
        String[] subjectItems = getResources().getStringArray(R.array.subject);
        String[] level1Items = getResources().getStringArray(R.array.level_choice1);
        String[] level2Items = getResources().getStringArray(R.array.level_choice2);

        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, subjectItems);
        ArrayAdapter<String> level1Adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, level1Items);
        ArrayAdapter<String> level2Adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, level2Items);

        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        subjectSpinner.setAdapter(subjectAdapter);
        level1Spinner.setAdapter(level1Adapter);
        level2Spinner.setAdapter(level2Adapter);

        // 문제 분류 설정 스피너
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                subject = subjectItems[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                subject = null;
            }
        });

        // 레벨1 스피너 (브론즈, 실버, ..., 민트 설정)
        level1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(level1Items[position].equals("민트")){
                    Toast.makeText(EnrollProblemScreen.this, "민트는 X(민트)를 선택해주세요", Toast.LENGTH_SHORT).show();
                    level1 = level1Items[position];
                    level1Num = 26;
                }
                else {
                    level1 = level1Items[position];
                    level1Num = 5 * position;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {level1 = null;}
        });

        // 레벨2 스피너 (세부 티어 : 1, 2, 3, 4, 5 설정)
        level2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(position != 5 && level1Num < 26) {
                    level2 = level2Items[position];
                    level2Num = position + 1;
                }
                // 민트인데 level2에서 X(민트)를 고르지 않은 경우
                else if (position != 5 && level1Num == 26){
                    Toast.makeText(EnrollProblemScreen.this, "민트는 X(민트)를 선택해주세요", Toast.LENGTH_SHORT).show();
                    level2 = "";
                    level2Num = 0;
                }
                // 민트가 아닌 데 level2에서 X(민트)를 고른 경우
                else if (level1Num < 26)
                {
                    Toast.makeText(EnrollProblemScreen.this, "해당 옵션은 민트만 선택 가능합니다", Toast.LENGTH_SHORT).show();
                }
                else{
                    level2 = "";
                    level2Num = 0;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {level2 = null;}
        });

        // 프로그래스바 숨기기
        progressBar.setVisibility(View.INVISIBLE);

        // 이미지 클릭 이벤트
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enrollTv.setVisibility(View.INVISIBLE);
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/");
                activityResult.launch(galleryIntent);
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null) {
                    uploadToFirebase(imageUri);
                } else {
                    Toast.makeText(EnrollProblemScreen.this, "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
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

    private void uploadToFirebase(Uri uri) {
        problemName = problemNameEt.getText().toString().trim();
        if(problemName.length() == 0){
            Toast.makeText(EnrollProblemScreen.this, "문제 이름을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        problemAnswer = problemAnswerEt.getText().toString().trim();
        if(problemAnswer.length() == 0){
            Toast.makeText(EnrollProblemScreen.this, "문제의 정답을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        fileName = subject + "/" + problemName + "."+getFileExtension(uri);
        StorageReference fileRef = reference.child(fileName);

        // totalLevel = 난이도 이름(브론즈1), totalLevelNum = 티어를 숫자로 표현(1), tierRating = 문제의 난이도별 레이팅 점수
        // getTierName() -> totalLevel과 totalLevelNum을 반환
        // ArrayList의 Map<String, Integer> 에서 totalLevel로 문제의 난이도별 레이팅 점수 받아옴
        tierRating = tierRatingArrayList.get(0).get(getTierName());

        Log.d(TAG, problemAnswer);
        Log.d(TAG, problemName);
        Log.d(TAG, fileName);
        Log.d(TAG, totalLevel);
        Log.d(TAG, totalLevelNum + "");
        Log.d(TAG, tierRating +"");

        // 1.문제 사진을 Storage의 경로(fileName)에 저장
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // 문제 이름(problemName), 문제 정답(problemAnswer), 스피너 결과 (문제 분류(subject), 난이도(totalLevel)) 활용하여 storage에 저장하고
                // 경로(fileName)를 활용하여 firestore에서 storage 사진 참조

                // 2. 문제 collection의 해당 과목 document의 필드 값인 문제 수에 1 더하기
                db.collection("문제")
                        .document(subject)
                        .update("문제 수", FieldValue.increment(1));

                // 3. Firestore - 문제 collection의 해당 과목 collection에 문제이름으로 document 저장하고 필드 값 세팅
                Map<String, Object> problemBase = new HashMap<>();
                problemBase.put("경로", fileName);
                problemBase.put("난이도", totalLevelNum);
                problemBase.put("난이도 평가의 합", totalLevelNum);
                problemBase.put("난이도를 평가한 사람 수", 1);
                problemBase.put("레이팅", tierRating);
                problemBase.put("맞힌 횟수", 0);
                problemBase.put("시도 횟수", 0);
                problemBase.put("정답", problemAnswer);
                problemBase.put("좋아요 수", 0);
                problemBase.put("풀이 수", 0);

                db.collection("문제")
                        .document(subject)
                        .collection(subject)
                        .document(problemName)
                        .set(problemBase);

                // 풀이 경로 미리 세팅
                Map<String, Object> base = new HashMap<>();
                base.put("base", 0);

                db.collection("문제")
                        .document(subject)
                        .collection(subject)
                        .document(problemName)
                        .collection(problemName)
                        .document("base")
                        .set(base);

                // 4. 유저 collection의  내가 올린 문제 document에 저장하고 필드 값 세팅
                Map<String, Object> myProblems = new HashMap<>();
                myProblems.put("경로", fileName);

                db.collection("유저")
                        .document(userName)
                        .collection("내가 올린 문제")
                        .document(problemName)
                        .set(myProblems);

                // 5. 유저의 "올린 문제 수" 필드 값 증가
                db.collection("유저")
                        .document(userName)
                        .update("올린 문제 수", FieldValue.increment(1));

                // 업로드 성공 후 다시 메인화면으로 복귀
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(EnrollProblemScreen.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                finish();
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
                Toast.makeText(EnrollProblemScreen.this, "업로드 실패", Toast.LENGTH_SHORT).show();
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
    public String getTierName () {
        totalLevelNum = level1Num + level2Num;
        totalLevel = level1 + level2;
        return totalLevel;
    }

    // EditText가 아닌 화면의 다른 곳을 클릭하면 소프트 키보드 내려감
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    public void goToMain(View view) {
        Intent homeIntent = new Intent(this, MainScreen.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

}