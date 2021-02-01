package com.jwsoft.diary.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.jwsoft.diary.DiaryContract;
import com.jwsoft.diary.DiaryDbHelper;
import com.jwsoft.diary.R;
import com.jwsoft.diary.SoftKeyboardDectectorView;
import com.jwsoft.diary.DiaryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ResisterActivity extends AppCompatActivity {

    // 오디오 녹화
    private static final String LOG_TAG = "AudioRecordTest";
    private MediaPlayer player = null;
    private MediaRecorder recorder = null;
    private static String recodedFileName = null;
    FloatingActionButton mRecordBtn = null;
    TextView mResetBtn = null;
    int currentState = 1;
    LinearLayout recodeView;

    // 권한 설정
    private static final int MULTIPLE_PERMISSION = 10235;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    // 날짜
    private int year;
    private int month;
    private int day;
    private TextView mDateText;
    // 저장될 파일의 이름
    private String RealFileName;

    // 사진
    private ImageView mPhotoBtn = null;
    private ImageView mPhotoImage = null;
    private ImageView mPhotoRemoveImage = null;
    private static final int GALLEY_CODE = 10;
    private String imagePath = null;


    // db 관련
    private boolean isCreateRecode = false;
    private boolean isCreateImage = false;
    private EditText mContentsEdit = null;
    private ImageView mResisterBtn = null;
    private ImageView mBackBtn = null;

    // db수정
    private int DiaryId = -1;
    private DiaryItem diaryItem = null;

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MULTIPLE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
                } else {
                    // 하나라도 거부한다면
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setTitle("앱 권한");
                    alertDialog.setMessage("해당 앱의 원활한 기능을 이용하시려면 애플리케이션 정보 > 권한에서 모든 권한을 허용해 주십시오");
                    // 권한설정 클릭시 이벤트 발생
                    alertDialog.setPositiveButton("권한설정", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                            startActivity(intent);
                            dialog.cancel();
                        }
                    });
                    //취소
                    alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
                return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resister);
        getSupportActionBar().hide();


        mDateText = findViewById(R.id.resi_date_text);
        recodeView = (LinearLayout) findViewById(R.id.resi_recode_view);
        mRecordBtn = (FloatingActionButton) findViewById(R.id.resi_play_btn);
        mResetBtn = (TextView) findViewById(R.id.resi_reset_btn);
        mPhotoBtn = (ImageView) findViewById(R.id.resi_photo_btn);
        mPhotoImage = (ImageView) findViewById(R.id.resi_photo_image);
        mPhotoRemoveImage = (ImageView) findViewById(R.id.resi_remove_photo_image);
        mContentsEdit = (EditText) findViewById(R.id.resi_contents_edit);
        mResisterBtn = (ImageView) findViewById(R.id.resi_resister_btn);
        mBackBtn = (ImageView) findViewById(R.id.resi_back_btn);


        // 사진 삭제
        mPhotoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoRemoveImage.setVisibility(View.VISIBLE);
            }
        });
        mPhotoRemoveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoImage.setVisibility(View.GONE);
                mPhotoRemoveImage.setVisibility(View.GONE);
                isCreateImage = false;
            }
        });

        Intent intent = getIntent();
        diaryItem = (DiaryItem) getIntent().getSerializableExtra("diaryItem");


        if (diaryItem != null) {
            String[] receivedDates = diaryItem.getDate().split("-");
            year = Integer.parseInt(receivedDates[0]);
            month = Integer.parseInt(receivedDates[1]);
            day = Integer.parseInt(receivedDates[2]);

            // contents
            mContentsEdit.setText(diaryItem.getContents());

            // 사진
            RealFileName = diaryItem.getPhotoPath();
            if (!RealFileName.equals("none")) {
                mPhotoImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mPhotoImage.setVisibility(View.VISIBLE);
                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mydiary/picture/" + RealFileName + ".jpeg");
                Glide.with(this)
                        .load(Uri.fromFile(f))
                        .apply(new RequestOptions().signature(new ObjectKey("signature string"))
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE))
                        .into(mPhotoImage);
                isCreateImage = true;
            }
            //녹음
            String receivedRecode = diaryItem.getRecodePath();
            if (!receivedRecode.equals("none")) {
                mRecordBtn.setImageResource(R.drawable.ic_play);
                currentState = 3;
                recodedFileName = diaryItem.getRecodePath();
                isCreateRecode = true;
                mResetBtn.setVisibility(View.VISIBLE);
            }
        } else {
            // 홈 프레그먼트에서 보낸
            year = intent.getIntExtra("year", 1);
            month = intent.getIntExtra("month", 1);
            day = intent.getIntExtra("day", 1);
        }

        mDateText.setText(String.format("%d.%s.%s", year, ZeroFill(month), ZeroFill(day)));
        RealFileName = ZeroFill(year) + ZeroFill(month) + ZeroFill(day);
        File file = new File(getSaveFolder("recode"), RealFileName + ".mp3");
        recodedFileName = file.getAbsolutePath();
        // 권한 설정
        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, MULTIPLE_PERMISSION);
        }

        // 키보드 리스너
        final SoftKeyboardDectectorView softKeyboardDecector = new SoftKeyboardDectectorView(this);
        addContentView(softKeyboardDecector, new FrameLayout.LayoutParams(-1, -1));
        softKeyboardDecector.setOnShownKeyboard(new SoftKeyboardDectectorView.OnShownKeyboardListener() {

            @Override
            public void onShowSoftKeyboard() {
                //키보드 등장할 때
                recodeView.setVisibility(View.INVISIBLE);

            }
        });

        softKeyboardDecector.setOnHiddenKeyboard(new SoftKeyboardDectectorView.OnHiddenKeyboardListener() {

            @Override
            public void onHiddenSoftKeyboard() {
                // 키보드 사라질 때
                recodeView.setVisibility(View.VISIBLE);
            }
        });

        //  녹음 기능
        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentState == 1) {
                    mContentsEdit.clearFocus();
                    startRecording(); // 녹음 시작
                    mRecordBtn.setImageResource(R.drawable.ic_stop);
                    mResetBtn.setVisibility(View.VISIBLE);
                    isCreateRecode = true;
                } else if (currentState == 2) {
                    stopRecording(); // 녹음 정지
                    mRecordBtn.setImageResource(R.drawable.ic_play);
                } else {
                    if (currentState % 2 == 1) {
                        startPlaying(); // 재생 시작
                        mRecordBtn.setImageResource(R.drawable.ic_pause);
                    } else {
                        stopPlaying(); // 재생 정지
                        mRecordBtn.setImageResource(R.drawable.ic_play);
                    }
                }
                currentState++;
            }
        });
        // 녹음 재시도
        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player != null) {
                    stopPlaying();
                }
                if (recorder != null) {
                    stopRecording();
                }
                currentState = 1;
                mRecordBtn.setImageResource(R.drawable.ic_mic);
                mResetBtn.setVisibility(View.INVISIBLE);
                // 수정 중일때는 여기서 삭제하지 않는다. 등록버튼이 눌려졌을 때 삭제
                if (isCreateRecode && diaryItem == null) {
                    File recodeFile = new File(recodedFileName);
                    if (recodeFile.exists()) {
                        recodeFile.delete();
                    }
                }
                isCreateRecode = false;
            }
        });


        mPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, GALLEY_CODE);
            }
        });
        // 등록버튼
        mResisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (player != null) {
                    stopPlaying();
                }
                if (recorder != null) {
                    stopRecording();
                }
                String contents = mContentsEdit.getText().toString().trim();
                String photo_path;
                String recode_path;

                if (isCreateImage == true) {
                    photo_path = RealFileName;
                } else {
                    // X 표시로 제거한 이미지 파일 삭제
                    photo_path = "none";
                    File imageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mydiary/picture/" + RealFileName+".jpeg");
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }
                }


                if (isCreateRecode == true) {
                    recode_path = recodedFileName;
                } else {
                    recode_path = "none";
                    File recodeFile = new File(recodedFileName);
                    if (recodeFile.exists()) {
                        recodeFile.delete();
                    }
                }

                String date = ZeroFill(year) + "-" + ZeroFill(month) + "-" + ZeroFill(day);
                ContentValues contentValues = new ContentValues();
                contentValues.put(DiaryContract.DiaryEntry.COLUMN_NAME_DATE, date);
                contentValues.put(DiaryContract.DiaryEntry.COLUMN_NAME_CONTENTS, contents);
                contentValues.put(DiaryContract.DiaryEntry.COLUMN_NAME_PHOTO_PAHT, photo_path);
                contentValues.put(DiaryContract.DiaryEntry.COLUMN_NAME_RECODE_PATH, recode_path);

                SQLiteDatabase db = DiaryDbHelper.getInstance(ResisterActivity.this).getWritableDatabase();

                if (diaryItem == null) {
                    long newRowId = db.insert(DiaryContract.DiaryEntry.TABLE_NAME, null, contentValues);
                    if (newRowId == -1) {
                        Log.e("CreatDiary", "저장에 문제가 발생했습니다");

                    } else {
                        Toast.makeText(ResisterActivity.this, "일기가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        Log.d("CreatDiary", "일기가 저장되었습니다.");

                        if (isCreateImage == true) {
                            photoSave();
                        }
                    }
                } else {
                    int count = db.update(DiaryContract.DiaryEntry.TABLE_NAME, contentValues, DiaryContract.DiaryEntry._ID + " = " + diaryItem.getId(), null);
                    if (count != 0) {
                        Toast.makeText(ResisterActivity.this, "일기가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                        if (isCreateImage == true && imagePath != null) {
                            photoSave();
                        }
                        setResult(RESULT_OK);
                    }
                }
                if (player != null) {
                    stopPlaying();
                }
                if (recorder != null) {
                    stopRecording();
                }

                if(isCreateImage == true && imagePath !=null){
                    // 이미지 저장 할 때 딜레이 주기
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 500);
                }else{
                    finish();
                }


            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void photoSave() {
        File f = new File(imagePath);
        Glide.with(getApplicationContext())
                .load(Uri.fromFile(f))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                        saveImage(bitmap, getSaveFolder("picture"), RealFileName + ".jpeg");
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(recodedFileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setOutputFile(recodedFileName);
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }


    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLEY_CODE) {
            try {
//          이미지 불러오기
                imagePath = getPath(data.getData());
                File f = new File(imagePath);
                mPhotoImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mPhotoImage.setVisibility(View.VISIBLE);

                Glide.with(this)
                        .load(Uri.fromFile(f))
                        .into(mPhotoImage);

                isCreateImage = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public String getPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(index);
    }

    // 저장 폴더 생성
    private File getSaveFolder(String folderName) {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mydiary/" + folderName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    // 이미지 저장 로직
    private void saveImage(Bitmap image, File storageDir, String imageFileName) {
        File imageFile = new File(storageDir, imageFileName);
        File file = new File(storageDir, imageFileName + ".jpeg");
        try {
            OutputStream fOut = new FileOutputStream(imageFile);
            image.compress(Bitmap.CompressFormat.JPEG, 75, fOut);
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        // 녹음파일 삭제 추가해야함
        if (player != null) {
            stopPlaying();

        }
        if (recorder != null) {
            stopRecording();

        }
        if (isCreateRecode && diaryItem == null) {
            File recodeFile = new File(recodedFileName);
            if (recodeFile.exists()) {
                recodeFile.delete();
            }
        }
        super.onBackPressed();
    }

    public String ZeroFill(int num) {
        if (num >= 0 && num <= 9) {
            return String.format("0%d", num);
        } else {
            return String.format("%d", num);
        }
    }
}
