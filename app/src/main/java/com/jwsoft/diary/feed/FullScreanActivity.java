package com.jwsoft.diary.feed;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.jwsoft.diary.DiaryContract;
import com.jwsoft.diary.DiaryDbHelper;
import com.jwsoft.diary.DiaryItem;
import com.jwsoft.diary.R;
import com.jwsoft.diary.home.ResisterActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;

public class FullScreanActivity extends AppCompatActivity {
    private DiaryItem diaryItem = new DiaryItem();
    private int select_id;

    MediaPlayer mp; // 음악 재생을 위한 객체
    int pos; // 재생 멈춘 시점
    SeekBar sb; // 음악 재생위치를 나타내는 시크바
    TextView currentTime;
    TextView durationTime;
    boolean isPlaying = false; // 재생중인지 확인할 변수
    FloatingActionButton playBtn;
    private int playState = 1;


    TextView dateText;
    ImageView photoImage;
    TextView contentsText;
    LinearLayout fsRecodeView;
    ScrollView fsContentsView;
    ImageView menuBtn;
    ImageView backBtn;

    boolean isNotRecode;
    boolean isNotPhoto;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mp != null) {
            isPlaying = false;
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    class MyThread extends Thread {
        @Override
        public void run() { // 쓰레드가 시작되면 콜백되는 메서드
            // 씨크바 막대기 조금씩 움직이기 (노래 끝날 때까지 반복)
            boolean start = true;
            while (isPlaying) {
                if (start) {
                    handler.sendEmptyMessage(0);
                }
                start = false;
            }
        }
    }

    @SuppressLint("HandlerLeak")
    //쓰레드가 실행되면 호출을 한다.
            //
            //
            // 호출 메시지 이름이 0이다.

            Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0 && mp != null) {
                sb.setProgress(mp.getCurrentPosition());
                currentTime.setText(milliSecondsToTimer(mp.getCurrentPosition()));
                if (isPlaying)
                    sendEmptyMessageDelayed(0, 20 /* ?초에 한번 재 실행 */);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screan);
        getSupportActionBar().hide();
        Log.e("호출되냐>>", "helloworld");

        Intent intent = getIntent();
        select_id = intent.getIntExtra("dbId", 1);
        selectIdData();

        dateText = (TextView) findViewById(R.id.fs_date_text);
        photoImage = (ImageView) findViewById(R.id.fs_photo_image);
        contentsText = (TextView) findViewById(R.id.fs_contents_text);
        fsRecodeView = (LinearLayout) findViewById(R.id.fs_recode_view);
        fsContentsView = (ScrollView) findViewById(R.id.fs_contents_view);
        menuBtn = (ImageView) findViewById(R.id.fs_menu_btn);
        backBtn = (ImageView) findViewById(R.id.fs_back_btn);
        playBtn = (FloatingActionButton) findViewById(R.id.fs_play_btn);
        currentTime = (TextView) findViewById(R.id.current_time_text);
        durationTime = (TextView) findViewById(R.id.duration_time_text);
        isNotRecode = diaryItem.getRecodePath().equals("none");
        isNotPhoto = diaryItem.getPhotoPath().equals("none");
        String date = diaryItem.getDate();

        dateText.setText(date.replace("-","."));

        if (isNotRecode) {
            fsRecodeView.setVisibility(View.GONE);
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int size = Math.round(40 * dm.density);
            fsContentsView.setPadding(24, 24, 24, size);
        } else {
            mp = new MediaPlayer();
            try {
                mp.setDataSource(diaryItem.getRecodePath());
                mp.prepare();
            } catch (IOException e) {
                Log.e("recodePlay", "prepare() failed");
            }
            mp.setLooping(false); // true:무한반복
            durationTime.setText(milliSecondsToTimer(mp.getDuration()));
        }

        if (isNotPhoto) {
            photoImage.setVisibility(View.GONE);
        } else {
            photoImage.setVisibility(View.VISIBLE);
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mydiary/picture/" + diaryItem.getPhotoPath() + ".jpeg");

            // 캐싱 처리 스
            Glide.with(this)
                    .load(Uri.fromFile(f))
                    .apply(new RequestOptions().signature(new ObjectKey("signature string"))
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE))
                    .into(photoImage);
        }


        if (!diaryItem.getContents().equals("")) {
            contentsText.setText(diaryItem.getContents());
        }else{
            contentsText.setText("");
        }

        photoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FullScreanActivity.this, FullImageActivity.class);
                intent.putExtra("photoPath", diaryItem.getPhotoPath());

                if (mp != null) {
                    isPlaying = false;
                    mp.stop();
                    mp.release();
                    mp = null;
                }
                playState = 1;
                sb.setProgress(0);
                currentTime.setText(milliSecondsToTimer(0));
                playBtn.setImageResource(R.drawable.ic_play);
                startActivity(intent);
            }
        });
        sb = (SeekBar) findViewById(R.id.fs_seek_bar);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                isPlaying = true;
                int ttt = seekBar.getProgress(); // 사용자가 움직여놓은 위치
                mp.seekTo(ttt);
                mp.start();
                new MyThread().start();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                isPlaying = false;
                mp.pause();
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getMax() == progress) {
                    playBtn.setImageResource(R.drawable.ic_play);
                    isPlaying = false;
                    mp.stop();
                    playState = 1;
                    sb.setProgress(0);
                    currentTime.setText(milliSecondsToTimer(0));
                }
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playState == 1) {
                    mp = new MediaPlayer();
                    try {
                        mp.setDataSource(diaryItem.getRecodePath());
                        mp.prepare();
                    } catch (IOException e) {
                        Log.e("recodePlay", "prepare() failed");
                    }
                    mp.setLooping(false); // true:무한반복
                    mp.start(); // 노래 재생 시작

                    int a = mp.getDuration(); // 노래의 재생시간(miliSecond)
                    sb.setMax(a);// 씨크바의 최대 범위를 노래의 재생시간으로 설정
                    new MyThread().start(); // 씨크바 그려줄 쓰레드 시작
                    isPlaying = true; // 씨크바 쓰레드 반복 하도록
                    playBtn.setImageResource(R.drawable.ic_pause);
                    playState = 2;
                } else if (playState == 2) {
                    pos = mp.getCurrentPosition();
                    mp.pause(); // 일시중지
                    isPlaying = false;
                    playState = 3;
                    playBtn.setImageResource(R.drawable.ic_play);
                } else if (playState == 3) {
                    // 멈춘 지점부터 재시작
                    mp.seekTo(pos); // 일시정지 시점으로 이동
                    mp.start(); // 시작
                    playBtn.setImageResource(R.drawable.ic_pause);
                    isPlaying = true; // 재생하도록 flag 변경
                    new MyThread().start(); // 쓰레드 시작

                    if (mp.getCurrentPosition() != mp.getDuration()) {
                        playState = 2;
                    }
                }
            }
        });

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(FullScreanActivity.this, menuBtn);
                MenuInflater inf = popupMenu.getMenuInflater();
                inf.inflate(R.menu.popup_menu_xml, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.modify:
                                Intent intent = new Intent(FullScreanActivity.this, ResisterActivity.class);
                                intent.putExtra("diaryItem", diaryItem);
                                isPlaying = false;
                                if (mp != null) {
                                    mp.stop();
                                    mp.release();
                                    mp = null;
                                }
                                playState = 1;
                                sb.setProgress(0);
                                currentTime.setText(milliSecondsToTimer(0));
                                playBtn.setImageResource(R.drawable.ic_play);
                                startActivityForResult(intent, 30);

                                break;
                            case R.id.remove:
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(FullScreanActivity.this);
                                alertDialog.setTitle("일기 삭제");
                                alertDialog.setMessage("일기를 삭제하시겠습니까?");
                                // 권한설정 클릭시 이벤트 발생
                                alertDialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 삭제 로직
                                        SQLiteDatabase db = DiaryDbHelper.getInstance(FullScreanActivity.this).getWritableDatabase();
                                        int deleteCount = db.delete(DiaryContract.DiaryEntry.TABLE_NAME, DiaryContract.DiaryEntry._ID + "=" + select_id, null);

                                        // 삭제 성공하면 ~
                                        if (deleteCount != 0) {
                                            // 레코드 파일 삭제
                                            String fileName = diaryItem.getDate();
                                            fileName = fileName.replace("-", "");
                                            File recodeFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mydiary/recode/" + fileName + ".mp3");
                                            if (recodeFile.exists()) {
                                                recodeFile.delete();
                                            }
                                            // 이미지 파일 삭제
                                            File photoFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mydiary/picture/" + fileName + ".jpeg");
                                            if (photoFile.exists()) {
                                                photoFile.delete();
                                            }
                                            dialog.cancel();

                                            isPlaying = false;
                                            if (mp != null) {
                                                mp.stop();
                                                mp.release();
                                                mp = null;
                                            }

                                            finish();
                                        } else {
                                            dialog.cancel();
                                        }
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
                                break;
                        }
                        return false;
                    }
                });
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void selectIdData() {
        Log.e("helloword", "되나용??");
        DiaryDbHelper dbHelper = DiaryDbHelper.getInstance(FullScreanActivity.this);
        Cursor cursor = dbHelper.getReadableDatabase().query(DiaryContract.DiaryEntry.TABLE_NAME,
                null, DiaryContract.DiaryEntry._ID + "=?", new String[]{String.valueOf(select_id)}, null, null, null);

        Log.e("selectIdData", "호출");
        while (cursor.moveToNext()) {
            diaryItem.setId(cursor.getInt(0));
            diaryItem.setDate(cursor.getString(1));
            diaryItem.setContents(cursor.getString(2));
            diaryItem.setPhotoPath(cursor.getString(3));
            diaryItem.setRecodePath(cursor.getString(4));
        }
        cursor.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPlaying = false;
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        return finalTimerString;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 30 && resultCode == RESULT_OK) {

            Intent intent = getIntent();
            finish();
            startActivity(intent);

        }
    }
}
