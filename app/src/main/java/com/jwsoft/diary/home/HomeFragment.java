package com.jwsoft.diary.home;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.jwsoft.diary.DiaryContract;
import com.jwsoft.diary.DiaryDbHelper;
import com.jwsoft.diary.R;


import com.jwsoft.diary.feed.FullScreanActivity;
import com.jwsoft.diary.home.decorators.EventDecorator;
import com.jwsoft.diary.home.decorators.SaturdayDecorator;
import com.jwsoft.diary.home.decorators.SundayDecorator;
import com.jwsoft.diary.home.decorators.TodayDecorator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment{

    // calenderView
    private MaterialCalendarView calendarView;

    // floating action button
    private FloatingActionButton fab_main;

    private TextView yearText;
    private TextView monthText;
    private boolean initialDay = true;
    private int currnetYear;
    private int currnetMonth;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);


        fab_main = (FloatingActionButton) root.findViewById(R.id.fab_main);
        yearText = (TextView) root.findViewById(R.id.home_year_text);
        monthText = (TextView) root.findViewById(R.id.home_month_text);

        calendarView = root.findViewById(R.id.calendarView);
        calendarView.setTopbarVisible(false);

        calendarView.addDecorators(new SaturdayDecorator(), new SundayDecorator(), new TodayDecorator());
        // 날짜 표시
        if (initialDay) {
            currnetYear = CalendarDay.today().getYear();
            currnetMonth = CalendarDay.today().getMonth()+1;
            yearText.setText(currnetYear + "년");
            monthText.setText(currnetMonth + "월");
            initialDay = false;
        }

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                currnetYear = date.getYear();
                currnetMonth = date.getMonth() + 1;
                yearText.setText(currnetYear+ "년");
                monthText.setText(currnetMonth+"월");
            }
        });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                int year = date.getYear();
                int month = date.getMonth()+1;
                int day = date.getDay();

                String db_date = String.format("%s-%s-%s",ZeroFill(year),ZeroFill(month),ZeroFill(day));
                Log.e("date", db_date);

                int id = isDiaryExist(db_date);
                if(id != -1){
                    Intent intent = new Intent(getContext(), FullScreanActivity.class);
                    intent.putExtra("dbId",id);
                    startActivityForResult(intent,0);

                }else{
                    Intent intent = new Intent(getContext(), ResisterActivity.class);
                    intent.putExtra("year", year);
                    intent.putExtra("month", month);
                    intent.putExtra("day", day);
                    startActivityForResult(intent,0);
                }
            }
        });

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDay date = CalendarDay.today();
                int year = date.getYear();
                int month = date.getMonth()+1;
                int day = date.getDay();

                String db_date = String.format("%s-%s-%s",ZeroFill(year),ZeroFill(month),ZeroFill(day));

                int id = isDiaryExist(db_date);
                if(id != -1){
                    Intent intent = new Intent(getContext(), FullScreanActivity.class);
                    intent.putExtra("dbId",id);
                    startActivityForResult(intent,0);

                }else{
                    Intent intent = new Intent(getContext(), ResisterActivity.class);
                    intent.putExtra("year", year);
                    intent.putExtra("month", month);
                    intent.putExtra("day", day);
                    startActivityForResult(intent,0);
                }
            }
        });
        return root;
    }

    public int isDiaryExist(String date) {
        int id = -1;
        DiaryDbHelper dbHelper = DiaryDbHelper.getInstance(getContext());
        Cursor cursor = dbHelper.getReadableDatabase().query(DiaryContract.DiaryEntry.TABLE_NAME,

                null, DiaryContract.DiaryEntry.COLUMN_NAME_DATE + "=?", new String[]{date}, null, null, null);

        while (cursor.moveToNext()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

    private void draw_dot(){
        // 데이터 조회
        // 모든 데이터를 조회하여 날짜 데이터 추출
        DiaryDbHelper dbHelper = DiaryDbHelper.getInstance(getContext());
        Cursor cursor = dbHelper.getReadableDatabase().query(DiaryContract.DiaryEntry.TABLE_NAME,
                null, null, null, null, null, null);

        // 날짜 데이터를 CalendarDay 형태로 바꿔서 ArrayList인 dates에 추가
        ArrayList<CalendarDay> dates = new ArrayList<>();
        while (cursor.moveToNext()) {
            String[] date = cursor.getString(1).split("-");
            dates.add(CalendarDay.from(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2])));
        }
        cursor.close();
        // 달력에 점 찍는 라이브러리 호출
        calendarView.addDecorators(new EventDecorator(Color.RED,dates));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        draw_dot();
    }

    public String ZeroFill(int num){
        if(num>=0 && num <=9){
            return String.format("0%d", num);
        }else{
            return String.format("%d", num);
        }
    }

}