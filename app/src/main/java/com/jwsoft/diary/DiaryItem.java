package com.jwsoft.diary;

import java.io.Serializable;

public class DiaryItem implements Serializable {
    private int id;
    private String date;
    private String contents;
    private String photoPath;
    private String recodePath;

    public DiaryItem(int id, String date, String contents, String photoPath, String recodePath) {
        this.id = id;
        this.date = date;
        this.contents = contents;
        this.photoPath = photoPath;
        this.recodePath = recodePath;
    }
    public DiaryItem() {
    }

    public int getId() {
        return id;
    }

    public DiaryItem setId(int id) {
        this.id = id;
        return this;
    }

    public String getDate() {
        return date;
    }

    public DiaryItem setDate(String date) {
        this.date = date;
        return this;
    }

    public String getContents() {
        return contents;
    }

    public DiaryItem setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public DiaryItem setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
        return this;
    }

    public String getRecodePath() {
        return recodePath;
    }

    public DiaryItem setRecodePath(String recodePath) {
        this.recodePath = recodePath;
        return this;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DiaryItem{");
        sb.append("id=").append(id);
        sb.append(", date='").append(date).append('\'');
        sb.append(", contents='").append(contents).append('\'');
        sb.append(", photoPath='").append(photoPath).append('\'');
        sb.append(", recodePath='").append(recodePath).append('\'');
        sb.append('}');
        return sb.toString();
    }
    public void clear(){
        this.id = -1;
        this.date = null;
        this.contents = null;
        this.photoPath = null;
        this.recodePath = null;
    }
}

