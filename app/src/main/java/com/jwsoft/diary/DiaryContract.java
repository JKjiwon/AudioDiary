package com.jwsoft.diary;

import android.provider.BaseColumns;

public final class DiaryContract {
    // 인스턴트화 금지
    private DiaryContract(){

    }
    // 테이블 정보를 내부클래스로 정의
    public static class DiaryEntry implements BaseColumns {
        public static final String TABLE_NAME = "diary";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_CONTENTS = "contents";
        public static final String COLUMN_NAME_PHOTO_PAHT = "photo_path";
        public static final String COLUMN_NAME_RECODE_PATH = "recode_path";
    }
}
