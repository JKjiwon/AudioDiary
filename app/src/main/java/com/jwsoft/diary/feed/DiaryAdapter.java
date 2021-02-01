package com.jwsoft.diary.feed;

import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.jwsoft.diary.DiaryItem;
import com.jwsoft.diary.R;

import java.io.File;
import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {
    private final List<DiaryItem> mDiaryItems;
    private DiaryViewClickListener mListener;

    public DiaryAdapter(List<DiaryItem> mDiaryItems) {
        this.mDiaryItems = mDiaryItems;
    }

    public void removeItem(int positon){
        mDiaryItems.remove(positon);
        notifyItemRemoved(positon);
        notifyItemRangeChanged(positon, mDiaryItems.size());
    }

    public void addItem(DiaryItem item) {
        mDiaryItems.add(item);
    }

    public void removeAllItems() {
        int size = mDiaryItems.size();
        mDiaryItems.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void setOnClickListener(DiaryViewClickListener listener){
        mListener = listener;
    }
    public interface DiaryViewClickListener{
        // 아이템 전체 클릭
        void onItemClicked(int positon);
        // play 버튼 클릭
        void onPlayButtonClicked(int position);
    }


    public DiaryItem getItem(int position){ return mDiaryItems.get(position); }

    // 뷰 홀더를 생성하는 부분, 레이아웃을 만드는 부분
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diary, parent, false);
        return new ViewHolder(view);
    }

    // 뷰 홀더에 데이터를 설정하는 부분
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiaryItem item = mDiaryItems.get(position);
        String date = item.getDate();

        holder.date.setText(date.replace("-","."));
        holder.contents.setText(item.getContents());

//        holder.contents.setText(item.getRecodePath());
        if (item.getPhotoPath().equals("none")) {
            holder.photo.setVisibility(View.GONE);
        } else {
            holder.photo.setVisibility(View.VISIBLE);
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mydiary/picture/" + item.getPhotoPath() + ".jpeg");
            Glide.with(holder.itemView.getContext())
                    .load(Uri.fromFile(f))
                    .apply(new RequestOptions().signature(new ObjectKey("signature string"))
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE))
                    .into(holder.photo);

//            holder.photo.setImageURI(Uri.fromFile(f));
        }
        if (item.getRecodePath().equals("none")) {
            holder.recode.setVisibility(View.GONE);
        } else {
            holder.recode.setVisibility(View.VISIBLE); // ??
        }

        if(mListener != null){
            final int pos = position;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClicked(pos);
                }
            });
            holder.recode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onPlayButtonClicked(pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDiaryItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        ImageView photo;
        TextView contents;
        ImageView recode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.item_diary_date_text);
            contents = itemView.findViewById(R.id.item_diary_contents_text);
            photo = (ImageView) itemView.findViewById(R.id.item_diary_photo_image);
            recode = (ImageView) itemView.findViewById(R.id.item_diary_recode_img);
        }
    }
}
