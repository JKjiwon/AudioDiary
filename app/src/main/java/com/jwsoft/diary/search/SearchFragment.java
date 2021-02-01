package com.jwsoft.diary.search;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwsoft.diary.DiaryContract;
import com.jwsoft.diary.DiaryDbHelper;
import com.jwsoft.diary.DiaryItem;
import com.jwsoft.diary.R;
import com.jwsoft.diary.feed.FullScreanActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements SearchDiaryAdapter.DiaryViewClickListener{

    private SearchDiaryAdapter adapter;
    private EditText searchEdit;
    private ImageView searchImg;
    private TextView noneResultText;
    private String searchContents;
    private List<DiaryItem> itemList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_search, container, false);

        searchEdit = (EditText) root.findViewById(R.id.frs_search_edit);
        searchImg = (ImageView) root.findViewById(R.id.frs_search_img);
        noneResultText = (TextView) root.findViewById(R.id.frg_search_none_text);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.frg_search_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        itemList = new ArrayList<>();
        recyclerView.setLayoutManager(layoutManager);

        adapter = new SearchDiaryAdapter(itemList);
        adapter.setOnClickListener(this);


        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchContents = searchEdit.getText().toString().trim();

                if (searchContents.equals("")) {
                    recyclerView.setVisibility(View.GONE);
                    noneResultText.setVisibility(View.VISIBLE);
                } else {
                    adapter.removeAllItems();
                    boolean isReuslt = searchData(searchContents);

                    if (isReuslt) {
                        recyclerView.setVisibility(View.VISIBLE);
                        noneResultText.setVisibility(View.GONE);
                        recyclerView.setAdapter(adapter);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        noneResultText.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    searchImg.callOnClick();
                    return true;
                }
                return false;
            }
        });

        return root;
    }


    public boolean searchData(String searchConents) {
        boolean isResult = false;

        DiaryDbHelper dbHelper = DiaryDbHelper.getInstance(getContext());
//        Cursor cursor = dbHelper.getReadableDatabase().query(DiaryContract.DiaryEntry.TABLE_NAME,
//                null,null,null,null,null, DiaryContract.DiaryEntry.COLUMN_NAME_DATE + " DESC");

        Cursor cursor = dbHelper.getReadableDatabase().query(DiaryContract.DiaryEntry.TABLE_NAME,
                null, DiaryContract.DiaryEntry.COLUMN_NAME_CONTENTS + " LIKE ? ", new String[]{"%" + searchConents + "%"},
                null, null, DiaryContract.DiaryEntry.COLUMN_NAME_DATE + " DESC");

        while (cursor.moveToNext()) {
            isResult = true;
            int id = cursor.getInt(0);
            String date = cursor.getString(1);
            String contents = cursor.getString(2);
            String photoPath = cursor.getString(3);
            String recodePath = cursor.getString(4);
            itemList.add(new DiaryItem(id, date, contents, photoPath, recodePath));
        }
        cursor.close();
        return isResult;
    }

    @Override
    public void onItemClicked(int positon) {
        Intent intent = new Intent(getContext(), FullScreanActivity.class);
        int dbId = adapter.getItem(positon).getId();
        intent.putExtra("dbId", dbId);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.removeAllItems();
    }
}