package com.huynguyen.puzzle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.renderscript.Sampler;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;
import static com.huynguyen.puzzle.MainActivity.DATABASE_NAME;
import static com.huynguyen.puzzle.MainActivity.database;

public class SaveActivity extends AppCompatActivity {

    ListView lvSaved;
    SavedAdapter savedAdapter;
    ImageButton btnSave;
    ImageButton btnBack;
    Bitmap bm;
    ArrayList<Integer> idList;
    int chunkNumbers;
    Long pauseTimeFromSave =SystemClock.uptimeMillis();
    public static FileSave selectedFileSave;
    public static boolean clickFromMainActivity=false;
    int itemPositionToDeleteFromDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        addControls();
        setData();
        addEvents();
        showFilesaveToListview();
    }

    private void addControls() {
        lvSaved = (ListView) findViewById(R.id.lvSaved);
        registerForContextMenu(lvSaved);
        savedAdapter = new SavedAdapter(SaveActivity.this,R.layout.item_save);
        lvSaved.setAdapter(savedAdapter);
        btnSave = (ImageButton) findViewById(R.id.imgbtnsave);
        btnBack = findViewById(R.id.imgbtnBack);
        idList=new ArrayList<>();
        if(MainActivity.clickLoadButton==true){
            btnSave.setVisibility(View.GONE);
        }
        else
        {
            btnSave.setVisibility(View.VISIBLE);
        }
    }
    private void setData() {
        bm = PuzzleActivity.bm;
        if(MainActivity.clickLoadButton=true)
        chunkNumbers= MainActivity.chunkNumbers;
    }

    private void addEvents() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i =0;i<PuzzleActivity.pieceIdToSaveList.size();i++)
                {
                    idList.add(PuzzleActivity.pieceIdToSaveList.get(i));
                }
                newFileSave();
                showFilesaveToListview();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if(PuzzleActivity.clickFromPuzzel==true){
                     PuzzleActivity.pauseTime = PuzzleActivity.pauseTime+ (SystemClock.uptimeMillis()-pauseTimeFromSave);
                     finish();
                 }
                else if(clickFromMainActivity=true) {
                    Intent intent = new Intent(SaveActivity.this, MainActivity.class);
                    clickFromMainActivity=false;
                    startActivity(intent);
                    finish();
                }
               else
                   finish();
            }
        });
        lvSaved.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(MainActivity.clickLoadButton==true && PuzzleActivity.clickFromPuzzel==false) {
                    selectedFileSave = (FileSave) lvSaved.getItemAtPosition(position);
                    Intent intent = new Intent(SaveActivity.this,PuzzleActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        lvSaved.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                FileSave deleteFilesave = (FileSave) lvSaved.getItemAtPosition(position);
                itemPositionToDeleteFromDB = deleteFilesave.getId();
                return false;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        PuzzleActivity.clickFromPuzzel=false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.contextmenu_removeitem_db,menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuDelete:{
                database.delete("filesaveTable","id=?",new String[] {String.valueOf(itemPositionToDeleteFromDB)});
                showFilesaveToListview();
            }
        }
        return super.onContextItemSelected(item);
    }

    private void showFilesaveToListview() {
        MainActivity.database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        //Cursor cursor = database.rawQuery("select * from filesaveTable",null);
        Cursor cursor = MainActivity.database.query("filesaveTable",null,null,null,
                null,null,null);
        savedAdapter.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            byte[] image = cursor.getBlob(1);
            Bitmap bitmap = getImage(image);
            int minute = cursor.getInt(2);
            int second = cursor.getInt(3);
            Long move = Long.valueOf(cursor.getInt(4));
            String date = cursor.getString(5);
            int gird = cursor.getInt(6);
            String arrayposition = cursor.getString(7);
            int[] arr = imagepostionArr(arrayposition);
            FileSave fileSave = new FileSave(id,bitmap,move,second,minute,gird,arr,date);
            savedAdapter.add(fileSave);
        }
        cursor.close();
    }

    private void newFileSave() {
        Intent i = getIntent();
        int minute = i.getIntExtra("minutes",0);
        int second = i.getIntExtra("second",0);
        Long moves = i.getLongExtra("moves",0);
        String date = i.getStringExtra("date");
        int chunkNumbers = i.getIntExtra("chunknumber",0);
        ContentValues values = new ContentValues();
        imagesPositions();
        values.put("image",bitmapByteArray());
        values.put("minute",minute);
        values.put("second",second);
        values.put("move",moves);
        values.put("date",date);
        values.put("grid",chunkNumbers);
        values.put("arraypostion",imagesPositions());
        Long k =MainActivity.database.insert("filesaveTable",null,values);
        if(k>0) {
            Toast.makeText(this,"saved successfully",Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] bitmapByteArray() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapByteArr = bos.toByteArray();
        return bitmapByteArr;
    }

    private String imagesPositions() {
        String imagePositions = "";
        if(idList.size()>0) {
            for(int i =0;i<idList.size();i++) {
                imagePositions=imagePositions+idList.get(i)+",";
            }
            return imagePositions;
        }
        else {
            Toast.makeText(this,"idlist empty",Toast.LENGTH_SHORT).show();
        }
        return imagePositions;
    }

    private static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    private int[] imagepostionArr(String str) {
        String[] array = str.split("\\,");
        int[] intArr = new int[array.length];
        for(int i =0;i<array.length;i++) {
            intArr[i] = Integer.parseInt(array[i]);
        }
        return intArr;
    }
}