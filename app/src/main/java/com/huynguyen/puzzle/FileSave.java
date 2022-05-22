package com.huynguyen.puzzle;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.Date;

public class FileSave {
    private int id;
    private Bitmap img;
    private Long moves;
    private int seconds;
    private int minutes;
    private int chunkNumber;
    private int arrId[];
    private String date;

    public FileSave(int id, Bitmap img, Long moves, int seconds, int minutes, int chunkNumber, int[] arrId, String date) {
        this.id = id;
        this.img = img;
        this.moves = moves;
        this.seconds = seconds;
        this.minutes = minutes;
        this.chunkNumber = chunkNumber;
        this.arrId = arrId;
        this.date = date;
    }

    public FileSave() {
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public Long getMoves() {
        return moves;
    }

    public void setMoves(Long moves) {
        this.moves = moves;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getChunkNumber() {
        return chunkNumber;
    }

    public void setChunkNumber(int chunkNumber) {
        this.chunkNumber = chunkNumber;
    }

    public int[] getArrId() {
        return arrId;
    }

    public void setArrId(int[] arrId) {
        this.arrId = arrId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
