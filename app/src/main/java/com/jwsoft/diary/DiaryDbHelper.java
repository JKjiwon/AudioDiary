package com.jwsoft.diary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DiaryDbHelper extends SQLiteOpenHelper {

    private static DiaryDbHelper sInstance;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Diary.db";
    private static final String SQL_CREATE_ENTRIES = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
            DiaryContract.DiaryEntry.TABLE_NAME, DiaryContract.DiaryEntry._ID, DiaryContract.DiaryEntry.COLUMN_NAME_DATE,
            DiaryContract.DiaryEntry.COLUMN_NAME_CONTENTS, DiaryContract.DiaryEntry.COLUMN_NAME_PHOTO_PAHT,
            DiaryContract.DiaryEntry.COLUMN_NAME_RECODE_PATH);
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DiaryContract.DiaryEntry.TABLE_NAME;


    // 펙토리 메서드
    public static synchronized DiaryDbHelper getInstance(Context context){
        if (sInstance == null){
            sInstance = new DiaryDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DiaryDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // DB 스키마가 변경될 때 여기서 데이터를 벡업하고
        // 테이블을 삭제 후 재생성 및 데이터 복원 등을 한다.
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
