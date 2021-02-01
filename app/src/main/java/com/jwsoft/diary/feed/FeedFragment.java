package com.jwsoft.diary.feed;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jwsoft.diary.DiaryContract;
import com.jwsoft.diary.DiaryDbHelper;
import com.jwsoft.diary.DiaryItem;
import com.jwsoft.diary.R;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment implements DiaryAdapter.DiaryViewClickListener {
    private DiaryAdapter adapter;
    private List<DiaryItem> itemList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_feed, container, false);
        RecyclerView recyclerView = (RecyclerView)root.findViewById(R.id.frg_feed_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        itemList = new ArrayList<>();
        adapter = new DiaryAdapter(itemList);
        selectData();
        adapter.setOnClickListener(this);
        recyclerView.setAdapter(adapter);
        return root;
    }

    public void selectData(){
        DiaryDbHelper dbHelper = DiaryDbHelper.getInstance(getContext());
        Cursor cursor = dbHelper.getReadableDatabase().query(DiaryContract.DiaryEntry.TABLE_NAME,
                null,null,null,null,null, DiaryContract.DiaryEntry.COLUMN_NAME_DATE + " DESC");

        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String date = cursor.getString(1);
            String contents = cursor.getString(2);
            String photoPath = cursor.getString(3);
            String recodePath = cursor.getString(4);
            itemList.add(new DiaryItem(id, date, contents, photoPath, recodePath));
        }
        cursor.close();
    }

    @Override
    public void onItemClicked(int positon) {
        Intent intent = new Intent(getContext(), FullScreanActivity.class);
        int dbId = adapter.getItem(positon).getId();
        intent.putExtra("dbId", dbId);
        startActivity(intent);
    }

    @Override
    public void onPlayButtonClicked(int position) {

    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.removeAllItems();
        selectData();
    }
}

