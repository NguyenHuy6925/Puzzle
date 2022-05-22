package com.huynguyen.puzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Pieces {
    private int id;
    private Bitmap bitmap;

    public Pieces() {
    }

    public Pieces(int id, Bitmap bitmap) {
        this.id = id;
        this.bitmap = bitmap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
